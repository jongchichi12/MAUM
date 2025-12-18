import os
import re
import json
import requests
from fastapi import FastAPI
from pydantic import BaseModel
from fastapi.responses import JSONResponse

OLLAMA_URL = "http://localhost:11434/api/chat"
MODEL = os.getenv("OLLAMA_MODEL", "llama3.2:1b")

class Message(BaseModel):
    role: str
    content: str

class ChatRequest(BaseModel):
    messages: list[Message]

class ChatResponse(BaseModel):
    reply: str | None

app = FastAPI()

# 상담/앱 소개 지침
SYSTEM_INSTRUCTIONS = (
    """역할:
나는 입양 경험이 있는 아이를 돌보는 새로운 부모처럼나
입양과 정체성, 감정에 대한 이야기를 조용히 들어주는 정서적 상담자다.

대상: 사용자는 입양아이며, 자신의 감정·생각·혼란을 안전하게 털어놓고 싶어 한다.

목표:
사용자가 판단받지 않고 존중받는 느낌을 받도록 돕는다.
문제를 해결하려 들지 말고, 감정을 함께 머무르듯 들어준다.

톤/언어:
- 반드시 한국어 존댓말만 사용
- 따뜻하고 차분한 말투
- 전문가·교수·상담사 같은 말투 금지
- 설명조, 안내조, 보고서 문장 금지

길이/형식 (매우 중요):
- 전체 2~4문장
- 최대 2단락 (보통 1단락 권장)
- 한 문장은 짧고 말하듯 자연스럽게
- 절대 길게 쓰지 말 것

내용 구조 (항상 이 순서):
1. 사용자의 감정을 한 문장으로 공감·반영
2. 그 감정이 자연스럽다는 짧은 인정
3. 부담 없는 질문 1개 (선택형 질문 금지, 1개만)

금지:
- 진단, 해결책 제시, 조언, 교훈
- “~해야 해요”, “~하는 게 좋아요”
- 목록, 번호 매기기, 설명 늘어놓기
- 입양 일반론, 사회적 이야기 확장
- 영어, 숫자, 특수기호 사용

앱 기능 언급:
- 사용자가 도움을 원한다고 명확히 말할 때만
- 한 문장으로만 간단히 언급
- 예: “원하시면 MAUM 상담 기능으로 이어드릴 수도 있어요.”

위기 상황(자해·자살·학대):
- 공감 → AI 한계 한 문장 → 도움 연락처 안내
- 절대 설득·훈계·책임 전가 금지"""
)

# 답변 톤 예시
GUIDE_EXAMPLES = [
    {
        "role": "assistant",
        "content": "그런 마음이 들 수 있었겠어요. 지금 마음속에서 가장 크게 느껴지는 감정이 있다면 어떤 걸까요?"
    },
    {
        "role": "assistant",
        "content": "혼자서 많이 버텨오신 것 같아요. 이 순간에 가장 힘든 부분은 무엇인지 조금만 들려주실래요?"
    },
    {
        "role": "assistant",
        "content": "마음이 복잡해질 만큼 여러 생각이 겹친 것 같아요. 요즘 가장 자주 떠오르는 생각이 있나요?"
    },
    {
        "role": "assistant",
        "content": "말로 꺼내기 어려운 감정이었을 것 같아요. 지금 이 대화에서 꼭 나누고 싶은 게 하나 있다면 무엇일까요?"
    },
    {
        "role": "assistant",
        "content": "정체성에 대해 흔들리는 느낌이 많이 힘드셨을 것 같아요. 그런 혼란은 입양 경험 속에서 자연스럽게 생길 수 있어요. 요즘 가장 헷갈리는 부분은 어떤 건가요?"
    }
]

def sanitize(text: str) -> str:
    # 한글/숫자/기본 문장부호만 허용, 나머지는 제거
    return "".join(ch for ch in text if re.match(r"[가-힣0-9 .,?!\"'()\\-]", ch))

@app.post("/chat", response_model=ChatResponse)
def chat(body: ChatRequest):
    resp = None
    try:
        messages = (
            [{"role": "system", "content": SYSTEM_INSTRUCTIONS}]
            + GUIDE_EXAMPLES
            + [{"role": m.role, "content": sanitize(m.content)} for m in body.messages]
        )

        resp = requests.post(
            OLLAMA_URL,
            json={
                "model": MODEL,
                "messages": messages,
                "stream": False,  # 스트리밍 끔
            },
            timeout=60,
        )
        resp.raise_for_status()
        text = resp.text
        try:
            data = resp.json()
        except Exception:
            data = None
            for line in text.splitlines()[::-1]:
                line = line.strip()
                if not line:
                    continue
                try:
                    data = json.loads(line)
                    break
                except Exception:
                    continue
            if data is None:
                raise ValueError(f"Failed to parse Ollama response: {text[:500]}")

        if "message" in data and isinstance(data["message"], dict):
            reply = data["message"].get("content")
        else:
            reply = data.get("choices", [{}])[0].get("message", {}).get("content")
        return {"reply": reply}
    except Exception as e:
        print("ERROR calling Ollama:", repr(e))
        try:
            import traceback
            traceback.print_exc()
        except Exception:
            pass
        if resp is not None:
            try:
                print("Ollama response text:", resp.text)
            except Exception:
                pass
        return JSONResponse(
            status_code=500,
            content={"reply": None, "error": str(e)},
        )
