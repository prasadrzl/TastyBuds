package com.app.tastybuds.ui.resturants.state

import com.app.tastybuds.domain.model.*

data class FoodDetailsUiState(
    val isLoading: Boolean = false,
    val foodDetailsData: FoodDetailsData? = null,
    val error: String? = null,
    val selectedSize: String = "",
    val selectedToppings: List<String> = emptyList(),
    val selectedSpiceLevel: String = "",
    val quantity: Int = 1,
    val specialNote: String = "",
    val totalPrice: Float = 0.0f
)