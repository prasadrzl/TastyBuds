package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeliveryAddress(
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
    val country: String = "Singapore",
    @Json(name = "latitude")
    val latitude: Double?,
    @Json(name = "longitude")
    val longitude: Double?,
    @Json(name = "delivery_instructions")
    val deliveryInstructions: String?
)
