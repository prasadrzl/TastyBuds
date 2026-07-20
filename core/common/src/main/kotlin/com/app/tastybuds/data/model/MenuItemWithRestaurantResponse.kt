package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MenuItemWithRestaurantResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "price") val price: Double = 0.0,
    @Json(name = "image") val image: String = "",
    @Json(name = "rating") val rating: Float = 0.0f,
    @Json(name = "review_count") val reviewCount: Int = 0,
    @Json(name = "is_popular") val isPopular: Boolean = false,
    @Json(name = "is_spicy") val isSpicy: Boolean = false,
    @Json(name = "prep_time") val prepTime: String? = null,
    @Json(name = "restaurants") val restaurant: RestaurantResponse? = null
)
