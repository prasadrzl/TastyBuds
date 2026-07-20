package com.app.tastybuds.data.model.mapper

import com.app.tastybuds.data.model.BadgeResponse
import com.app.tastybuds.data.model.BannerResponse
import com.app.tastybuds.data.model.CategoryResponse
import com.app.tastybuds.data.model.CollectionResponse
import com.app.tastybuds.data.model.DealResponse
import com.app.tastybuds.data.model.RestaurantResponse
import com.app.tastybuds.domain.model.Badge
import com.app.tastybuds.domain.model.Banner
import com.app.tastybuds.domain.model.Category
import com.app.tastybuds.domain.model.Deal
import com.app.tastybuds.domain.model.Restaurant
import java.util.Locale
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
        badge = badge,
        restaurantIds = restaurantIds
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

fun DealResponse.toDeal(): Deal {
    return Deal(
        id = id,
        menuItemId = menuItemId,
        title = name,
        description = description,
        originalPrice = "$${String.format(Locale.getDefault(), "%.2f", originalPrice)}",
        salePrice = "$${String.format(Locale.getDefault(), "%.2f", salePrice)}",
        discountPercentage = discountPercent,
        imageUrl = image,
        restaurantId = restaurantId,
        restaurantName = restaurantName,
        deliveryTime = deliveryTime,
        rating = rating.toFloat(),
        badges = badges.map { it.toBadge() },
        createdAt = createdAt
    )
}

fun BadgeResponse.toBadge(): Badge {
    return Badge(
        text = text,
        type = type,
        backgroundColor = backgroundColor
    )
}

// Pure-Kotlin ARGB hex parser (no Android/Compose dependency) so this mapper can live in
// :core:common. Accepts "#RRGGBB" or "#AARRGGBB"; defaults alpha to opaque when omitted.
fun parseColor(colorHex: String): Int {
    return try {
        val cleaned = colorHex.removePrefix("#")
        val argb = if (cleaned.length == 6) "FF$cleaned" else cleaned
        argb.toLong(16).toInt()
    } catch (e: Exception) {
        0xFFFF7700.toInt()
    }
}