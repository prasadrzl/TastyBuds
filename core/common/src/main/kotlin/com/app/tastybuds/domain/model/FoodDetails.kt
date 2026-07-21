package com.app.tastybuds.domain.model

data class FoodDetails(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val basePrice: Float = 0.0f,
    val imageUrl: String = "",
    val category: String = "",
    val rating: Float = 0.0f,
    val reviewCount: Int = 0,
    val restaurantId: String = "",
    val restaurantName: String = "",
    val isVegetarian: Boolean = false,
    val isSpicy: Boolean = false,
    val allergens: List<String> = emptyList(),
    val prepTime: String = "",
    val calories: Int = 0,
    val isFavorite: Boolean = false
)

data class SizeOption(
    val id: String = "",
    val name: String = "",
    val additionalPrice: Float = 0.0f,
    val isDefault: Boolean = false,
    val isAvailable: Boolean = true,
    val description: String = ""
)

data class ToppingOption(
    val id: String = "",
    val name: String = "",
    val price: Float = 0.0f,
    val isDefault: Boolean = false,
    val isAvailable: Boolean = true,
    val description: String = "",
    var isSelected: Boolean = false
)

data class SpiceLevel(
    val id: String = "",
    val name: String = "",
    val level: Int = 0,
    val isDefault: Boolean = false,
    val isAvailable: Boolean = true,
    val description: String = ""
)

data class FoodCustomization(
    val sizes: List<SizeOption> = emptyList(),
    val toppings: List<ToppingOption> = emptyList(),
    val spiceLevels: List<SpiceLevel> = emptyList()
)

data class FoodDetailsData(
    val foodDetails: FoodDetails = FoodDetails(),
    val customization: FoodCustomization = FoodCustomization()
)