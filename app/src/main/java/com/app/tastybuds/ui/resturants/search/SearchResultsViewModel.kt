package com.app.tastybuds.ui.resturants.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.domain.model.SearchResult
import com.app.tastybuds.domain.SearchResultsUseCase
import com.app.tastybuds.ui.resturants.state.SearchUiState
import com.app.tastybuds.util.isRestaurantNear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultsViewModel @Inject constructor(
    private val searchResultsUseCase: SearchResultsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var originalSearchResults: List<SearchResult> = emptyList()

    fun searchMenuItems(query: String = "") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            searchResultsUseCase.searchMenuItemsGroupedByRestaurant(query)
                .collect { results ->
                    originalSearchResults = results

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            searchResults = results,
                            isEmpty = results.isEmpty(),
                            error = null
                        )
                    }
                    applyFiltersAndSorting()
                }
        }
    }

    fun updateSortOption(sortId: String) {
        _uiState.update { it.copy(selectedSortId = sortId) }
        applyFiltersAndSorting()
    }

    fun toggleFilter(filter: String) {
        val currentFilters = _uiState.value.selectedFilters
        val newFilters = if (currentFilters.contains(filter)) {
            currentFilters - filter
        } else {
            currentFilters + filter
        }
        _uiState.update { it.copy(selectedFilters = newFilters) }
        applyFiltersAndSorting()
    }

    fun clearAllFilters() {
        _uiState.update {
            it.copy(
                selectedFilters = emptySet(),
                selectedSortId = SortOptionIds.RELEVANCE
            )
        }
        applyFiltersAndSorting()
    }

    private fun applyFiltersAndSorting() {
        val currentState = _uiState.value

        val filteredResults = applyFilters(currentState.searchResults, currentState.selectedFilters)

        val sortedResults = applySorting(filteredResults, currentState.selectedSortId)

        _uiState.update {
            it.copy(filteredAndSortedResults = sortedResults)
        }
    }

    private fun applyFilters(
        results: List<SearchResult>,
        selectedBadges: Set<String>
    ): List<SearchResult> {
        if (selectedBadges.isEmpty()) {
            return results
        }

        return results.filter { searchResult ->
            val restaurant = searchResult.restaurant ?: return@filter false

            selectedBadges.all { badgeId ->
                when (badgeId) {
                    "freeship" -> {
                        restaurant.badges.any { it.lowercase().contains("freeship") } ||
                                restaurant.deliveryFee <= 0.0
                    }

                    "favorite" -> {
                        restaurant.isFavorite
                    }

                    "near_you" -> {
                        isRestaurantNear(restaurant.distance)
                    }

                    "partner" -> {
                        restaurant.badges.any { badge ->
                            val lowerBadge = badge.lowercase()
                            lowerBadge.contains("partner") || lowerBadge.contains("verified")
                        }
                    }

                    else -> {
                        restaurant.badges.any {
                            it.lowercase().replace(" ", "_") == badgeId.lowercase()
                        }
                    }
                }
            }
        }
    }

    private fun applySorting(
        results: List<SearchResult>,
        sortId: String
    ): List<SearchResult> {
        return when (sortId) {
            SortOptionIds.RATING -> {
                results.sortedWith(compareByDescending { it.restaurant?.rating ?: 0f })
            }

            SortOptionIds.DISTANCE -> {
                results.sortedWith(compareBy {
                    val distanceStr =
                        it.restaurant?.distance?.replace("[^0-9.]".toRegex(), "") ?: "999999"
                    distanceStr.toFloatOrNull() ?: Float.MAX_VALUE
                })
            }

            SortOptionIds.DELIVERY_TIME -> {
                results.sortedWith(compareBy {
                    val timeStr =
                        it.restaurant?.deliveryTime?.replace("[^0-9]".toRegex(), "") ?: "999999"
                    timeStr.toIntOrNull() ?: Int.MAX_VALUE
                })
            }

            SortOptionIds.PRICE_LOW_TO_HIGH -> {
                results.sortedWith(compareBy {
                    if (it.menuItemList.isNotEmpty()) {
                        it.menuItemList.minByOrNull { menuItem -> menuItem.price }?.price
                            ?: Float.MAX_VALUE
                    } else {
                        Float.MAX_VALUE
                    }
                })
            }

            SortOptionIds.RELEVANCE -> results

            else -> results
        }
    }

    fun getAvailableFilters(): List<String> {
        return _uiState.value.searchResults
            .mapNotNull { it.restaurant }
            .flatMap { it.badges }
            .distinct()
            .sorted()
    }

    fun getMenuItemsCount(): Int {
        return _uiState.value.filteredAndSortedResults.sumOf { it.menuItemList.size }
    }

    fun getCurrentSortDisplayText(sortOptions: List<SortOption>): Int {
        return sortOptions.find { it.id == _uiState.value.selectedSortId }?.displayTextRes
            ?: com.app.tastybuds.R.string.sort_by
    }

    fun resetSearch() {
        _uiState.update {
            SearchUiState()
        }
        originalSearchResults = emptyList()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}