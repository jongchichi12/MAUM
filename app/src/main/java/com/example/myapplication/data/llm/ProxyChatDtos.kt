package com.example.myapplication.data.llm

import com.squareup.moshi.Json

data class ProxyChatRequest(
    @Json(name = "messages") val messages: List<ProxyMessageDto>
)

data class ProxyMessageDto(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String
)

data class ProxyChatResponse(
    @Json(name = "reply") val reply: String?
)
