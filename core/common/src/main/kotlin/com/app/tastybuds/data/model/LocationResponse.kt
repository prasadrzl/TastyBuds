package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationResponse(
    @Json(name = "latitude")
    val latitude: Double,
    @Json(name = "longitude")
    val longitude: Double,
    @Json(name = "address")
    val address: String?
)
