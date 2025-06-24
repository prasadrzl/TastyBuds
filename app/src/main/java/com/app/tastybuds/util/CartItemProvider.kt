package com.app.tastybuds.util

import com.app.tastybuds.domain.model.CartItem
import com.app.tastybuds.data.model.OrderItemSize
import com.app.tastybuds.data.model.OrderItemSpiceLevel
import com.app.tastybuds.data.model.OrderItemTopping
import com.app.tastybuds.domain.model.FoodDetailsData
import com.app.tastybuds.ui.resturants.state.FoodDetailsUiState

fun createCartItemFromUiState(
    foodData: FoodDetailsData,
    uiState: FoodDetailsUiState
): CartItem? {
    val hasRequiredSize =
        foodData.customization.sizes.isEmpty() || uiState.selectedSize.isNotEmpty()
    val hasRequiredSpice =
        foodData.customization.spiceLevels.isEmpty() || uiState.selectedSpiceLevel.isNotEmpty()

    if (!hasRequiredSize || !hasRequiredSpice) {
        return null
    }

    val selectedSizeOption = if (uiState.selectedSize.isNotEmpty()) {
        foodData.customization.sizes.find { it.id == uiState.selectedSize }?.let { size ->
            OrderItemSize(
                id = size.id,
                name = size.name,
                additionalPrice = size.additionalPrice.toDouble()
            )
        }
    } else null

    val selectedToppingOptions = uiState.selectedToppings.mapNotNull { toppingId ->
        foodData.customization.toppings.find { it.id == toppingId }?.let { topping ->
            OrderItemTopping(
                id = topping.id,
                name = topping.name,
                price = topping.price.toDouble()
            )
        }
    }

    val selectedSpiceLevelOption = if (uiState.selectedSpiceLevel.isNotEmpty()) {
        foodData.customization.spiceLevels.find { it.id == uiState.selectedSpiceLevel }
            ?.let { spice ->
                OrderItemSpiceLevel(
                    id = spice.id,
                    name = spice.name,
                    level = spice.level
                )
            }
    } else null

    return CartItem(
        menuItemId = foodData.foodDetails.id,
        name = foodData.foodDetails.name,
        image = foodData.foodDetails.imageUrl,
        basePrice = foodData.foodDetails.basePrice.toDouble(),
        selectedSize = selectedSizeOption,
        selectedToppings = selectedToppingOptions,
        selectedSpiceLevel = selectedSpiceLevelOption,
        quantity = uiState.quantity,
        notes = uiState.specialNote.ifBlank { null },
        restaurantId = foodData.foodDetails.restaurantId
    )
}