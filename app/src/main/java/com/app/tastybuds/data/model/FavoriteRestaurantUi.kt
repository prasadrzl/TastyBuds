package com.app.tastybuds.data.model

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