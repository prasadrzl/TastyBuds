package com.app.tastybuds.data.model.mapper

import com.app.tastybuds.domain.model.*
import com.app.tastybuds.data.model.FoodCustomizationResponse
import com.app.tastybuds.data.model.FoodDetailsResponse
import com.app.tastybuds.data.model.SizeOptionResponse
import com.app.tastybuds.data.model.SpiceLevelResponse
import com.app.tastybuds.data.model.ToppingOptionResponse

fun FoodDetailsResponse.toFoodDetails() = FoodDetails(
    id = id,
    name = name,
    description = description,
    basePrice = basePrice,
    imageUrl = imageUrl,
    category = category,
    rating = rating,
    reviewCount = reviewCount,
    restaurantId = restaurantId,
    restaurantName = restaurantName,
    isVegetarian = isVegetarian,
    isSpicy = isSpicy,
    allergens = allergens,
    prepTime = prepTime,
    calories = calories ?: 0,
    isFavorite = isFavorite
)

fun SizeOptionResponse.toSizeOption() = SizeOption(
    id = id,
    name = name,
    additionalPrice = additionalPrice,
    isDefault = isDefault,
    isAvailable = isAvailable,
    description = description
)

fun ToppingOptionResponse.toToppingOption() = ToppingOption(
    id = id,
    name = name,
    price = price,
    isDefault = isDefault,
    isAvailable = isAvailable,
    description = description,
    isSelected = isDefault
)

fun SpiceLevelResponse.toSpiceLevel() = SpiceLevel(
    id = id,
    name = name,
    level = level,
    isDefault = isDefault,
    isAvailable = isAvailable,
    description = description
)

fun FoodCustomizationResponse.toFoodCustomization() = FoodCustomization(
    sizes = sizes.map { it.toSizeOption() },
    toppings = toppings.map { it.toToppingOption() },
    spiceLevels = spiceLevels.map { it.toSpiceLevel() }
)