package com.app.tastybuds.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.data.model.FavoriteMenuItemUi
import com.app.tastybuds.data.model.FavoriteRestaurantUi
import com.app.tastybuds.domain.FavoritesUseCase
import com.app.tastybuds.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            _uiState.value = _uiState.value.setLoading()

            val error = handleRemoveFavorite(favoriteId, userId)

            if (error != null) {
                _uiState.value = _uiState.value.setError(error)
            } else {
                loadUserFavoritesWithDetails(userId)
            }
        }
    }

    fun loadUserFavoritesWithDetails(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.setLoading()

            try {
                val restaurantResult = favoritesUseCase.getFavoriteRestaurantsForUI(userId)
                val menuItemResult = favoritesUseCase.getFavoriteMenuItemsForUI(userId)

                handleLoadResults(restaurantResult, menuItemResult)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.setError(
                    FavoriteError.LOAD_FAVORITES_FAILED,
                    e.message
                )
            }
        }
    }

    private suspend fun handleRemoveFavorite(favoriteId: Int, userId: String): FavoriteError? {
        val currentState = _uiState.value

        currentState.findRestaurantFavorite(favoriteId)?.let { restaurant ->
            val (_, error) = favoritesUseCase.toggleRestaurantFavorite(
                userId,
                restaurant.restaurantId
            )
                .mapToFavoriteError(FavoriteError.REMOVE_RESTAURANT_FAILED)
            return error
        }

        currentState.findMenuItemFavorite(favoriteId)?.let { menuItem ->
            val (_, error) = favoritesUseCase.toggleMenuItemFavorite(userId, menuItem.menuItemId)
                .mapToFavoriteError(FavoriteError.REMOVE_MENU_ITEM_FAILED)
            return error
        }

        return FavoriteError.FAVORITE_NOT_FOUND
    }

    private fun handleLoadResults(
        restaurantResult: Result<List<FavoriteRestaurantUi>>,
        menuItemResult: Result<List<FavoriteMenuItemUi>>
    ) {
        val (restaurants, restaurantError) = restaurantResult.mapToFavoriteError(FavoriteError.LOAD_FAVORITES_FAILED)
        val (menuItems, menuItemError) = menuItemResult.mapToFavoriteError(FavoriteError.LOAD_FAVORITES_FAILED)

        val error = restaurantError ?: menuItemError

        _uiState.value = if (error != null) {
            _uiState.value.setError(error)
        } else {
            _uiState.value.setSuccess(restaurants ?: emptyList(), menuItems ?: emptyList())
        }
    }
}

private fun FavoritesUiState.setLoading() =
    copy(isLoading = true, error = null, errorDetails = null)

private fun FavoritesUiState.setError(error: FavoriteError, details: String? = null) =
    copy(isLoading = false, error = error, errorDetails = details)

private fun FavoritesUiState.setSuccess(
    restaurants: List<FavoriteRestaurantUi>,
    menuItems: List<FavoriteMenuItemUi>
) = copy(
    isLoading = false,
    error = null,
    errorDetails = null,
    favoriteRestaurants = restaurants,
    favoriteMenuItems = menuItems,
    favoriteRestaurantsWithDetails = emptyList(),
    favoriteMenuItemsWithDetails = emptyList()
)

private fun FavoritesUiState.findRestaurantFavorite(favoriteId: Int): FavoriteRestaurantUi? =
    favoriteRestaurants.find { it.id == favoriteId }

private fun FavoritesUiState.findMenuItemFavorite(favoriteId: Int): FavoriteMenuItemUi? =
    favoriteMenuItems.find { it.id == favoriteId }

private fun <T> Result<T>.mapToFavoriteError(errorType: FavoriteError): Pair<T?, FavoriteError?> =
    when (this) {
        is Result.Success -> Pair(data, null)
        is Result.Error -> Pair(null, errorType)
        is Result.Loading -> Pair(null, null)
    }