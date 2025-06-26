package com.app.tastybuds.ui.resturants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.domain.RestaurantUseCase
import com.app.tastybuds.domain.model.Collection
import com.app.tastybuds.domain.model.Restaurant
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
class CollectionListingViewModel @Inject constructor(
    private val restaurantUseCase: RestaurantUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionListingUiState())
    val uiState: StateFlow<CollectionListingUiState> = _uiState.asStateFlow()


    fun loadCollectionRestaurants(restaurantIds: String) {
        viewModelScope.launch {
            try {
                restaurantUseCase.getRestaurantsByIds(restaurantIds).onSuccess { restaurants ->
                    _uiState.update {
                        it.copy(
                            isLoading = false, restaurants = restaurants, error = null
                        )
                    }
                }.onError { errorMessage ->
                    _uiState.update {
                        it.copy(
                            isLoading = false, error = errorMessage
                        )
                    }
                }.onLoading {
                    _uiState.update { it.copy(isLoading = true) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Failed to load restaurants"
                    )
                }
            }
        }
    }

    fun retry(restaurantIds: String) {
        loadCollectionRestaurants(restaurantIds)
    }
}

data class CollectionListingUiState(
    val isLoading: Boolean = false,
    val collection: Collection? = null,
    val restaurants: List<Restaurant> = emptyList(),
    val error: String? = null
)