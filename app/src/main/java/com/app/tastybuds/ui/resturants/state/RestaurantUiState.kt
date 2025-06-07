package com.app.tastybuds.ui.resturants.state

import com.app.tastybuds.domain.model.Restaurant

data class RestaurantUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val restaurants: List<Restaurant> = emptyList(),
    val categoryName: String = "",
    val searchQuery: String = "",
    val isEmpty: Boolean = false
)