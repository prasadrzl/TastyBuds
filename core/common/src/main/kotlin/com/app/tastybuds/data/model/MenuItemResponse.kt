package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MenuItemResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "price") val price: Float = 0.0f,
    @Json(name = "image") val imageUrl: String = "",
    @Json(name = "rating") val rating: Float = 0.0f,
    @Json(name = "review_count") val reviewCount: Int = 0,
    @Json(name = "category") val category: String = "",
    @Json(name = "is_popular") val isPopular: Boolean = false,
    @Json(name = "is_spicy") val isSpicy: Boolean = false,
    @Json(name = "restaurant_id") val restaurantId: String = "",
    @Json(name = "prep_time") val prepTime: String = "15-20 mins",
    @Json(name = "is_favorite") val isFavorite: Boolean = false
)
