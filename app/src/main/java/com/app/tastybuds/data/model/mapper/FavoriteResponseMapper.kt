package com.app.tastybuds.data.model.mapper

import com.app.tastybuds.data.model.FavoriteMenuItemUi
import com.app.tastybuds.data.model.FavoriteRestaurantUi
import com.app.tastybuds.data.model.FavoriteWithMenuItemResponse
import com.app.tastybuds.data.model.FavoriteWithRestaurantResponse


fun FavoriteWithRestaurantResponse.toRestaurantUiModel(): FavoriteRestaurantUi {
    val restaurantData = this.restaurant

    return FavoriteRestaurantUi(
        id = this.id,
        restaurantId = this.restaurantId ?: "",
        name = restaurantData?.name ?: "",
        cuisine = restaurantData?.cuisine?.joinToString(", ") ?: "",
        rating = restaurantData?.rating ?: 0.0f,
        reviewCount = restaurantData?.reviewCount ?: 0,
        deliveryTime = restaurantData?.deliveryTime ?: "",
        distance = restaurantData?.distance ?: "",
        priceRange = restaurantData?.priceRange ?: "",
        imageUrl = restaurantData?.imageUrl ?: "",
        deliveryFee = restaurantData?.deliveryFee ?: 0.0f,
        isOpen = restaurantData?.isOpen ?: true,
        createdAt = this.createdAt
    )
}

fun FavoriteWithMenuItemResponse.toMenuItemUiModel(): FavoriteMenuItemUi {
    val menuItemData = this.menuItem

    return FavoriteMenuItemUi(
        id = this.id,
        menuItemId = this.menuItemId ?: "",
        name = menuItemData?.name ?: "",
        description = menuItemData?.description ?: "",
        price = menuItemData?.price?.toFloat() ?: 0.0f,
        imageUrl = menuItemData?.image ?: "",
        rating = menuItemData?.rating ?: 0.0f,
        reviewCount = menuItemData?.reviewCount ?: 0,
        restaurantName = menuItemData?.restaurant?.name ?: "",
        restaurantId = menuItemData?.restaurant?.id ?: "",
        isPopular = menuItemData?.isPopular ?: false,
        isSpicy = menuItemData?.isSpicy ?: false,
        createdAt = this.createdAt
    )
}