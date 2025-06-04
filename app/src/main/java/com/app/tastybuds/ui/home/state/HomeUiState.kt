package com.app.tastybuds.ui.home.state

import com.app.tastybuds.domain.model.Banner
import com.app.tastybuds.domain.model.Category
import com.app.tastybuds.domain.model.Collection
import com.app.tastybuds.domain.model.Deal
import com.app.tastybuds.domain.model.Restaurant

data class HomeUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val banners: List<Banner> = emptyList(),
    val categories: List<Category> = emptyList(),
    val voucherCount: Int = 0,
    val collections: List<Collection> = emptyList(),
    val recommendedRestaurants: List<Restaurant> = emptyList(),
    val deals: List<Deal> = emptyList()
)