package com.app.tastybuds.ui.favorites

import com.app.tastybuds.domain.model.FavoriteMenuItem
import com.app.tastybuds.domain.model.FavoriteRestaurant

data class FavoriteRestaurantUi(
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
) {
    val ratingText: String
        get() = if (rating > 0) "$rating ($reviewCount)" else "No ratings"

    val deliveryInfo: String
        get() = "$deliveryTime • $distance"

    val hasValidImage: Boolean
        get() = imageUrl.isNotBlank() && !imageUrl.contains("placeholder")
}

data class FavoriteMenuItemUi(
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
) {
    val priceText: String
        get() = if (price > 0) "$${"%.2f".format(price)}" else "Price not available"

    val ratingText: String
        get() = if (rating > 0) "$rating ($reviewCount)" else "No ratings"

    val hasValidImage: Boolean
        get() = imageUrl.isNotBlank() && !imageUrl.contains("placeholder")
}

fun FavoriteRestaurant.toUi(): FavoriteRestaurantUi = FavoriteRestaurantUi(
    id = id,
    restaurantId = restaurantId,
    name = name,
    cuisine = cuisine,
    rating = rating,
    reviewCount = reviewCount,
    deliveryTime = deliveryTime,
    distance = distance,
    priceRange = priceRange,
    imageUrl = imageUrl,
    deliveryFee = deliveryFee,
    isOpen = isOpen,
    createdAt = createdAt
)

fun FavoriteMenuItem.toUi(): FavoriteMenuItemUi = FavoriteMenuItemUi(
    id = id,
    menuItemId = menuItemId,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    rating = rating,
    reviewCount = reviewCount,
    restaurantName = restaurantName,
    restaurantId = restaurantId,
    isPopular = isPopular,
    isSpicy = isSpicy,
    createdAt = createdAt
)
