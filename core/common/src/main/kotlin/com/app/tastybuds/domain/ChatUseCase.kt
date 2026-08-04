package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.ChatEvent
import com.app.tastybuds.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    fun connect(orderId: String, userId: String): Flow<ChatEvent> =
        repository.connect(orderId, userId)

    fun sendMessage(content: String): Boolean {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) return false
        return repository.send(trimmed)
    }

    fun disconnect() = repository.disconnect()
}
