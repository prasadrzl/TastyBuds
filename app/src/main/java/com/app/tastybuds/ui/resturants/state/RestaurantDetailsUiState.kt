package com.app.tastybuds.ui.resturants.state

import com.app.tastybuds.data.model.RestaurantDetailsData

data class RestaurantDetailsUiState(
    val isLoading: Boolean = false,
    val restaurantData: RestaurantDetailsData? = null,
    val error: String? = null,
    val isFavorite: Boolean = false,
    val voucherCount: Int = 0
)
