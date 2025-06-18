package com.app.tastybuds.ui.resturants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.domain.FavoritesUseCase
import com.app.tastybuds.domain.RestaurantDetailsUseCase
import com.app.tastybuds.ui.resturants.state.RestaurantDetailsUiState
import com.app.tastybuds.util.Result
import com.app.tastybuds.util.onError
import com.app.tastybuds.util.onLoading
import com.app.tastybuds.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantDetailsViewModel @Inject constructor(
    private val restaurantDetailsUseCase: RestaurantDetailsUseCase,
    private val favoritesUseCase: FavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantDetailsUiState())
    val uiState: StateFlow<RestaurantDetailsUiState> = _uiState.asStateFlow()

    private val _restaurantId = MutableStateFlow("")
    private val _userId = MutableStateFlow("")

    fun loadRestaurantDetails(restaurantId: String, userId: String) {
        _restaurantId.value = restaurantId
        _userId.value = userId

        viewModelScope.launch {
            restaurantDetailsUseCase(restaurantId, userId)
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _uiState.update { it.copy(isLoading = true, error = null) }
                        }

                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    restaurantData = result.data,
                                    error = null,
                                    voucherCount = result.data.vouchers.size,
                                    isFavorite = result.data.restaurant.isFavorite
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

    private fun toggleRestaurantFavorite() {
        if (_restaurantId.value.isEmpty() || _userId.value.isEmpty()) return

        viewModelScope.launch {
            val currentIsFavorite = _uiState.value.isFavorite
            _uiState.update { it.copy(isFavorite = !currentIsFavorite) }

            favoritesUseCase.toggleRestaurantFavorite(
                userId = _userId.value,
                restaurantId = _restaurantId.value
            ).onSuccess { isNowFavorite ->
                _uiState.update { state ->
                    state.copy(
                        isFavorite = isNowFavorite,
                        error = null
                    )
                }
            }
                .onError {
                    _uiState.update {
                        it.copy(
                            isFavorite = currentIsFavorite,
                            error = it.error
                        )
                    }
                }
        }
    }

    fun toggleMenuItemFavorite(menuItemId: String) {
        if (_userId.value.isEmpty()) return

        viewModelScope.launch {
            val currentData = _uiState.value.restaurantData
            if (currentData != null) {
                // Find and update the menu item optimistically
                val updatedMenuItems = currentData.menuItems.map { item ->
                    if (item.id == menuItemId) {
                        item.copy(isFavorite = !item.isFavorite)
                    } else {
                        item
                    }
                }

                val updatedForYouItems = currentData.forYouItems.map { item ->
                    if (item.id == menuItemId) {
                        item.copy(isFavorite = !item.isFavorite)
                    } else {
                        item
                    }
                }

                _uiState.update {
                    it.copy(
                        restaurantData = currentData.copy(
                            menuItems = updatedMenuItems,
                            forYouItems = updatedForYouItems
                        )
                    )
                }

                favoritesUseCase.toggleMenuItemFavorite(
                    userId = _userId.value,
                    menuItemId = menuItemId,
                    restaurantId = _restaurantId.value
                ).onError { result ->
                    _uiState.update {
                        it.copy(
                            restaurantData = currentData,
                            error = it.error
                        )
                    }
                }
                    .onSuccess { result ->

                    }
                    .onLoading {

                    }
            }
        }
    }

    fun toggleFavorite() = toggleRestaurantFavorite()

    fun retry() {
        if (_restaurantId.value.isNotEmpty() && _userId.value.isNotEmpty()) {
            loadRestaurantDetails(_restaurantId.value, _userId.value)
        }
    }
}