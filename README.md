# MAUM 실행/개발 가이드

## 1) 의존성 설치 (프로젝트 루트)
```bash
pip install fastapi uvicorn requests pydantic
```

## 2) Ollama 설치/모델 준비
- 다운로드: https://ollama.com/download (macOS dmg) 또는 스크립트
  ```bash
  curl -fsSL https://ollama.com/install.sh | sh
  ```
- 기본 모델 받기:
  ```bash
ollama pull mistral:7b-instruct
  ```
- 설치된 모델 확인/삭제:
  ```bash
ollama list
ollama rm <모델이름>      # 예: ollama rm mistral:7b-instruct"
  ```

## 3) 서버 실행 (프록시 → Ollama)
```bash
ollama serve   # Ollama 데몬 기동
```
```bash
OLLAMA_MODEL="mistral:7b-instruct" \
python -m uvicorn server:app --host 0.0.0.0 --port 8080
```
- 기본 Ollama 엔드포인트: `http://127.0.0.1:11434/api/chat`
- 다른 모델을 쓰려면 `ollama pull <모델>` 후 `OLLAMA_MODEL=<모델>`로 실행.

## 4) 포트/환경변수 설정 예시
- 프록시 포트: `8080` (에뮬레이터는 `http://10.0.2.2:8080/`로 접근)
- 환경변수:
  - `OLLAMA_MODEL=mistral:7b-instruct` (현재 기본)
  - 필요 시 `OLLAMA_URL`로 Ollama 주소 변경(기본 `http://127.0.0.1:11434/api/chat`)

## 5) 서버 동작 확인 (옵션)
```bash
curl -v -H "Content-Type: application/json" \
  -d '{"messages":[{"role":"user","content":"안녕"}]}' \
  http://127.0.0.1:8080/chat
```
`{"reply":"..."}` 응답이 오면 정상.

## 6) 앱 연동 주의사항
- `local.properties` 예:
  ```
  PROXY_BASE_URL=http://10.0.2.2:8080/
  PROXY_API_KEY=
  ```
  (에뮬레이터에서 호스트 포트 8080 접근)
- 매니페스트에 `android:usesCleartextTraffic="true"` 적용됨.
- 프록시가 내려가면 앱이 자동으로 스텁(더미) 응답으로 폴백.

## 7) 데이터 저장
- AI 채팅: Room(`maum.db`)의 `chat_messages` 테이블에 저장/복원.
- 뿌리찾기 신청서: Room(`SupportApplicationEntity`) + Firestore `rootRequests` 컬렉션에 동시 저장.

## 8) 기타 실행 관련 주의사항
- 프록시(uvicorn)와 Ollama 데몬을 켜둔 상태에서 앱을 실행해야 LLM 응답을 받습니다.
- 디버그 프로세스 종료 시 Inspector에서 DB가 `closed`로 보일 수 있음(앱 재실행 후 새로고침).
- 모델 교체 시: 새 모델 `ollama pull` → `OLLAMA_MODEL=<모델>`로 프록시 재실행.

## 9) Android 빌드/실행 메모
- JDK/Gradle: 프로젝트에 포함된 래퍼로 `./gradlew assembleDebug` 실행 가능.
- 최소/타겟 SDK: minSdk 24, targetSdk 36.
- 디버그 모드로 실행하면 Room DB(`maum.db`)를 Database Inspector에서 조회 가능.
- HTTP 프록시 접근을 위해 `android:usesCleartextTraffic="true"` 설정되어 있음.

## 10) Firebase 메모
- `app/google-services.json` 포함, Firestore `rootRequests` 컬렉션에 뿌리찾기 신청서 저장.
- 로컬 DB를 지워도 Firestore 데이터는 남으므로 콘솔에서 관리/삭제.
- 앱 삭제/재설치 시 로컬 DB만 초기화됨.
- 콘솔 빠른 링크: https://console.firebase.google.com/u/0/project/maum-f4280/firestore/databases/-default-/data/~2FrootRequests

## 11) 로컬 DB(Room) 메모
- DB 버전: 2 (`maum.db`).
- 테이블: `chat_messages`(AI 대화), `SupportApplicationEntity`(뿌리찾기 신청서).
- 이전 버전에서 올라올 때는 마이그레이션 필요. 테스트 환경에서는 앱 삭제 후 재설치로 초기화 가능.

## 12) LLM/프록시 흐름 요약
- 실행 순서: `ollama serve` → `OLLAMA_MODEL=... python -m uvicorn server:app --host 0.0.0.0 --port 8080` → 앱 실행.
- 기본 모델: `mistral:7b-instruct` (README 기준); 다른 모델은 `ollama pull <모델>` 후 `OLLAMA_MODEL=<모델>`.
- 프록시/ollama가 꺼져 있으면 앱이 스텁(더미) 응답으로 폴백.
