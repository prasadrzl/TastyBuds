package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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
    val discountType: String,
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
