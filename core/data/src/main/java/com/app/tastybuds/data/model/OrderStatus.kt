package com.app.tastybuds.data.model

import com.squareup.moshi.Json

enum class OrderStatus(val displayName: String) {
    @Json(name = "pending")
    PENDING("Pending"),

    @Json(name = "confirmed")
    CONFIRMED("Confirmed"),

    @Json(name = "preparing")
    PREPARING("Preparing"),

    @Json(name = "ready")
    READY("Ready"),

    @Json(name = "out_for_delivery")
    OUT_FOR_DELIVERY("Out for Delivery"),

    @Json(name = "delivered")
    DELIVERED("Delivered"),

    @Json(name = "cancelled")
    CANCELLED("Cancelled")
}
