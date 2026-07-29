package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DealResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "menu_item_id") val menuItemId: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "original_price") val originalPrice: Double = 0.0,
    @Json(name = "sale_price") val salePrice: Double = 0.0,
    @Json(name = "discount_percent") val discountPercent: Int = 0,
    @Json(name = "image") val image: String = "",
    @Json(name = "restaurant_id") val restaurantId: String = "",
    @Json(name = "restaurant_name") val restaurantName: String = "",
    @Json(name = "delivery_time") val deliveryTime: String = "",
    @Json(name = "rating") val rating: Double = 0.0,
    @Json(name = "badges") val badges: List<BadgeResponse> = emptyList(),
    @Json(name = "created_at") val createdAt: String = ""
)

@JsonClass(generateAdapter = true)
data class BadgeResponse(
    @Json(name = "text") val text: String = "",
    @Json(name = "type") val type: String = "",
    @Json(name = "background_color") val backgroundColor: String = ""
)
