package com.app.tastybuds.data.model

// API Response models
data class BannerResponse(
    val id: String,
    val title: String,
    val price: String,
    val description: String,
    val imageUrl: String,
    val colorHex: String
)

data class CategoryResponse(
    val id: String,
    val name: String,
    val image_url: String,
    val colorHex: String
)

data class CollectionResponse(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val badge: String?
)

data class RestaurantResponse(
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

data class DealResponse(
    val id: String,
    val title: String,
    val price: String,
    val originalPrice: String?,
    val imageUrl: String,
    val badge: String?,
    val discountPercentage: Int?
)

data class VoucherCountResponse(
    val userId: String,
    val voucherCount: Int
)