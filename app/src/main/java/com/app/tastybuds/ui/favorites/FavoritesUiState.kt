package com.app.tastybuds.ui.favorites

import com.app.tastybuds.data.model.FavoriteMenuItemUi
import com.app.tastybuds.data.model.FavoriteRestaurantUi
import com.app.tastybuds.data.model.FavoriteWithMenuItemResponse
import com.app.tastybuds.data.model.FavoriteWithRestaurantResponse

data class FavoritesUiState(
    val isLoading: Boolean = false,

    val favoriteRestaurants: List<FavoriteRestaurantUi> = emptyList(),
    val favoriteMenuItems: List<FavoriteMenuItemUi> = emptyList(),

    // Keep original detailed responses for potential future use
    val favoriteRestaurantsWithDetails: List<FavoriteWithRestaurantResponse> = emptyList(),
    val favoriteMenuItemsWithDetails: List<FavoriteWithMenuItemResponse> = emptyList(),

    val error: String? = null
)

