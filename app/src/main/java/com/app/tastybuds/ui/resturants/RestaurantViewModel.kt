package com.app.tastybuds.ui.resturants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.domain.RestaurantUseCase
import com.app.tastybuds.ui.resturants.state.RestaurantUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class RestaurantViewModel @Inject constructor(
    private val restaurantUseCase: RestaurantUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantUiState())
    val uiState: StateFlow<RestaurantUiState> = _uiState.asStateFlow()

    fun loadRestaurantsByCategory(categoryId: String, categoryName: String) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    error = null, 
                    categoryName = categoryName,
                    searchQuery = ""
                ) 
            }
            
            try {
                restaurantUseCase.getRestaurantsByCategory(categoryId).collect { restaurants ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            restaurants = restaurants,
                            isEmpty = restaurants.isEmpty()
                        )
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun searchRestaurants(query: String) {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    error = null,
                    searchQuery = query,
                    categoryName = ""
                ) 
            }
            
            try {
                restaurantUseCase.searchRestaurants(query).collect { restaurants ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            restaurants = restaurants,
                            isEmpty = restaurants.isEmpty()
                        )
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    fun loadAllRestaurants() {
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    error = null,
                    categoryName = "All Restaurants",
                    searchQuery = ""
                ) 
            }
            
            try {
                restaurantUseCase.getAllRestaurants().collect { restaurants ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            restaurants = restaurants,
                            isEmpty = restaurants.isEmpty()
                        )
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun handleError(e: Exception) {
        _uiState.update {
            it.copy(
                isLoading = false,
                error = when (e) {
                    is IOException -> "Network error. Please check your connection."
                    else -> e.localizedMessage ?: "An unknown error occurred"
                }
            )
        }
    }

    fun retry() {
        val currentState = _uiState.value
        when {
            currentState.categoryName.isNotBlank() && currentState.categoryName != "All Restaurants" -> {
                loadRestaurantsByCategory("cat_004", currentState.categoryName)
            }
            currentState.searchQuery.isNotBlank() -> {
                searchRestaurants(currentState.searchQuery)
            }
            else -> {
                loadAllRestaurants()
            }
        }
    }
}