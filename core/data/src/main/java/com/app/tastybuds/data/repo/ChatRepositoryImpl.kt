package com.app.tastybuds.data.repo

import com.app.tastybuds.core.data.BuildConfig
import com.app.tastybuds.data.model.ChatMessageDto
import com.app.tastybuds.data.model.mapper.toDomain
import com.app.tastybuds.domain.model.ChatConnectionState
import com.app.tastybuds.domain.model.ChatEvent
import com.app.tastybuds.domain.model.SenderType
import com.app.tastybuds.domain.repository.ChatRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val dataSource: ChatWebSocketDataSource,
    private val tokenStore: AuthTokenStore,
    moshi: Moshi
) : ChatRepository {

    private val adapter = moshi.adapter(ChatMessageDto::class.java)
    private var orderId: String = ""
    private var userId: String = ""

    override fun connect(orderId: String, userId: String): Flow<ChatEvent> {
        this.orderId = orderId
        this.userId = userId
        val token = tokenStore.getToken().orEmpty()
        val url = "${BuildConfig.WS_BASE_URL}/ws/chat?orderId=$orderId&userId=$userId&token=$token"

        return dataSource.connect(url)
            .mapNotNull { frame -> frame.toChatEventOrNull() }
            .onStart { emit(ChatEvent.ConnectionChanged(ChatConnectionState.CONNECTING)) }
    }

    override fun send(content: String): Boolean {
        val dto = ChatMessageDto(
            id = UUID.randomUUID().toString(),
            orderId = orderId,
            senderId = userId,
            senderType = SenderType.CUSTOMER.name,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        return dataSource.send(adapter.toJson(dto))
    }

    override fun disconnect() = dataSource.disconnect()

    /** Null for an unparseable inbound message (dropped); every other frame maps to an event. */
    private fun SocketFrame.toChatEventOrNull(): ChatEvent? = when (this) {
        is SocketFrame.Open ->
            ChatEvent.ConnectionChanged(ChatConnectionState.CONNECTED)

        is SocketFrame.Message ->
            runCatching { adapter.fromJson(text) }.getOrNull()
                ?.let { ChatEvent.MessageReceived(it.toDomain()) }

        is SocketFrame.Closed ->
            ChatEvent.ConnectionChanged(ChatConnectionState.DISCONNECTED)

        is SocketFrame.Failure ->
            ChatEvent.ConnectionChanged(ChatConnectionState.ERROR)
    }
}
