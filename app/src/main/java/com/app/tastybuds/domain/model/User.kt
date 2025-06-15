package com.app.tastybuds.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val profileUrl: String?,
    val currentLocation: Location?,
    val createdAt: String
)

data class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)

data class UpdateProfileRequest(
    val name: String? = null,
    val email: String? = null,
    val profileUrl: String? = null
)