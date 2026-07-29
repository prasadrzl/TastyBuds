package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserAddress(
    @Json(name = "id")
    val id: String = "",
    @Json(name = "user_id")
    val userId: String = "",
    @Json(name = "address_line")
    val addressLine: String = "",
    @Json(name = "street")
    val street: String? = null,
    @Json(name = "city")
    val city: String? = null,
    @Json(name = "state")
    val state: String? = null,
    @Json(name = "postal_code")
    val postalCode: String? = null,
    @Json(name = "country")
    val country: String? = null,
    @Json(name = "latitude")
    val latitude: Double? = null,
    @Json(name = "longitude")
    val longitude: Double? = null,
    @Json(name = "address_type")
    val addressType: String? = null,
    @Json(name = "address_label")
    val addressLabel: String? = null,
    @Json(name = "is_default")
    val isDefault: Boolean = false,
    @Json(name = "delivery_instructions")
    val deliveryInstructions: String? = null,
    @Json(name = "created_at")
    val createdAt: String? = null
)
