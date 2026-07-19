package com.app.tastybuds.domain.model

// Pure business data — no display formatting here. The presentation layer
// (ui/favorites/*Ui.kt) maps these into UI-ready models with computed display text.

data class FavoriteRestaurant(
    val id: Int = -1,
    val restaurantId: String = "",
    val name: String = "Unknown Restaurant",
    val cuisine: String = "Various Cuisine",
    val rating: Float = 0.0f,
    val reviewCount: Int = 0,
    val deliveryTime: String = "N/A",
    val distance: String = "Unknown",
    val priceRange: String = "$",
    val imageUrl: String = "",
    val deliveryFee: Float = 0.0f,
    val isOpen: Boolean = true,
    val createdAt: String = ""
)

data class FavoriteMenuItem(
    val id: Int = -1,
    val menuItemId: String = "",
    val name: String = "Unknown Item",
    val description: String = "",
    val price: Float = 0.0f,
    val imageUrl: String = "",
    val rating: Float = 0.0f,
    val reviewCount: Int = 0,
    val restaurantName: String = "Unknown Restaurant",
    val restaurantId: String = "",
    val isPopular: Boolean = false,
    val isSpicy: Boolean = false,
    val createdAt: String = ""
)
