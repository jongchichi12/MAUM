package com.example.myapplication.data.llm

/**
 * 네트워크 실패 시 사용할 스텁 클라이언트.
 */
class ProxyChatStub : ProxyChatClient(
    baseUrl = "http://stub",
    apiKey = null
) {
    suspend fun sendStub(messages: List<ChatMessage>): Result<String> {
        val lastUser = messages.lastOrNull { it.role == ChatRole.USER }?.content.orEmpty()
        val reply = if (lastUser.isBlank()) {
            "어떤 이야기든 괜찮아요. 편하게 말씀해 주세요."
        } else {
            "들려주셔서 고마워요.\n말씀하신 내용: \"$lastUser\""
        }
        return Result.success(reply)
    }

    override suspend fun send(messages: List<ChatMessage>): Result<String> = sendStub(messages)
}
