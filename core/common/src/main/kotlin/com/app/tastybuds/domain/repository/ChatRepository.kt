package com.app.tastybuds.domain.repository

import com.app.tastybuds.domain.model.ChatEvent
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun connect(orderId: String, userId: String): Flow<ChatEvent>
    fun send(content: String): Boolean
    fun disconnect()
}
