package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BannerResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "discount") val discount: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "background_color") val backgroundColorHex: String = "#FF7700",
    @Json(name = "image") val imageUrl: String = "",
    @Json(name = "cta_text") val ctaText: String = "",
    @Json(name = "action_type") val actionType: String = "",
    @Json(name = "action_id") val actionId: String = "",
    @Json(name = "created_at") val createdAt: String = ""
)

@JsonClass(generateAdapter = true)
data class CategoryResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "icon") val icon: String = "",
    @Json(name = "background_color") val backgroundColorHex: String = "#FF7700",
    @Json(name = "restaurant_count") val restaurantCount: Int = 0,
    @Json(name = "is_popular") val isPopular: Boolean = false,
    @Json(name = "created_at") val createdAt: String = ""
)

@JsonClass(generateAdapter = true)
data class RestaurantBadge(
    @Json(name = "text") val text: String = "",
    @Json(name = "type") val type: String = "",
    @Json(name = "background_color") val backgroundColor: String = "#FF7700"
)

@JsonClass(generateAdapter = true)
data class RestaurantResponse(
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
    @Json(name = "created_at") val createdAt: String = ""
)

@JsonClass(generateAdapter = true)
data class CollectionResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "subtitle") val subtitle: String = "",
    @Json(name = "image") val imageUrl: String = "",
    @Json(name = "badge") val badge: String? = null,
    @Json(name = "restaurant_count") val restaurantCount: Int = 0,
    @Json(name = "average_rating") val averageRating: Float = 0.0f,
    @Json(name = "restaurant_ids") val restaurantIds: List<String> = emptyList(),
    @Json(name = "created_at") val createdAt: String = ""
)

@JsonClass(generateAdapter = true)
data class DealResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "price") val price: String = "",
    @Json(name = "original_price") val originalPrice: String? = null,
    @Json(name = "image") val imageUrl: String = "",
    @Json(name = "badge") val badge: String? = null,
    @Json(name = "discount_percentage") val discountPercentage: Int? = null
)

@JsonClass(generateAdapter = true)
data class VoucherCountResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "user_id") val userId: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "value") val value: Float = 0.0f,
    @Json(name = "is_used") val isUsed: Boolean = false,
    @Json(name = "expiry_date") val expiryDate: String = "",
    @Json(name = "created_at") val createdAt: String = ""
)