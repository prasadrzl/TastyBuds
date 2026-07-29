package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddFavoriteRequest(
    @Json(name = "user_id") val userId: String,
    @Json(name = "menu_item_id") val menuItemId: String? = null,
    @Json(name = "restaurant_id") val restaurantId: String? = null
)
