package com.app.tastybuds.data.model

data class UpdateOrderStatusRequest(
    val status: String,
    val updatedAt: String = ""
)