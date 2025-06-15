package com.app.tastybuds.ui.resturants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.domain.FoodDetailsUseCase
import com.app.tastybuds.domain.model.FoodDetailsData
import com.app.tastybuds.ui.resturants.state.FoodDetailsUiState
import com.app.tastybuds.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodDetailsViewModel @Inject constructor(
    private val foodDetailsUseCase: FoodDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodDetailsUiState())
    val uiState: StateFlow<FoodDetailsUiState> = _uiState.asStateFlow()

    private val _foodItemId = MutableStateFlow("")

    fun loadFoodDetails(foodItemId: String) {
        _foodItemId.value = foodItemId

        viewModelScope.launch {
            foodDetailsUseCase(foodItemId)
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _uiState.update { it.copy(isLoading = true, error = null) }
                        }

                        is Result.Success -> {
                            val data = result.data
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    foodDetailsData = data,
                                    error = null,
                                    selectedSize = data.customization.sizes.find { size -> size.isDefault }?.id
                                        ?: data.customization.sizes.firstOrNull()?.id ?: "",
                                    selectedToppings = data.customization.toppings.filter { topping -> topping.isDefault }
                                        .map { it.id },
                                    selectedSpiceLevel = data.customization.spiceLevels.find { spice -> spice.isDefault }?.id
                                        ?: data.customization.spiceLevels.firstOrNull()?.id ?: "",
                                    totalPrice = calculateTotalPrice(
                                        data,
                                        it.selectedSize,
                                        it.selectedToppings,
                                        it.quantity
                                    )
                                )
                            }
                        }

                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
        }
    }

    fun updateSelectedSize(sizeId: String) {
        _uiState.update { state ->
            val newTotalPrice = state.foodDetailsData?.let { data ->
                calculateTotalPrice(data, sizeId, state.selectedToppings, state.quantity)
            } ?: 0.0f

            state.copy(
                selectedSize = sizeId,
                totalPrice = newTotalPrice
            )
        }
    }

    fun updateSelectedToppings(toppings: List<String>) {
        _uiState.update { state ->
            val newTotalPrice = state.foodDetailsData?.let { data ->
                calculateTotalPrice(data, state.selectedSize, toppings, state.quantity)
            } ?: 0.0f

            state.copy(
                selectedToppings = toppings,
                totalPrice = newTotalPrice
            )
        }
    }

    fun updateSelectedSpiceLevel(spiceLevelId: String) {
        _uiState.update { it.copy(selectedSpiceLevel = spiceLevelId) }
    }

    fun updateQuantity(quantity: Int) {
        if (quantity > 0) {
            _uiState.update { state ->
                val newTotalPrice = state.foodDetailsData?.let { data ->
                    calculateTotalPrice(data, state.selectedSize, state.selectedToppings, quantity)
                } ?: 0.0f

                state.copy(
                    quantity = quantity,
                    totalPrice = newTotalPrice
                )
            }
        }
    }

    fun updateSpecialNote(note: String) {
        _uiState.update { it.copy(specialNote = note) }
    }

    fun retry() {
        if (_foodItemId.value.isNotEmpty()) {
            loadFoodDetails(_foodItemId.value)
        }
    }

    private fun calculateTotalPrice(
        data: FoodDetailsData,
        sizeId: String,
        toppingIds: List<String>,
        quantity: Int
    ): Float {
        val basePrice = data.foodDetails.basePrice

        val sizePrice = data.customization.sizes
            .find { it.id == sizeId }?.additionalPrice ?: 0.0f

        val toppingsPrice = data.customization.toppings
            .filter { it.id in toppingIds }
            .sumOf { it.price.toDouble() }.toFloat()

        return (basePrice + sizePrice + toppingsPrice) * quantity
    }
}