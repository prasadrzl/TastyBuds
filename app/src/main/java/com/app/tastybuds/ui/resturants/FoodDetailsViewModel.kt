package com.app.tastybuds.ui.resturants

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.domain.FoodDetailsUseCase
import com.app.tastybuds.domain.model.FoodCustomization
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
        if (foodItemId.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Invalid food item ID"
                )
            }
            return
        }

        _foodItemId.value = foodItemId
        Log.d("FoodDetailsVM", "Loading food details for ID: $foodItemId")

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val foodDetailsResult = foodDetailsUseCase.getFoodDetails(foodItemId)
                val customizationResult = foodDetailsUseCase.getCustomizationOptions(foodItemId)

                when (foodDetailsResult) {
                    is Result.Success -> {
                        val foodDetails = foodDetailsResult.data
                        val customization = when (customizationResult) {
                            is Result.Success -> customizationResult.data
                            else -> FoodCustomization()
                        }

                        val foodDetailsData = FoodDetailsData(
                            foodDetails = foodDetails,
                            customization = customization
                        )

                        // Get default selections
                        val defaultSize = customization.sizes.find { it.isDefault }?.id
                            ?: customization.sizes.firstOrNull()?.id ?: ""

                        val defaultToppings = customization.toppings
                            .filter { it.isDefault }
                            .map { it.id }

                        val defaultSpiceLevel = customization.spiceLevels.find { it.isDefault }?.id
                            ?: customization.spiceLevels.firstOrNull()?.id ?: ""

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                foodDetailsData = foodDetailsData,
                                error = null,
                                selectedSize = defaultSize,
                                selectedToppings = defaultToppings,
                                selectedSpiceLevel = defaultSpiceLevel,
                                totalPrice = calculateTotalPrice(
                                    foodDetailsData,
                                    defaultSize,
                                    defaultToppings,
                                    1
                                )
                            )
                        }

                        Log.d("FoodDetailsVM", "Successfully loaded food details: ${foodDetails.name}")
                    }

                    is Result.Error -> {
                        Log.e("FoodDetailsVM", "Error loading food details: ${foodDetailsResult.message}")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = foodDetailsResult.message
                            )
                        }
                    }

                    Result.Loading -> {}
                }

            } catch (e: Exception) {
                android.util.Log.e("FoodDetailsVM", "Exception in loadFoodDetails: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load food details: ${e.message}"
                    )
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
        val foodItemId = _foodItemId.value
        if (foodItemId.isNotEmpty()) {
            android.util.Log.d("FoodDetailsVM", "Retrying to load food details for ID: $foodItemId")
            loadFoodDetails(foodItemId)
        } else {
            android.util.Log.w("FoodDetailsVM", "Cannot retry - no food item ID available")
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "No food item ID available for retry"
                )
            }
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