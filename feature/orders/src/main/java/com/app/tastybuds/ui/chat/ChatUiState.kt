package com.app.tastybuds.ui.chat

import com.app.tastybuds.domain.model.ChatConnectionState
import com.app.tastybuds.domain.model.ChatMessage

data class ChatUiState(
    val currentUserId: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val connectionState: ChatConnectionState = ChatConnectionState.DISCONNECTED,
    val input: String = "",
    val isSending: Boolean = false
)
