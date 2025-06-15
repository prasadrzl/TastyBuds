package com.app.tastybuds.domain.model

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

@JsonClass(generateAdapter = true)
data class LocationResponse(
    @Json(name = "latitude")
    val latitude: Double,
    @Json(name = "longitude")
    val longitude: Double,
    @Json(name = "address")
    val address: String?
)

@JsonClass(generateAdapter = true)
data class UpdateUserRequest(
    @Json(name = "name")
    val name: String? = null,
    @Json(name = "email")
    val email: String? = null,
    @Json(name = "profile_url")
    val profileUrl: String? = null
)