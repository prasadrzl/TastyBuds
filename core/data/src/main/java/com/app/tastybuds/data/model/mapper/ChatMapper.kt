package com.app.tastybuds.data.model.mapper

import com.app.tastybuds.data.model.ChatMessageDto
import com.app.tastybuds.domain.model.ChatMessage
import com.app.tastybuds.domain.model.SenderType

fun ChatMessageDto.toDomain(): ChatMessage = ChatMessage(
    id = id,
    orderId = orderId,
    senderId = senderId,
    senderType = SenderType.fromRaw(senderType),
    content = content,
    timestamp = timestamp
)

fun ChatMessage.toDto(): ChatMessageDto = ChatMessageDto(
    id = id,
    orderId = orderId,
    senderId = senderId,
    senderType = senderType.name,
    content = content,
    timestamp = timestamp
)
