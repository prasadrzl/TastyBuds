package com.app.tastybuds.ui.resturants.state

import com.app.tastybuds.domain.model.CategoryDetailsData
import com.app.tastybuds.domain.model.Restaurant

data class RestaurantUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val restaurants: List<Restaurant> = emptyList(),
    val categoryName: String = "",
    val searchQuery: String = "",
    val currentCategoryId: String? = null,
    val categoryDetails: CategoryDetailsData? = null,
    val isEmpty: Boolean = false
)