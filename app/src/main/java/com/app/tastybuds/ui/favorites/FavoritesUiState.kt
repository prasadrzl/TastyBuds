package com.app.tastybuds.ui.favorites

data class FavoritesUiState(
    val isLoading: Boolean = false,

    val favoriteRestaurants: List<FavoriteRestaurantUi> = emptyList(),
    val favoriteMenuItems: List<FavoriteMenuItemUi> = emptyList(),

    val error: FavoriteError? = null,
    val errorDetails: String? = null
)

