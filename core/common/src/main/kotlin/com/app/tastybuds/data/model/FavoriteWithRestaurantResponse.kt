package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavoriteWithRestaurantResponse(
    @Json(name = "id") val id: Int = -1,
    @Json(name = "user_id") val userId: String = "",
    @Json(name = "restaurant_id") val restaurantId: String? = null,
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "restaurants") val restaurant: RestaurantDetailsResponse? = null
)