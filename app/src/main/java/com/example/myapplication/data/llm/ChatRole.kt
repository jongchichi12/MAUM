package com.example.myapplication.data.llm

enum class ChatRole(val apiValue: String) {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");
}
