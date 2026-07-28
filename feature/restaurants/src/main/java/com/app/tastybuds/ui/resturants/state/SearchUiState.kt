package com.app.tastybuds.ui.resturants.state

import com.app.tastybuds.domain.model.SearchResult
import com.app.tastybuds.ui.resturants.search.SortOptionIds

data class SearchUiState(
    val isLoading: Boolean = false,
    val searchResults: List<SearchResult> = emptyList(),
    val filteredAndSortedResults: List<SearchResult> = emptyList(),
    val selectedSortId: String = SortOptionIds.RELEVANCE,
    val selectedFilters: Set<String> = emptySet(),
    val isEmpty: Boolean = false,
    val error: String? = null
)