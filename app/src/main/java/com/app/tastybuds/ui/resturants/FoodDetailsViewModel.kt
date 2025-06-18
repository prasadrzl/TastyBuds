package com.app.tastybuds.ui.resturants

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.data.repo.AuthRepository
import com.app.tastybuds.domain.FavoritesUseCase
import com.app.tastybuds.domain.FoodDetailsUseCase
import com.app.tastybuds.domain.model.FoodCustomization
import com.app.tastybuds.domain.model.FoodDetailsData
import com.app.tastybuds.ui.resturants.state.FoodDetailsUiState
import com.app.tastybuds.util.Result
import com.app.tastybuds.util.calculateTotalPrice
import com.app.tastybuds.util.onError
import com.app.tastybuds.util.onLoading
import com.app.tastybuds.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodDetailsViewModel @Inject constructor(
    private val foodDetailsUseCase: FoodDetailsUseCase,
    private val favoritesUseCase: FavoritesUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodDetailsUiState())
    val uiState: StateFlow<FoodDetailsUiState> = _uiState.asStateFlow()

    private val _foodItemId = MutableStateFlow("")
    private val _userId = MutableStateFlow("")

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
                _userId.value = authRepository.getUserId().first() ?: ""
                _uiState.update { it.copy(isLoading = true, error = null) }

                val foodDetailsResult = foodDetailsUseCase.getFoodDetails(foodItemId, userId = _userId.value)
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
                    }

                    is Result.Error -> {
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
            loadFoodDetails(foodItemId)
        } else {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "No food item ID available for retry"
                )
            }
        }
    }

    fun toggleFavorite() {
        if (_foodItemId.value.isEmpty() || _userId.value.isEmpty()) return

        viewModelScope.launch {
            val currentFoodData = _uiState.value.foodDetailsData
            if (currentFoodData != null) {
                val updatedFoodDetails = currentFoodData.foodDetails.copy(
                    isFavorite = !currentFoodData.foodDetails.isFavorite
                )
                _uiState.update {
                    it.copy(
                        foodDetailsData = currentFoodData.copy(foodDetails = updatedFoodDetails)
                    )
                }

                favoritesUseCase.toggleMenuItemFavorite(
                    userId = _userId.value,
                    menuItemId = _foodItemId.value
                ).onSuccess { result ->

                    val revertedFoodDetails = updatedFoodDetails.copy(
                        isFavorite = result
                    )
                    _uiState.update {
                        it.copy(
                            foodDetailsData = currentFoodData.copy(foodDetails = revertedFoodDetails)
                        )
                    }
                }.onError {
                    _uiState.update {
                        it.copy(error = it.error)
                    }
                }
                    .onLoading {

                    }
            }
        }
    }
}