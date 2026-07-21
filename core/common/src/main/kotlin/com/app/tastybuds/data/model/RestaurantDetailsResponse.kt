package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RestaurantDetailsResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "cuisine") val cuisine: List<String> = emptyList(),
    @Json(name = "rating") val rating: Float = 0.0f,
    @Json(name = "review_count") val reviewCount: Int = 0,
    @Json(name = "delivery_time") val deliveryTime: String = "",
    @Json(name = "distance") val distance: String = "",
    @Json(name = "price_range") val priceRange: String = "",
    @Json(name = "image") val imageUrl: String = "",
    @Json(name = "is_open") val isOpen: Boolean = true,
    @Json(name = "delivery_fee") val deliveryFee: Float = 0.0f,
    @Json(name = "minimum_order") val minimumOrder: Float? = null,
    @Json(name = "phone") val phone: String = "",
    @Json(name = "address") val address: String = "",
    @Json(name = "is_favorite") val isFavorite: Boolean = false,
)
