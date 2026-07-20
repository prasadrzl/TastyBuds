package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VoucherCountResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "user_id") val userId: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "value") val value: Float = 0.0f,
    @Json(name = "is_used") val isUsed: Boolean = false,
    @Json(name = "expiry_date") val expiryDate: String = "",
    @Json(name = "created_at") val createdAt: String = ""
)
