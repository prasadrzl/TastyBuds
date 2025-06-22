package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VoucherApiResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "user_id") val userId: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "value") val value: Double = 0.0,
    @Json(name = "is_used") val isUsed: Boolean = false,
    @Json(name = "expiry_date") val expiryDate: String? = null,
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "restaurant_id") val restaurantId: String? = null,
    @Json(name = "voucher_type") val voucherType: String = "restaurant_specific",
    @Json(name = "applicable_category_ids") val applicableCategoryIds: List<String>? = null,
    @Json(name = "minimum_order_amount") val minimumOrderAmount: Double = 0.0,
    @Json(name = "usage_limit") val usageLimit: Int = 1,
    @Json(name = "used_count") val usedCount: Int = 0,
    @Json(name = "is_active") val isActive: Boolean = true,
    @Json(name = "start_date") val startDate: String = "",
    @Json(name = "end_date") val endDate: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "discount_type") val discountType: String = "percentage"
)

@JsonClass(generateAdapter = true)
data class VoucherUsageRequest(
    @Json(name = "is_used") val isUsed: Boolean = true,
    @Json(name = "used_count") val usedCount: Int = 1
)