package com.app.tastybuds.data.model.mapper

import com.app.tastybuds.data.model.FavoriteMenuItemUi
import com.app.tastybuds.data.model.FavoriteWithMenuItemResponse
import com.app.tastybuds.data.model.FavoriteWithRestaurantResponse
import com.app.tastybuds.data.model.FavoriteRestaurantUi as FavoriteRestaurantUi

private fun FavoriteWithRestaurantResponse.toRestaurantUiModel(): FavoriteRestaurantUi {
    val restaurantData = this.restaurant

    return FavoriteRestaurantUi(
        id = this.id,
        restaurantId = this.restaurantId ?: "",
        name = restaurantData?.name ?: "Unknown Restaurant",
        cuisine = restaurantData?.cuisine?.joinToString(", ") ?: "Various Cuisine",
        rating = restaurantData?.rating ?: 0.0f,
        reviewCount = restaurantData?.reviewCount ?: 0,
        deliveryTime = restaurantData?.deliveryTime ?: "N/A",
        distance = restaurantData?.distance ?: "Unknown",
        priceRange = restaurantData?.priceRange ?: "$",
        imageUrl = restaurantData?.imageUrl ?: "",
        deliveryFee = restaurantData?.deliveryFee ?: 0.0f,
        isOpen = restaurantData?.isOpen ?: true,
        createdAt = this.createdAt
    )
}

/**
 * Maps API FavoriteWithMenuItemResponse to UI FavoriteMenuItemUi
 */
private fun FavoriteWithMenuItemResponse.toMenuItemUiModel(): FavoriteMenuItemUi {
    val menuItemData = this.menuItem

    return FavoriteMenuItemUi(
        id = this.id,
        menuItemId = this.menuItemId ?: "",
        name = menuItemData?.name ?: "Unknown Item",
        description = menuItemData?.description ?: "",
        price = menuItemData?.price?.toFloat() ?: 0.0f,
        imageUrl = menuItemData?.image ?: "",
        rating = menuItemData?.rating ?: 0.0f,
        reviewCount = menuItemData?.reviewCount ?: 0,
        restaurantName = menuItemData?.restaurant?.name ?: "Unknown Restaurant",
        restaurantId = menuItemData?.restaurant?.id ?: "",
        isPopular = menuItemData?.isPopular ?: false,
        isSpicy = menuItemData?.isSpicy ?: false,
        createdAt = this.createdAt
    )
}