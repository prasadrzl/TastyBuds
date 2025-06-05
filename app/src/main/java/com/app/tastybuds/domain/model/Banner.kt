package com.app.tastybuds.domain.model

import androidx.compose.ui.graphics.Color

data class Banner(
    val id: String = "",
    val title: String = "",
    val price: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val backgroundColor: Color = Color(0xFFFF7700)
)

data class Category(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val backgroundColor: Color = Color(0xFFFF7700)
)

data class Collection(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val imageUrl: String = "",
    val badge: String? = null
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
    val title: String = "",
    val price: String = "",
    val originalPrice: String? = null,
    val imageUrl: String = "",
    val badge: String? = null,
    val discountPercentage: Int? = null
)