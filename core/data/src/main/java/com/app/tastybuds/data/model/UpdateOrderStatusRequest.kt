package com.app.tastybuds.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateOrderStatusRequest(
    val status: String = "",
    val updatedAt: String = ""
)
