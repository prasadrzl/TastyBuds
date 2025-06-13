package com.app.tastybuds.domain.model

data class CategoryRestaurant(
    val id: String,
    val name: String,
    val description: String,
    val cuisine: String,
    val rating: Float,
    val reviewCount: Int,
    val deliveryTime: String,
    val deliveryFee: Float,
    val distance: String,
    val priceRange: String,
    val imageUrl: String,
    val badges: List<String>,
    val isOpen: Boolean,
    val isFavorite: Boolean,
    val isFreeship: Boolean,
    val minimumOrder: Float?
)

data class CategoryMenuItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Float = 0f,
    val imageUrl: String = "",
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val isPopular: Boolean = false,
    val isSpicy: Boolean = false,
    val restaurantId: String = "",
    val restaurantName: String = "",
    val deliveryTime: String = ""
)

data class CategoryDetailsData(
    val topRestaurants: List<CategoryRestaurant> = emptyList(),
    val menuItems: List<CategoryMenuItem> = emptyList(),
    val recommendedRestaurants: List<CategoryRestaurant> = emptyList()
)