package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateUserRequest(
    @Json(name = "name")
    val name: String? = null,
    @Json(name = "email")
    val email: String? = null,
    @Json(name = "profile_url")
    val profileUrl: String? = null
)
