package com.app.tastybuds.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodDetailsResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "description") val description: String = "",
    @Json(name = "base_price") val basePrice: Float = 0.0f,
    @Json(name = "image") val imageUrl: String = "",
    @Json(name = "category") val category: String = "",
    @Json(name = "rating") val rating: Float = 0.0f,
    @Json(name = "review_count") val reviewCount: Int = 0,
    @Json(name = "restaurant_id") val restaurantId: String = "",
    @Json(name = "restaurant_name") val restaurantName: String = "",
    @Json(name = "is_vegetarian") val isVegetarian: Boolean = false,
    @Json(name = "is_spicy") val isSpicy: Boolean = false,
    @Json(name = "allergens") val allergens: List<String> = emptyList(),
    @Json(name = "prep_time") val prepTime: String = "15-20 mins",
    @Json(name = "calories") val calories: Int? = null,
    @Json(name = "isFavorite") val isFavorite: Boolean = false
)

@JsonClass(generateAdapter = true)
data class SizeOptionResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "additional_price") val additionalPrice: Float = 0.0f,
    @Json(name = "is_default") val isDefault: Boolean = false,
    @Json(name = "is_available") val isAvailable: Boolean = true,
    @Json(name = "description") val description: String = ""
)

@JsonClass(generateAdapter = true)
data class ToppingOptionResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "price") val price: Float = 0.0f,
    @Json(name = "is_default") val isDefault: Boolean = false,
    @Json(name = "is_available") val isAvailable: Boolean = true,
    @Json(name = "description") val description: String = ""
)

@JsonClass(generateAdapter = true)
data class SpiceLevelResponse(
    @Json(name = "id") val id: String = "",
    @Json(name = "name") val name: String = "",
    @Json(name = "level") val level: Int = 0,
    @Json(name = "is_default") val isDefault: Boolean = false,
    @Json(name = "is_available") val isAvailable: Boolean = true,
    @Json(name = "description") val description: String = ""
)

@JsonClass(generateAdapter = true)
data class FoodCustomizationResponse(
    @Json(name = "sizes") val sizes: List<SizeOptionResponse> = emptyList(),
    @Json(name = "toppings") val toppings: List<ToppingOptionResponse> = emptyList(),
    @Json(name = "spice_levels") val spiceLevels: List<SpiceLevelResponse> = emptyList()
)