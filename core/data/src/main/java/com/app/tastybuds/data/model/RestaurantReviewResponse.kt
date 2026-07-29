package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RestaurantReviewResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "restaurant_id") val restaurantId: String = "",
    @Json(name = "user_id") val userId: String = "",
    @Json(name = "user_name") val userName: String = "",
    @Json(name = "user_avatar") val userAvatar: String = "",
    @Json(name = "rating") val rating: Float = 0.0f,
    @Json(name = "comment") val comment: String = "",
    @Json(name = "helpful_count") val helpfulCount: Int = 0,
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "time_ago") val timeAgo: String = ""
)
