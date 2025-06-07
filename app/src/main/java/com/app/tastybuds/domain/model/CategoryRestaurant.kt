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
    val id: String,
    val name: String,
    val description: String,
    val price: Float,
    val imageUrl: String,
    val rating: Float,
    val reviewCount: Int,
    val isPopular: Boolean,
    val isSpicy: Boolean,
    val restaurantId: String,
    val restaurantName: String,
    val deliveryTime: String
)

data class CategoryDetailsData(
    val topRestaurants: List<CategoryRestaurant>,
    val menuItems: List<CategoryMenuItem>,
    val recommendedRestaurants: List<CategoryRestaurant>
)