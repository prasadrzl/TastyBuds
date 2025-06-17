package com.app.tastybuds.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.domain.FavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.app.tastybuds.util.onError
import com.app.tastybuds.util.onLoading
import com.app.tastybuds.util.onSuccess
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesUseCase: FavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    fun loadUserFavorites(userId: String) {
        viewModelScope.launch {
            favoritesUseCase.getUserFavorites(userId)
                .onSuccess { result ->
                    val restaurantFavorites = result.filter { it.restaurantId != null }
                    val menuItemFavorites = result.filter { it.menuItemId != null }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            favoriteRestaurants = restaurantFavorites,
                            favoriteMenuItems = menuItemFavorites,
                            error = null
                        )
                    }
                }
                .onError {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = it.error ?: "Unknown error"
                        )
                    }
                }
                .onLoading {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                }
        }
    }

    fun removeFavorite(favoriteId: Int, userId: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val favoriteToRemove = currentState.favoriteRestaurants.find { it.id == favoriteId }
                ?: currentState.favoriteMenuItems.find { it.id == favoriteId }

            if (favoriteToRemove != null) {
                favoritesUseCase.toggleMenuItemFavorite(
                    userId = userId,
                    menuItemId = favoriteToRemove.menuItemId ?: "",
                    restaurantId = favoriteToRemove.restaurantId
                ).onSuccess { result ->
                    loadUserFavorites(userId)
                }
                    .onError {
                        _uiState.update { it.copy(error = it.error ?: "Unknown error") }
                    }
                    .onLoading {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
            }
        }
    }
}