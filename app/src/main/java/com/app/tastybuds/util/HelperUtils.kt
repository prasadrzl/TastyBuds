package com.app.tastybuds.util

fun parseDeliveryTime(deliveryTime: String): Int {
    return deliveryTime.filter { it.isDigit() }.toIntOrNull() ?: 999
}

fun parseDistance(distance: String): Double {
    return distance.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 999.0
}