package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VoucherUsageRequest(
    @Json(name = "is_used") val isUsed: Boolean = true,
    @Json(name = "used_count") val usedCount: Int = 1
)
