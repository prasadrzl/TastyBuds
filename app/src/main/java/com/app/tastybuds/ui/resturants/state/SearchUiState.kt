package com.app.tastybuds.ui.resturants.state

import com.app.tastybuds.domain.model.SearchResult
import com.app.tastybuds.ui.resturants.search.SortOptionIds

data class SearchUiState(
    val isLoading: Boolean = false,
    val searchResults: List<SearchResult> = emptyList(),
    val filteredAndSortedResults: List<SearchResult> = emptyList(), // Add this
    val selectedSortId: String = SortOptionIds.RELEVANCE, // Add this
    val selectedFilters: Set<String> = emptySet(), // Add this
    val isEmpty: Boolean = false,
    val error: String? = null
)