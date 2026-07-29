package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RestaurantVoucherResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "value") val value: Float = 0.0f,
    @Json(name = "discount_type") val discountType: String = "percentage",
    @Json(name = "user_id") val userId: String = "",
    @Json(name = "restaurant_id") val restaurantId: String? = null,
    @Json(name = "restaurant_name") val restaurantName: String? = null,
    @Json(name = "minimum_order_amount") val minimumOrderAmount: Float = 0.0f,
    @Json(name = "end_date") val endDate: String = ""
)
