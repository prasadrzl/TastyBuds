package com.app.tastybuds.ui.resturants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.data.model.RestaurantMenuItem
import com.app.tastybuds.domain.FavoritesUseCase
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
    private val favoritesUseCase: FavoritesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantDetailsUiState())
    val uiState: StateFlow<RestaurantDetailsUiState> = _uiState.asStateFlow()

    private val _restaurantId = MutableStateFlow("")
    private val _userId = MutableStateFlow("")

    private val _allMenuItems = MutableStateFlow<List<RestaurantMenuItem>>(emptyList())
    val allMenuItems: StateFlow<List<RestaurantMenuItem>> = _allMenuItems.asStateFlow()

    private val _isLoadingMenu = MutableStateFlow(false)
    val isLoadingMenu: StateFlow<Boolean> = _isLoadingMenu.asStateFlow()

    private val _filteredMenuItems = MutableStateFlow<List<RestaurantMenuItem>>(emptyList())
    val filteredMenuItems: StateFlow<List<RestaurantMenuItem>> = _filteredMenuItems.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    fun loadRestaurantDetails(restaurantId: String, userId: String) {
        _restaurantId.value = restaurantId
        _userId.value = userId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            restaurantDetailsUseCase(restaurantId, userId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                restaurantData = result.data,
                                error = null
                            )
                        }
                        loadMenuItems(restaurantId)
                    }

                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }

                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun loadMenuItems(restaurantId: String) {
        viewModelScope.launch {
            _isLoadingMenu.value = true

            when (val result = restaurantDetailsUseCase.getRestaurantMenuItems(restaurantId)) {
                is Result.Success -> {
                    _allMenuItems.value = result.data
                    _isLoadingMenu.value = false

                    val uniqueCategories = result.data
                        .map { it.category }
                        .filter { it.isNotEmpty() }
                        .distinct()
                        .sorted()
                    _categories.value = uniqueCategories

                    applyFilter(_selectedCategory.value)
                }

                is Result.Error -> {
                    _isLoadingMenu.value = false
                    _uiState.update { currentState ->
                        currentState.copy(error = "Failed to load menu: ${result.message}")
                    }
                }

                is Result.Loading -> {
                    _isLoadingMenu.value = true
                }
            }
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        applyFilter(category)
    }

    private fun applyFilter(category: String) {
        val filtered = if (category == "All") {
            _allMenuItems.value
        } else {
            _allMenuItems.value.filter { it.category == category }
        }
        _filteredMenuItems.value = filtered
    }

    fun toggleFavorite() {
        val restaurantId = _restaurantId.value
        val userId = _userId.value

        if (restaurantId.isNotEmpty() && userId.isNotEmpty()) {
            viewModelScope.launch {
                favoritesUseCase.toggleRestaurantFavorite(userId, restaurantId)
                    .onSuccess { isFavorite ->
                        _uiState.update { currentState ->
                            currentState.restaurantData?.let { data ->
                                val updatedRestaurant =
                                    data.restaurant.copy(isFavorite = isFavorite)
                                val updatedData = data.copy(restaurant = updatedRestaurant)
                                currentState.copy(restaurantData = updatedData)
                            } ?: currentState
                        }
                    }
                    .onError { error -> }
            }
        }
    }

    fun toggleMenuItemFavorite(menuItemId: String) {
        val userId = _userId.value
        val restaurantId = _restaurantId.value

        if (userId.isNotEmpty() && menuItemId.isNotEmpty()) {
            viewModelScope.launch {
                favoritesUseCase.toggleMenuItemFavorite(userId, menuItemId, restaurantId)
                    .onSuccess { isFavorite ->
                        _allMenuItems.update { items ->
                            items.map { item ->
                                if (item.id == menuItemId) {
                                    item.copy(isFavorite = isFavorite)
                                } else {
                                    item
                                }
                            }
                        }

                        applyFilter(_selectedCategory.value)
                    }
                    .onError { error -> }
            }
        }
    }

    fun retry() {
        val restaurantId = _restaurantId.value
        val userId = _userId.value
        if (restaurantId.isNotEmpty() && userId.isNotEmpty()) {
            loadRestaurantDetails(restaurantId, userId)
        }
    }
}