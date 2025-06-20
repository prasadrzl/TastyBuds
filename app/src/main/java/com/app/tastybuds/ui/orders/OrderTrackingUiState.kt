package com.app.tastybuds.ui.orders

import com.app.tastybuds.data.model.Order

data class OrderTrackingUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val order: Order? = null,
    val customerName: String = "Customer",
    val deliveryAddress: String = "",
    val restaurantLocation: Pair<Double, Double>? = null,
    val deliveryLocation: Pair<Double, Double>? = null,
    val distance: String = "",
    val estimatedDeliveryTime: String = "",
    val actualDeliveryTime: String = "",
    val progressSteps: List<OrderProgressStep> = emptyList()
)

data class OrderProgressStep(
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val isActive: Boolean = false
)

