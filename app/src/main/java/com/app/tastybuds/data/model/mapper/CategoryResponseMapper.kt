package com.app.tastybuds.data.model.mapper

import com.app.tastybuds.data.model.CategoryMenuItemResponse
import com.app.tastybuds.data.model.CategoryRestaurantResponse
import com.app.tastybuds.domain.model.CategoryRestaurant
import com.app.tastybuds.domain.model.CategoryMenuItem

fun CategoryRestaurantResponse.toDomainModel(): CategoryRestaurant {
    return CategoryRestaurant(
        id = id,
        name = name,
        description = description,
        cuisine = cuisine.joinToString(", "),
        rating = rating,
        reviewCount = reviewCount,
        deliveryTime = deliveryTime,
        deliveryFee = deliveryFee,
        distance = distance,
        priceRange = priceRange,
        imageUrl = imageUrl,
        badges = badges.map { it.text },
        isOpen = isOpen,
        isFavorite = isFavorite,
        isFreeship = isFreeship,
        minimumOrder = minimumOrder
    )
}

fun CategoryMenuItemResponse.toDomainModel(): CategoryMenuItem {
    return CategoryMenuItem(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl,
        rating = rating,
        reviewCount = reviewCount,
        isPopular = isPopular,
        isSpicy = isSpicy,
        restaurantId = restaurantId,
        restaurantName = restaurant?.name ?: "",
        deliveryTime = restaurant?.deliveryTime ?: ""
    )
}