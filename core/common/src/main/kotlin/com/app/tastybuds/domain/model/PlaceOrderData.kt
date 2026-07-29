package com.app.tastybuds.domain.model

// Domain command produced by OrderUseCase and consumed by OrderRepository. The data layer
// maps this to the wire DTO (CreateOrderRequest) — the domain never sees the DTO.
data class PlaceOrderData(
    val userId: String,
    val restaurantId: String?,
    val items: List<CartItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val promotionDiscount: Double,
    val totalAmount: Double,
    val paymentMethod: String,
    val deliveryAddress: UserAddress,
    val estimatedDeliveryTime: Int,
    val specialNotes: String?
)
