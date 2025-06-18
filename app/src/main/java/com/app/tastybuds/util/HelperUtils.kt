package com.app.tastybuds.util

import com.app.tastybuds.domain.model.FoodDetailsData

fun calculateTotalPrice(
    data: FoodDetailsData,
    sizeId: String,
    toppingIds: List<String>,
    quantity: Int
): Float {
    val basePrice = data.foodDetails.basePrice

    val sizePrice = data.customization.sizes
        .find { it.id == sizeId }?.additionalPrice ?: 0.0f

    val toppingsPrice = data.customization.toppings
        .filter { it.id in toppingIds }
        .sumOf { it.price.toDouble() }.toFloat()

    return (basePrice + sizePrice + toppingsPrice) * quantity
}