package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserResponse(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "email")
    val email: String,
    @Json(name = "profile_url")
    val profileUrl: String?,
    @Json(name = "current_location")
    val currentLocation: LocationResponse?,
    @Json(name = "created_at")
    val createdAt: String
)
