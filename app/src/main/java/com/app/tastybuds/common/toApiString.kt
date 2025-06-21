package com.app.tastybuds.common

import com.app.tastybuds.data.model.OrderStatus

fun OrderStatus.toApiString(): String {
    return when (this) {
        OrderStatus.PENDING -> "pending"
        OrderStatus.CONFIRMED -> "confirmed"
        OrderStatus.PREPARING -> "preparing"
        OrderStatus.READY -> "ready"
        OrderStatus.OUT_FOR_DELIVERY -> "out_for_delivery"
        OrderStatus.DELIVERED -> "delivered"
        OrderStatus.CANCELLED -> "cancelled"
    }
}