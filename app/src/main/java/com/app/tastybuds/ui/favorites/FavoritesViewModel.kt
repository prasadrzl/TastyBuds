package com.app.tastybuds.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.data.model.FavoriteWithMenuItemResponse
import com.app.tastybuds.data.model.FavoriteWithRestaurantResponse
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
import com.app.tastybuds.util.Result

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesUseCase: FavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

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
                    loadUserFavoritesWithDetails(userId)
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

    fun loadUserFavoritesWithDetails(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val restaurantFavoritesResult =
                    favoritesUseCase.getFavoriteRestaurantsWithDetails(userId)

                val menuItemFavoritesResult =
                    favoritesUseCase.getFavoriteMenuItemsWithDetails(userId)

                var restaurantFavorites = emptyList<FavoriteWithRestaurantResponse>()
                var menuItemFavorites = emptyList<FavoriteWithMenuItemResponse>()
                var error: String? = null

                when (restaurantFavoritesResult) {
                    is Result.Success -> restaurantFavorites = restaurantFavoritesResult.data
                    is Result.Error -> error = restaurantFavoritesResult.message
                    else -> {}
                }

                when (menuItemFavoritesResult) {
                    is Result.Success -> menuItemFavorites = menuItemFavoritesResult.data
                    is Result.Error -> error = error ?: menuItemFavoritesResult.message
                    else -> {}
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        favoriteRestaurantsWithDetails = restaurantFavorites,
                        favoriteMenuItemsWithDetails = menuItemFavorites,
                        error = error
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load favorites: ${e.message}"
                    )
                }
            }
        }
    }
}