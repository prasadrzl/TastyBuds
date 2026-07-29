package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// All fields default so an omitted key (backend uses non_null inclusion) never crashes
// Moshi deserialization, and nested types can be built as `OrderItemCustomization()`.
@JsonClass(generateAdapter = true)
data class OrderItemCustomization(
    @Json(name = "size")
    val size: OrderItemSize? = null,
    @Json(name = "toppings")
    val toppings: List<OrderItemTopping> = emptyList(),
    @Json(name = "spice_level")
    val spiceLevel: OrderItemSpiceLevel? = null
)

@JsonClass(generateAdapter = true)
data class OrderItemSize(
    @Json(name = "id")
    val id: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "additional_price")
    val additionalPrice: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class OrderItemTopping(
    @Json(name = "id")
    val id: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "price")
    val price: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class OrderItemSpiceLevel(
    @Json(name = "id")
    val id: String = "",
    @Json(name = "name")
    val name: String = "",
    @Json(name = "level")
    val level: Int = 0
)
