package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "icon") val icon: String = "",
    @Json(name = "background_color") val backgroundColorHex: String = "#FF7700",
    @Json(name = "restaurant_count") val restaurantCount: Int = 0,
    @Json(name = "is_popular") val isPopular: Boolean = false,
    @Json(name = "created_at") val createdAt: String = ""
)
