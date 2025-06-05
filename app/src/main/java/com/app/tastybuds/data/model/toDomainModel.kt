package com.app.tastybuds.data.model

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.app.tastybuds.domain.model.Banner
import com.app.tastybuds.domain.model.Category
import com.app.tastybuds.domain.model.Deal
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.domain.model.Collection as FoodCollection

fun BannerResponse.toDomainModel(): Banner {
    return Banner(
        id = id,
        title = title,
        price = discount,
        description = description,
        imageUrl = imageUrl,
        backgroundColor = parseColor(backgroundColorHex)
    )
}

fun CategoryResponse.toDomainModel(): Category {
    return Category(
        id = id,
        name = name,
        imageUrl = icon,
        backgroundColor = parseColor(backgroundColorHex)
    )
}

fun CollectionResponse.toDomainModel(): FoodCollection {
    return FoodCollection(
        id = id,
        title = title,
        subtitle = subtitle,
        imageUrl = imageUrl,
        badge = badge
    )
}

fun RestaurantResponse.toDomainModel(): Restaurant {
    return Restaurant(
        id = id,
        name = name,
        cuisine = cuisine.joinToString(", "),
        rating = rating,
        reviewCount = reviewCount,
        deliveryTime = deliveryTime,
        distance = distance,
        imageUrl = imageUrl,
        badge = badges.firstOrNull()?.text
    )
}

fun DealResponse.toDomainModel(): Deal {
    return Deal(
        id = id,
        title = title,
        price = price,
        originalPrice = originalPrice,
        imageUrl = imageUrl,
        badge = badge,
        discountPercentage = discountPercentage
    )
}

fun parseColor(colorHex: String): Color {
    return try {
        Color(colorHex.toColorInt())
    } catch (e: Exception) {
        Color(0xFFFF7700)
    }
}