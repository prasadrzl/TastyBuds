package com.app.tastybuds.ui.favorites

import com.app.tastybuds.data.model.FavoriteResponse
import com.app.tastybuds.data.model.FavoriteWithMenuItemResponse
import com.app.tastybuds.data.model.FavoriteWithRestaurantResponse

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val favoriteRestaurants: List<FavoriteResponse> = emptyList(),
    val favoriteMenuItems: List<FavoriteResponse> = emptyList(),
    val favoriteRestaurantsWithDetails: List<FavoriteWithRestaurantResponse> = emptyList(),
    val favoriteMenuItemsWithDetails: List<FavoriteWithMenuItemResponse> = emptyList(),
    val error: String? = null
)