package com.app.tastybuds.ui.favorites

import com.app.tastybuds.data.model.FavoriteResponse

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val favoriteRestaurants: List<FavoriteResponse> = emptyList(),
    val favoriteMenuItems: List<FavoriteResponse> = emptyList(),
    val error: String? = null
)