package com.app.tastybuds.domain.model

data class ChatMessage(
    val id: String,
    val orderId: String,
    val senderId: String,
    val senderType: SenderType,
    val content: String,
    val timestamp: Long
)

enum class SenderType {
    CUSTOMER,
    DRIVER,
    SYSTEM;

    companion object {
        fun fromRaw(raw: String?): SenderType =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: SYSTEM
    }
}

/** Lifecycle of the chat WebSocket connection, surfaced to the UI. */
enum class ChatConnectionState {
    CONNECTING,
    CONNECTED,
    DISCONNECTED,
    ERROR
}

/** Events emitted by the chat stream while a connection is open. */
sealed interface ChatEvent {
    data class MessageReceived(val message: ChatMessage) : ChatEvent
    data class ConnectionChanged(val state: ChatConnectionState) : ChatEvent
}
