package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessageDto(
    @Json(name = "id") val id: String = "",
    @Json(name = "orderId") val orderId: String = "",
    @Json(name = "senderId") val senderId: String = "",
    @Json(name = "senderType") val senderType: String = "SYSTEM",
    @Json(name = "content") val content: String = "",
    @Json(name = "timestamp") val timestamp: Long = 0L
)
