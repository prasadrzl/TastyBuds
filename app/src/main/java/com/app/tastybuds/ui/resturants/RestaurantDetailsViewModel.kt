package com.app.tastybuds.ui.resturants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.domain.RestaurantDetailsUseCase
import com.app.tastybuds.ui.resturants.state.RestaurantDetailsUiState
import com.app.tastybuds.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantDetailsViewModel @Inject constructor(
    private val restaurantDetailsUseCase: RestaurantDetailsUseCase
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
                                    voucherCount = result.data.vouchers.size
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

    fun toggleFavorite() {
        _uiState.update { it.copy(isFavorite = !it.isFavorite) }
    }

    fun retry() {
        if (_restaurantId.value.isNotEmpty() && _userId.value.isNotEmpty()) {
            loadRestaurantDetails(_restaurantId.value, _userId.value)
        }
    }
}