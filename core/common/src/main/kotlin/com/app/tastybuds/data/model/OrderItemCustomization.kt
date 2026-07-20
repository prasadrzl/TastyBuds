package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderItemCustomization(
    @Json(name = "size")
    val size: OrderItemSize?,
    @Json(name = "toppings")
    val toppings: List<OrderItemTopping>,
    @Json(name = "spice_level")
    val spiceLevel: OrderItemSpiceLevel?
)

@JsonClass(generateAdapter = true)
data class OrderItemSize(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "additional_price")
    val additionalPrice: Double
)

@JsonClass(generateAdapter = true)
data class OrderItemTopping(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "price")
    val price: Double
)

@JsonClass(generateAdapter = true)
data class OrderItemSpiceLevel(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "level")
    val level: Int
)
