package com.app.tastybuds.domain.model

// Pure domain equivalents of the data.model DTOs used by the order flow
// (data.model.Order, UserAddress, DeliveryAddress, OrderItemRequest, Voucher, OrderStatus).
// data.model.OrderStatus keeps its Moshi @Json annotations for response deserialization;
// this is the domain-layer vocabulary, mapped via fromString() like VoucherType/DiscountType.

enum class OrderStatus(val displayName: String) {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    PREPARING("Preparing"),
    READY("Ready"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    companion object {
        fun fromString(value: String): OrderStatus = when (value.lowercase()) {
            "pending" -> PENDING
            "confirmed" -> CONFIRMED
            "preparing" -> PREPARING
            "ready" -> READY
            "out_for_delivery" -> OUT_FOR_DELIVERY
            "delivered" -> DELIVERED
            "cancelled" -> CANCELLED
            else -> PENDING
        }
    }
}

data class UserAddress(
    val id: String,
    val userId: String,
    val addressLine: String,
    val street: String?,
    val city: String?,
    val state: String?,
    val postalCode: String?,
    val country: String?,
    val latitude: Double?,
    val longitude: Double?,
    val addressType: String?,
    val addressLabel: String?,
    val isDefault: Boolean,
    val deliveryInstructions: String?,
    val createdAt: String?
)

data class DeliveryAddress(
    val addressLine: String,
    val street: String?,
    val city: String?,
    val state: String?,
    val postalCode: String?,
    val country: String = "Singapore",
    val latitude: Double?,
    val longitude: Double?,
    val deliveryInstructions: String?
)

data class OrderItemSize(
    val id: String,
    val name: String,
    val additionalPrice: Double
)

data class OrderItemTopping(
    val id: String,
    val name: String,
    val price: Double
)

data class OrderItemSpiceLevel(
    val id: String,
    val name: String,
    val level: Int
)

data class OrderItemCustomization(
    val size: OrderItemSize?,
    val toppings: List<OrderItemTopping>,
    val spiceLevel: OrderItemSpiceLevel?
)

data class OrderLineItem(
    val menuItemId: String,
    val name: String,
    val image: String?,
    val basePrice: Double,
    val quantity: Int,
    val customizations: OrderItemCustomization,
    val itemTotal: Double,
    val notes: String?
)

data class Order(
    val id: String,
    val userId: String,
    val restaurantId: String?,
    val restaurantName: String? = null,
    val status: OrderStatus,
    val orderItems: List<OrderLineItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val promotionDiscount: Double,
    val totalAmount: Double,
    val paymentMethod: String,
    val paymentStatus: String,
    val deliveryAddress: DeliveryAddress,
    val deliveryLat: Double?,
    val deliveryLng: Double?,
    val estimatedDeliveryTime: Int,
    val actualDeliveryTime: String?,
    val specialNotes: String?,
    val createdAt: String,
    val updatedAt: String?
)

// Named distinctly from domain.model.Voucher (the general "my vouchers" feature model) —
// this mirrors data.model.Voucher, the order-checkout-time redemption DTO, which has a
// different shape (raw discountType string + calculateDiscount business rule) serving a
// different purpose than the Vouchers feature's display-oriented Voucher model.
data class AvailableVoucher(
    val id: String,
    val userId: String?,
    val title: String,
    val description: String?,
    val discountType: String,
    val discountValue: Double,
    val minimumOrder: Double?,
    val maxDiscount: Double?,
    val restaurantId: String?,
    val validFrom: String?,
    val validUntil: String?,
    val isUsed: Boolean,
    val termsConditions: String?
) {
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
