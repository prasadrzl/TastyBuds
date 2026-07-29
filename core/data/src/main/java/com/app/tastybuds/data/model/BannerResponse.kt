package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BannerResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "discount") val discount: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "background_color") val backgroundColorHex: String = "#FF7700",
    @Json(name = "image") val imageUrl: String = "",
    @Json(name = "cta_text") val ctaText: String = "",
    @Json(name = "action_type") val actionType: String = "",
    @Json(name = "action_id") val actionId: String = "",
    @Json(name = "created_at") val createdAt: String = ""
)
