package com.app.tastybuds.util

fun isRestaurantNear(distance: String?): Boolean {
    if (distance.isNullOrBlank()) return false

    return try {
        val distanceValue = distance.replace(Regex("[^0-9.]"), "").toFloatOrNull()
        when {
            distance.contains("m", true) && !distance.contains("km", true) -> {
                (distanceValue ?: Float.MAX_VALUE) <= 500f
            }

            distance.contains("km", true) -> {
                (distanceValue ?: Float.MAX_VALUE) <= 2.0f
            }

            else -> false
        }
    } catch (e: Exception) {
        false
    }
}