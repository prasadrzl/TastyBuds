package com.app.tastybuds.util

import com.app.tastybuds.domain.model.FoodDetailsData
import java.text.SimpleDateFormat
import java.util.Locale

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

fun formatOrderDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}