package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryRestaurantResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "cuisine") val cuisine: List<String> = emptyList(),
    @Json(name = "rating") val rating: Float = 0.0f,
    @Json(name = "review_count") val reviewCount: Int = 0,
    @Json(name = "delivery_time") val deliveryTime: String = "",
    @Json(name = "delivery_fee") val deliveryFee: Float = 0.0f,
    @Json(name = "distance") val distance: String = "",
    @Json(name = "price_range") val priceRange: String = "",
    @Json(name = "image") val imageUrl: String = "",
    @Json(name = "badges") val badges: List<RestaurantBadge> = emptyList(),
    @Json(name = "is_open") val isOpen: Boolean = true,
    @Json(name = "is_favorite") val isFavorite: Boolean = false,
    @Json(name = "category_ids") val categoryIds: List<String> = emptyList(),
    @Json(name = "is_freeship") val isFreeship: Boolean = false,
    @Json(name = "minimum_order") val minimumOrder: Float? = null,
    @Json(name = "created_at") val createdAt: String = ""
)

@JsonClass(generateAdapter = true)
data class CategoryMenuItemResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "price") val price: Float = 0.0f,
    @Json(name = "image") val imageUrl: String = "",
    @Json(name = "category_id") val categoryId: String = "",
    @Json(name = "rating") val rating: Float = 0.0f,
    @Json(name = "review_count") val reviewCount: Int = 0,
    @Json(name = "is_popular") val isPopular: Boolean = false,
    @Json(name = "is_spicy") val isSpicy: Boolean = false,
    @Json(name = "restaurant_id") val restaurantId: String = "",
    @Json(name = "restaurants") val restaurant: MenuItemRestaurant? = null
)

@JsonClass(generateAdapter = true)
data class MenuItemRestaurant(
    @Json(name = "name") val name: String = "",
    @Json(name = "delivery_time") val deliveryTime: String = "",
    @Json(name = "id") val id: String = ""
)