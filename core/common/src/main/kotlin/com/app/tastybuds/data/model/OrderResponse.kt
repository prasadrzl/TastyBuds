package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Every field defaults so an omitted key (backend uses non_null inclusion) never crashes
// Moshi deserialization. OrderItemRequest is also used as a request body — the app always
// sets the values, so defaults here are harmless there.
@JsonClass(generateAdapter = true)
data class OrderItemRequest(
    @Json(name = "menu_item_id")
    val menuItemId: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "image")
    val image: String? = null,
    @Json(name = "base_price")
    val basePrice: Double = 0.0,
    @Json(name = "quantity")
    val quantity: Int = 0,
    @Json(name = "customizations")
    val customizations: OrderItemCustomization = OrderItemCustomization(),
    @Json(name = "item_total")
    val itemTotal: Double = 0.0,
    @Json(name = "notes")
    val notes: String? = null
)

@JsonClass(generateAdapter = true)
data class Order(
    @Json(name = "id")
    val id: String = "",
    @Json(name = "user_id")
    val userId: String = "",
    @Json(name = "restaurant_id")
    val restaurantId: String? = null,
    @Json(name = "restaurant_name")
    val restaurantName: String? = null,
    @Json(name = "status")
    val status: OrderStatus = OrderStatus.PENDING,
    @Json(name = "order_items")
    val orderItems: List<OrderItemRequest> = emptyList(),
    @Json(name = "subtotal")
    val subtotal: Double = 0.0,
    @Json(name = "delivery_fee")
    val deliveryFee: Double = 0.0,
    @Json(name = "promotion_discount")
    val promotionDiscount: Double = 0.0,
    @Json(name = "total_amount")
    val totalAmount: Double = 0.0,
    @Json(name = "payment_method")
    val paymentMethod: String = "",
    @Json(name = "payment_status")
    val paymentStatus: String = "",
    @Json(name = "delivery_address")
    val deliveryAddress: DeliveryAddress = DeliveryAddress(),
    @Json(name = "delivery_lat")
    val deliveryLat: Double? = null,
    @Json(name = "delivery_lng")
    val deliveryLng: Double? = null,
    @Json(name = "estimated_delivery_time")
    val estimatedDeliveryTime: Int = 0,
    @Json(name = "actual_delivery_time")
    val actualDeliveryTime: String? = null,
    @Json(name = "special_notes")
    val specialNotes: String? = null,
    @Json(name = "created_at")
    val createdAt: String = "",
    @Json(name = "updated_at")
    val updatedAt: String? = null
)
