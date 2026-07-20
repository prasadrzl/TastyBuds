package com.app.tastybuds.domain.model

// `backgroundColor` is a raw ARGB int (matches Compose's Color(Int) constructor), not a
// Color object — this is a pure Kotlin domain model, so it can't depend on Compose. Wrap
// with androidx.compose.ui.graphics.Color(...) at the UI layer.

data class Banner(
    val id: String = "",
    val title: String = "",
    val price: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val backgroundColor: Int = 0xFFFF7700.toInt()
)

data class Category(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val backgroundColor: Int = 0xFFFF7700.toInt()
)

data class Collection(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val imageUrl: String = "",
    val badge: String? = null,
    val restaurantIds: List<String> = emptyList(),
)

data class Restaurant(
    val id: String = "",
    val name: String = "",
    val cuisine: String = "",
    val rating: Float = 0.0f,
    val reviewCount: Int = 0,
    val deliveryTime: String = "",
    val distance: String = "",
    val imageUrl: String = "",
    val badge: String? = null
)

data class Deal(
    val id: String = "",
    val menuItemId: String = "",
    val title: String = "",
    val description: String = "",
    val originalPrice: String = "",
    val salePrice: String = "",
    val discountPercentage: Int = 0,
    val imageUrl: String = "",
    val restaurantId: String = "",
    val restaurantName: String = "",
    val deliveryTime: String = "",
    val rating: Float = 0.0f,
    val badges: List<Badge> = emptyList(),
    val createdAt: String = ""
)

data class Badge(
    val text: String = "",
    val type: String = "",
    val backgroundColor: String = ""
)