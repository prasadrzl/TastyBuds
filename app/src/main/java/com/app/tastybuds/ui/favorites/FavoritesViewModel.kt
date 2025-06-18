package com.app.tastybuds.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.data.model.FavoriteMenuItemUi
import com.app.tastybuds.data.model.FavoriteRestaurantUi
import com.app.tastybuds.domain.FavoritesUseCase
import com.app.tastybuds.util.Result
import com.app.tastybuds.util.onError
import com.app.tastybuds.util.onLoading
import com.app.tastybuds.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesUseCase: FavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    fun removeFavorite(favoriteId: Int, userId: String) {
        viewModelScope.launch {
            val currentState = _uiState.value

            // Find favorite to remove from UI models
            val restaurantFavorite = currentState.favoriteRestaurants.find { it.id == favoriteId }
            val menuItemFavorite = currentState.favoriteMenuItems.find { it.id == favoriteId }

            when {
                restaurantFavorite != null -> {
                    // Remove restaurant favorite
                    favoritesUseCase.toggleRestaurantFavorite(
                        userId = userId,
                        restaurantId = restaurantFavorite.restaurantId
                    ).onSuccess {
                        loadUserFavoritesWithDetails(userId)
                    }.onError {
                        _uiState.update { it.copy(error = it.error ?: "Failed to remove restaurant favorite") }
                    }.onLoading {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                }

                menuItemFavorite != null -> {
                    // Remove menu item favorite
                    favoritesUseCase.toggleMenuItemFavorite(
                        userId = userId,
                        menuItemId = menuItemFavorite.menuItemId,
                        restaurantId = menuItemFavorite.restaurantId
                    ).onSuccess {
                        loadUserFavoritesWithDetails(userId)
                    }.onError {
                        _uiState.update { it.copy(error = it.error ?: "Failed to remove menu item favorite") }
                    }.onLoading {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                }

                else -> {
                    _uiState.update { it.copy(error = "Favorite item not found") }
                }
            }
        }
    }

    fun loadUserFavoritesWithDetails(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // 🔧 CLEANED: Now UseCase handles mapping, ViewModel just consumes UI models
                val restaurantFavoritesResult = favoritesUseCase.getFavoriteRestaurantsForUI(userId)
                val menuItemFavoritesResult = favoritesUseCase.getFavoriteMenuItemsForUI(userId)

                var restaurantUiModels = emptyList<FavoriteRestaurantUi>()
                var menuItemUiModels = emptyList<FavoriteMenuItemUi>()
                var error: String? = null

                when (restaurantFavoritesResult) {
                    is Result.Success -> restaurantUiModels = restaurantFavoritesResult.data
                    is Result.Error -> error = restaurantFavoritesResult.message
                    else -> {}
                }

                when (menuItemFavoritesResult) {
                    is Result.Success -> menuItemUiModels = menuItemFavoritesResult.data
                    is Result.Error -> error = error ?: menuItemFavoritesResult.message
                    else -> {}
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        // 🔧 CLEAN: Direct assignment of UI models from UseCase
                        favoriteRestaurants = restaurantUiModels,
                        favoriteMenuItems = menuItemUiModels,
                        // Note: WithDetails can be removed if not needed elsewhere
                        favoriteRestaurantsWithDetails = emptyList(),
                        favoriteMenuItemsWithDetails = emptyList(),
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