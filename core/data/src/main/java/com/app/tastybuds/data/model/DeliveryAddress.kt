package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// All fields default so this can be built as `DeliveryAddress()` and tolerate omitted keys.
@JsonClass(generateAdapter = true)
data class DeliveryAddress(
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
    val country: String = "Singapore",
    @Json(name = "latitude")
    val latitude: Double? = null,
    @Json(name = "longitude")
    val longitude: Double? = null,
    @Json(name = "delivery_instructions")
    val deliveryInstructions: String? = null
)
