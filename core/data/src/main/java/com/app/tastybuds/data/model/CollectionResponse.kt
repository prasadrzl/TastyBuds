package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CollectionResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "subtitle") val subtitle: String = "",
    @Json(name = "image") val imageUrl: String = "",
    @Json(name = "badge") val badge: String? = null,
    @Json(name = "restaurant_count") val restaurantCount: Int = 0,
    @Json(name = "average_rating") val averageRating: Float = 0.0f,
    @Json(name = "restaurant_ids") val restaurantIds: List<String> = emptyList(),
    @Json(name = "created_at") val createdAt: String = ""
)
