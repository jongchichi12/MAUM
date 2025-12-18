package com.example.myapplication.ui.counsel.chat

import android.os.SystemClock
import android.util.Log
import com.example.myapplication.data.llm.ChatMessage
import com.example.myapplication.data.llm.ChatRole
import com.example.myapplication.data.llm.ProxyChatClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * AI 상담 화면 전용 간단한 상태/전송 관리기.
 * Activity에서 버튼 클릭 시 이 컨트롤러를 통해 전송하도록 구성한다.
 */
class AICounselingController(
    private val primaryClient: ProxyChatClient?,
    private val fallbackClient: ProxyChatClient,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())
 ) {

    var onNewMessage: ((ChatMessage) -> Unit)? = null
    var onError: ((Throwable) -> Unit)? = null
    var onMetaUpdated: ((riskLevel: String) -> Unit)? = null
    private var lastSendAt: Long = 0L
    private val minIntervalMs: Long = 10_000L          // 10초 쿨타임
    private var blockUntilMs: Long = 0L
    private val blockAfter429Ms: Long = 10_000L        // 429 받으면 10초 차단(테스트 편의를 위해 짧게)
    private var useFallback: Boolean = false           // 429 이후 스텁 모드로 전환
    private val cache: MutableList<ChatMessage> = mutableListOf()

    fun sendUserMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return
        val now = SystemClock.elapsedRealtime()
        if (now < blockUntilMs) {
            onError?.invoke(IllegalStateException("잠시 후 다시 시도해 주세요. (쿨타임/오프라인 모드)"))
            return
        }
        if (now - lastSendAt < minIntervalMs) {
            onError?.invoke(IllegalStateException("조금만 천천히 보내주세요."))
            return
        }
        lastSendAt = now

        val userMessage = ChatMessage(role = ChatRole.USER, content = trimmed)
        cache.add(userMessage)
        onNewMessage?.invoke(userMessage)

        scope.launch {
            val client = if (useFallback) fallbackClient else primaryClient ?: fallbackClient
            val result = client.send(cache.toList())
            result
                .onSuccess { reply ->
                    val assistantMsg = ChatMessage(
                        role = ChatRole.ASSISTANT,
                        content = reply
                    )
                    cache.add(assistantMsg)
                    onNewMessage?.invoke(assistantMsg)
                }
                .onFailure { error ->
                    // 429일 때만 쿨타임/스텁 전환, 그 외에는 즉시 재시도 가능하게 둔다.
                    if (error is retrofit2.HttpException && error.code() == 429) {
                        blockUntilMs = SystemClock.elapsedRealtime() + blockAfter429Ms
                        useFallback = true
                        Log.w("AICounselingController", "429 received, switching to fallback mode", error)
                    } else {
                        Log.w("AICounselingController", "LLM call failed, keeping primary", error)
                    }
                    val fallback = ChatMessage(
                        role = ChatRole.ASSISTANT,
                        content = "지금은 연결이 어려워요. 잠시 후 다시 시도해 주시면 감사하겠습니다."
                    )
                    cache.add(fallback)
                    onNewMessage?.invoke(fallback)
                }
        }
    }

    fun seedHistory(messages: List<ChatMessage>) {
        cache.clear()
        cache.addAll(messages)
    }
}
