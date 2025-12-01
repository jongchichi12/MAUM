import os
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

# 상담/앱 소개 기본 지침을 시스템 메시지로 추가
SYSTEM_INSTRUCTIONS = (
    "당신은 입양인/입양 부모를 위해 공감적이고 안전한 상담을 제공하는 조력자입니다. "
    "민감한 주제에서는 비판 없이 공감하고, 긴급 위험(자/타해)이 보이면 즉시 전문기관 상담을 권유하세요. "
    "앱 정보: MAUM은 입양가족을 위한 상담, 뿌리찾기 지원(신청서 작성/기관 정보), 그리고 기록 보기 기능을 제공합니다. "
    "대화는 짧고 따뜻하게, 사용자가 원하는 속도를 존중하며 진행하세요."
)

@app.post("/chat", response_model=ChatResponse)
def chat(body: ChatRequest):
    try:
        # 시스템 지침을 맨 앞에 삽입
        messages = [
            {"role": "system", "content": SYSTEM_INSTRUCTIONS}
        ] + [
            {"role": m.role, "content": m.content}
            for m in body.messages
        ]

        resp = requests.post(
            OLLAMA_URL,
            json={
                "model": MODEL,
                "messages": messages,
                # 스트리밍 말고 한 번에 응답 받기
                "stream": False,
            },
            timeout=60,
        )
        resp.raise_for_status()
        text = resp.text
        try:
            data = resp.json()
        except Exception:
            # 여러 줄로 올 수 있으니 뒤에서부터 첫 번째 유효 JSON을 파싱
            data = None
            for line in text.splitlines()[::-1]:
                line = line.strip()
                if not line:
                    continue
                try:
                    data = requests.models.json.loads(line)
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
        # 서버 콘솔에 상세 로그 출력
        print("ERROR calling Ollama:", repr(e))
        try:
            print("Ollama response text:", resp.text)
        except Exception:
            pass
        return JSONResponse(
            status_code=500,
            content={"reply": None, "error": str(e)},
        )
