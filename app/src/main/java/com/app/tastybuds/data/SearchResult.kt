package com.app.tastybuds.data


data class SearchResult(
    val id: String,
    val restaurant: Restaurant? = null,
    val menuItemList: List<MenuItem> = emptyList()
)

data class Restaurant(
    val id: String,
    val name: String,
    val description: String,
    val cuisine: List<String>,
    val rating: Float,
    val reviewCount: Int,
    val deliveryTime: String,
    val distance: String,
    val deliveryFee: Double,
    val imageUrl: String,
    val badges: List<String>,
    val isOpen: Boolean,
    val isFavorite: Boolean,
    val priceRange: String,
    val type: SearchResultType
)

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val rating: Float,
    val reviewCount: Int,
    val isPopular: Boolean,
    val isSpicy: Boolean,
    val prepTime: String,
    val categoryId: String,
    val type: SearchResultType
)

enum class SearchResultType {
    RESTAURANT,
    FOOD_ITEM
}