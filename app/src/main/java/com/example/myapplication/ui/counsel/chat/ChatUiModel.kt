package com.example.myapplication.ui.counsel.chat

import com.example.myapplication.data.llm.ChatMessage
import com.example.myapplication.data.llm.ChatRole

/**
 * UI 전용 변환/표시 상태를 담는다. 필요 시 바인딩/어댑터에서 사용.
 */
data class ChatUiModel(
    val message: ChatMessage,
    val isMine: Boolean = message.role == ChatRole.USER
)
