package com.app.tastybuds.domain.model

data class Banner(
    val id: String,
    val title: String,
    val price: String,
    val description: String,
    val imageUrl: String,
    val backgroundColor: androidx.compose.ui.graphics.Color
)

data class Category(
    val id: String,
    val name: String,
    val imageUrl: String,
    val backgroundColor: androidx.compose.ui.graphics.Color
)

data class Collection(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val badge: String?
)

data class Restaurant(
    val id: String,
    val name: String,
    val cuisine: String,
    val rating: Float,
    val reviewCount: Int,
    val deliveryTime: String,
    val distance: String,
    val imageUrl: String,
    val badge: String?
)

data class Deal(
    val id: String,
    val title: String,
    val price: String,
    val originalPrice: String?,
    val imageUrl: String,
    val badge: String?,
    val discountPercentage: Int?
)