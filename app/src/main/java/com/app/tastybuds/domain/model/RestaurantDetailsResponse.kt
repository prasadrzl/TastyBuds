package com.app.tastybuds.domain.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RestaurantDetailsResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "cuisine") val cuisine: List<String> = emptyList(),
    @Json(name = "rating") val rating: Float = 0.0f,
    @Json(name = "review_count") val reviewCount: Int = 0,
    @Json(name = "delivery_time") val deliveryTime: String = "",
    @Json(name = "distance") val distance: String = "",
    @Json(name = "price_range") val priceRange: String = "",
    @Json(name = "image") val imageUrl: String = "",
    @Json(name = "is_open") val isOpen: Boolean = true,
    @Json(name = "delivery_fee") val deliveryFee: Float = 0.0f,
    @Json(name = "minimum_order") val minimumOrder: Float? = null,
    @Json(name = "phone") val phone: String = "",
    @Json(name = "address") val address: String = ""
)

@JsonClass(generateAdapter = true)
data class MenuItemResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "price") val price: Float = 0.0f,
    @Json(name = "image") val imageUrl: String = "",
    @Json(name = "rating") val rating: Float = 0.0f,
    @Json(name = "review_count") val reviewCount: Int = 0,
    @Json(name = "category") val category: String = "",
    @Json(name = "is_popular") val isPopular: Boolean = false,
    @Json(name = "is_spicy") val isSpicy: Boolean = false,
    @Json(name = "restaurant_id") val restaurantId: String = "",
    @Json(name = "prep_time") val prepTime: String = "15-20 mins"
)

@JsonClass(generateAdapter = true)
data class RestaurantReviewResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "restaurant_id") val restaurantId: String = "",
    @Json(name = "user_id") val userId: String = "",
    @Json(name = "user_name") val userName: String = "",
    @Json(name = "user_avatar") val userAvatar: String = "",
    @Json(name = "rating") val rating: Float = 0.0f,
    @Json(name = "comment") val comment: String = "",
    @Json(name = "helpful_count") val helpfulCount: Int = 0,
    @Json(name = "created_at") val createdAt: String = "",
    @Json(name = "time_ago") val timeAgo: String = ""
)

@JsonClass(generateAdapter = true)
data class RestaurantVoucherResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "title") val title: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "value") val value: Float = 0.0f,
    @Json(name = "discount_type") val discountType: String = "percentage",
    @Json(name = "user_id") val userId: String = "",
    @Json(name = "restaurant_id") val restaurantId: String? = null,
    @Json(name = "restaurant_name") val restaurantName: String? = null,
    @Json(name = "minimum_order_amount") val minimumOrderAmount: Float = 0.0f,
    @Json(name = "end_date") val endDate: String = ""
)

@JsonClass(generateAdapter = true)
data class ComboResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "price") val price: Float = 0.0f,
    @Json(name = "image") val imageUrl: String = "",
    @Json(name = "rating") val rating: Float = 0.0f,
    @Json(name = "review_count") val reviewCount: Int = 0,
    @Json(name = "restaurant_id") val restaurantId: String = ""
)