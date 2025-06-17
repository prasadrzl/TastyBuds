package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddFavoriteRequest(
    @Json(name = "user_id") val userId: String,
    @Json(name = "menu_item_id") val menuItemId: String? = null,
    @Json(name = "restaurant_id") val restaurantId: String? = null
)

@JsonClass(generateAdapter = true)
data class FavoriteResponse(
    @Json(name = "id") val id: Int = -1,
    @Json(name = "user_id") val userId: String = "",
    @Json(name = "menu_item_id") val menuItemId: String? = null,
    @Json(name = "restaurant_id") val restaurantId: String? = null,
    @Json(name = "created_at") val createdAt: String = ""
)