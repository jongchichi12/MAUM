package com.example.myapplication.data.llm

import retrofit2.http.Body
import retrofit2.http.POST

interface ProxyChatApi {
    @POST("chat")
    suspend fun sendChat(@Body body: ProxyChatRequest): ProxyChatResponse
}
