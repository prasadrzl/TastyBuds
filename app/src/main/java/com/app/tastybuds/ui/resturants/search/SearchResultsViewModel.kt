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
                            searchResults = applyCurrentFilters(results),
                            isEmpty = results.isEmpty(),
                            error = null
                        )
                    }
                }
        }
    }

    private fun applyCurrentFilters(results: List<SearchResult>): List<SearchResult> {
        return applyFilters(results, _uiState.value.selectedFilters)
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
}