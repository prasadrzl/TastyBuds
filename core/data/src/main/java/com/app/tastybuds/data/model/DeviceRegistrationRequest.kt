package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeviceRegistrationRequest(
    @Json(name = "user_id") val userId: String,
    @Json(name = "token") val token: String,
    @Json(name = "platform") val platform: String = "ANDROID"
)
