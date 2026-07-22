package com.app.tastybuds.ui.resturants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.domain.RestaurantUseCase
import com.app.tastybuds.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeeAllViewModel @Inject constructor(
    private val restaurantUseCase: RestaurantUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeeAllUiState())
    val uiState: StateFlow<SeeAllUiState> = _uiState.asStateFlow()

    fun loadItems(categoryId: String, type: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                when (type) {
                    "restaurants", "top_restaurants" -> {
                        loadTopRestaurants(categoryId)
                    }

                    "recommended_restaurants" -> {
                        loadRecommendedRestaurants(categoryId)
                    }

                    "menu_items", "popular_items" -> {
                        loadMenuItems(categoryId)
                    }

                    else -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Unknown content type: $type"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "An unexpected error occurred"
                )
            }
        }
    }

    private suspend fun loadTopRestaurants(categoryId: String) {
        when (val result = restaurantUseCase.getTopRestaurantsByCategory(categoryId)) {
            is Result.Success -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    restaurants = result.data,
                    error = null
                )
            }

            is Result.Error -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message
                )
            }

            is Result.Loading -> Unit
        }
    }

    private suspend fun loadRecommendedRestaurants(categoryId: String) {
        when (val result = restaurantUseCase.getRecommendedRestaurantsByCategory(categoryId)) {
            is Result.Success -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    restaurants = result.data,
                    error = null
                )
            }

            is Result.Error -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message
                )
            }

            is Result.Loading -> Unit
        }
    }

    private suspend fun loadMenuItems(categoryId: String) {
        when (val result = restaurantUseCase.getMenuItemsByCategory(categoryId)) {
            is Result.Success -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    menuItems = result.data,
                    error = null
                )
            }

            is Result.Error -> {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message
                )
            }

            is Result.Loading -> Unit
        }
    }

    fun retry(categoryId: String, type: String) {
        loadItems(categoryId, type)
    }
}