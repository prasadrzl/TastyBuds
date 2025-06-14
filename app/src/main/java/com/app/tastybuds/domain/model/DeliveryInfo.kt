package com.app.tastybuds.domain.model

data class DeliveryInfo(
    val deliveryTime: String,
    val deliveryFee: Float,
    val minimumOrder: Float?,
    val distance: String
)