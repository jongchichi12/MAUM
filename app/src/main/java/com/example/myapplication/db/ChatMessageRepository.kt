package com.example.myapplication.db

class ChatMessageRepository(
    private val dao: ChatMessageDao
) {
    suspend fun getAll(): List<ChatMessageEntity> = dao.getAll()
    suspend fun add(role: String, content: String, createdAt: Long = System.currentTimeMillis()) {
        dao.insert(ChatMessageEntity(role = role, content = content, createdAt = createdAt))
    }
    suspend fun clear() = dao.clear()
}
