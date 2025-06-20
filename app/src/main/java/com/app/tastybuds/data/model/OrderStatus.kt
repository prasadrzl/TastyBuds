package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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

@JsonClass(generateAdapter = true)
data class UserAddress(
    @Json(name = "id")
    val id: String,
    @Json(name = "user_id")
    val userId: String,
    @Json(name = "address_line")
    val addressLine: String,
    @Json(name = "street")
    val street: String?,
    @Json(name = "city")
    val city: String?,
    @Json(name = "state")
    val state: String?,
    @Json(name = "postal_code")
    val postalCode: String?,
    @Json(name = "country")
    val country: String?,
    @Json(name = "latitude")
    val latitude: Double?,
    @Json(name = "longitude")
    val longitude: Double?,
    @Json(name = "address_type")
    val addressType: String?,
    @Json(name = "address_label")
    val addressLabel: String?,
    @Json(name = "is_default")
    val isDefault: Boolean,
    @Json(name = "delivery_instructions")
    val deliveryInstructions: String?,
    @Json(name = "created_at")
    val createdAt: String?
)

@JsonClass(generateAdapter = true)
data class DeliveryAddress(
    @Json(name = "address_line")
    val addressLine: String,
    @Json(name = "street")
    val street: String?,
    @Json(name = "city")
    val city: String?,
    @Json(name = "state")
    val state: String?,
    @Json(name = "postal_code")
    val postalCode: String?,
    @Json(name = "country")
    val country: String = "Singapore",
    @Json(name = "latitude")
    val latitude: Double?,
    @Json(name = "longitude")
    val longitude: Double?,
    @Json(name = "delivery_instructions")
    val deliveryInstructions: String?
)


@JsonClass(generateAdapter = true)
data class OrderItemCustomization(
    @Json(name = "size")
    val size: OrderItemSize?,
    @Json(name = "toppings")
    val toppings: List<OrderItemTopping>,
    @Json(name = "spice_level")
    val spiceLevel: OrderItemSpiceLevel?
)

@JsonClass(generateAdapter = true)
data class OrderItemSize(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "additional_price")
    val additionalPrice: Double
)

@JsonClass(generateAdapter = true)
data class OrderItemTopping(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "price")
    val price: Double
)

@JsonClass(generateAdapter = true)
data class OrderItemSpiceLevel(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "level")
    val level: Int
)


@JsonClass(generateAdapter = true)
data class Order(
    @Json(name = "id")
    val id: String,
    @Json(name = "user_id")
    val userId: String,
    @Json(name = "restaurant_id")
    val restaurantId: String?,
    @Json(name = "status")
    val status: OrderStatus,
    @Json(name = "order_items")
    val orderItems: List<OrderItemRequest>,
    @Json(name = "subtotal")
    val subtotal: Double,
    @Json(name = "delivery_fee")
    val deliveryFee: Double,
    @Json(name = "promotion_discount")
    val promotionDiscount: Double,
    @Json(name = "total_amount")
    val totalAmount: Double,
    @Json(name = "payment_method")
    val paymentMethod: String,
    @Json(name = "payment_status")
    val paymentStatus: String,
    @Json(name = "delivery_address")
    val deliveryAddress: DeliveryAddress,
    @Json(name = "delivery_lat")
    val deliveryLat: Double?,
    @Json(name = "delivery_lng")
    val deliveryLng: Double?,
    @Json(name = "estimated_delivery_time")
    val estimatedDeliveryTime: Int,
    @Json(name = "actual_delivery_time")
    val actualDeliveryTime: String?,
    @Json(name = "special_notes")
    val specialNotes: String?,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "updated_at")
    val updatedAt: String?
)

@JsonClass(generateAdapter = true)
data class Voucher(
    @Json(name = "id")
    val id: String,
    @Json(name = "user_id")
    val userId: String?,
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String?,
    @Json(name = "discount_type")
    val discountType: String, // 'percentage', 'fixed_amount'
    @Json(name = "discount_value")
    val discountValue: Double,
    @Json(name = "minimum_order")
    val minimumOrder: Double?,
    @Json(name = "max_discount")
    val maxDiscount: Double?,
    @Json(name = "restaurant_id")
    val restaurantId: String?,
    @Json(name = "valid_from")
    val validFrom: String?,
    @Json(name = "valid_until")
    val validUntil: String?,
    @Json(name = "is_used")
    val isUsed: Boolean,
    @Json(name = "terms_conditions")
    val termsConditions: String?
) {
    // Calculate discount amount for given subtotal
    fun calculateDiscount(subtotal: Double): Double {
        if (minimumOrder != null && subtotal < minimumOrder) return 0.0
        
        return when (discountType) {
            "percentage" -> {
                val discount = subtotal * (discountValue / 100.0)
                if (maxDiscount != null) minOf(discount, maxDiscount) else discount
            }
            "fixed_amount" -> discountValue
            else -> 0.0
        }
    }
}