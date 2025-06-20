package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateOrderRequest(
    @Json(name = "user_id")
    val userId: String,
    @Json(name = "restaurant_id")
    val restaurantId: String?,
    @Json(name = "order_items")
    val orderItems: List<OrderItemRequest>,
    @Json(name = "subtotal")
    val subtotal: Double,
    @Json(name = "delivery_fee")
    val deliveryFee: Double = 2.0,
    @Json(name = "promotion_discount")
    val promotionDiscount: Double = 0.0,
    @Json(name = "total_amount")
    val totalAmount: Double,
    @Json(name = "payment_method")
    val paymentMethod: String = "e-wallet",
    @Json(name = "delivery_address")
    val deliveryAddress: DeliveryAddress,
    @Json(name = "delivery_lat")
    val deliveryLat: Double?,
    @Json(name = "delivery_lng")
    val deliveryLng: Double?,
    @Json(name = "estimated_delivery_time")
    val estimatedDeliveryTime: Int = 20,
    @Json(name = "special_notes")
    val specialNotes: String?
)

@JsonClass(generateAdapter = true)
data class OrderItemRequest(
    @Json(name = "menu_item_id")
    val menuItemId: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "image")
    val image: String?,
    @Json(name = "base_price")
    val basePrice: Double,
    @Json(name = "quantity")
    val quantity: Int,
    @Json(name = "customizations")
    val customizations: OrderItemCustomization,
    @Json(name = "item_total")
    val itemTotal: Double,
    @Json(name = "notes")
    val notes: String?
)
