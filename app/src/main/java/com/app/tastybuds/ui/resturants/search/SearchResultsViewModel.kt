package com.app.tastybuds.ui.resturants.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.domain.SearchResultsUseCase
import com.app.tastybuds.ui.resturants.state.SearchUiState
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

    fun searchMenuItems(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            searchResultsUseCase.searchMenuItemsGroupedByRestaurant(query)
                .collect { results ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            searchResults = results,
                            isEmpty = results.isEmpty(),
                            error = null
                        )
                    }
                }
        }
    }

    fun clearResults() {
        _uiState.update {
            it.copy(
                searchResults = emptyList(),
                isEmpty = false,
                isLoading = false,
                error = null
            )
        }
    }
}