package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserAddress(
    @Json(name = "id")
    val id: String,
    @Json(name = "user_id")
    val userId: String,
    @Json(name = "address_line")
    val addressLine: String,
    @Json(name = "street")
    val street: String?,
    @Json(name = "city")
    val city: String?,
    @Json(name = "state")
    val state: String?,
    @Json(name = "postal_code")
    val postalCode: String?,
    @Json(name = "country")
    val country: String?,
    @Json(name = "latitude")
    val latitude: Double?,
    @Json(name = "longitude")
    val longitude: Double?,
    @Json(name = "address_type")
    val addressType: String?,
    @Json(name = "address_label")
    val addressLabel: String?,
    @Json(name = "is_default")
    val isDefault: Boolean,
    @Json(name = "delivery_instructions")
    val deliveryInstructions: String?,
    @Json(name = "created_at")
    val createdAt: String?
)
