package com.app.tastybuds.ui.resturants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.data.MenuItem
import com.app.tastybuds.domain.FavoritesUseCase
import com.app.tastybuds.domain.GetMenuItemsUseCase
import com.app.tastybuds.domain.RestaurantDetailsUseCase
import com.app.tastybuds.ui.resturants.state.RestaurantDetailsUiState
import com.app.tastybuds.util.Result
import com.app.tastybuds.util.onError
import com.app.tastybuds.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantDetailsViewModel @Inject constructor(
    private val restaurantDetailsUseCase: RestaurantDetailsUseCase,
    private val favoritesUseCase: FavoritesUseCase,
    private val getMenuItemsUseCase: GetMenuItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantDetailsUiState())
    val uiState: StateFlow<RestaurantDetailsUiState> = _uiState.asStateFlow()

    private val _restaurantId = MutableStateFlow("")
    private val _userId = MutableStateFlow("")


    private val _allMenuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val allMenuItems: StateFlow<List<MenuItem>> = _allMenuItems.asStateFlow()

    private val _isLoadingMenu = MutableStateFlow(false)
    val isLoadingMenu: StateFlow<Boolean> = _isLoadingMenu.asStateFlow()

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

    fun toggleFavorite() = toggleRestaurantFavorite()

    fun retry() {
        if (_restaurantId.value.isNotEmpty() && _userId.value.isNotEmpty()) {
            loadRestaurantDetails(_restaurantId.value, _userId.value)
        }
    }

    private fun loadCompleteMenuItems(restaurantId: String) {
        viewModelScope.launch {
            _isLoadingMenu.value = true
            getMenuItemsUseCase(restaurantId).on { result ->
                when (result) {
                    is Result.Success -> {
                        _allMenuItems.value = result.data
                        _isLoadingMenu.value = false
                    }
                    is Result.Error -> {
                        _isLoadingMenu.value = false
                        // Handle error if needed
                    }
                    is Result.Loading -> {
                        _isLoadingMenu.value = true
                    }
                }
            }
        }
    }
}