package com.app.tastybuds.ui.resturants.state

import com.app.tastybuds.domain.model.SearchResult

data class SearchUiState(
    val isLoading: Boolean = false,
    val searchResults: List<SearchResult> = emptyList(),
    val selectedFilters: Set<String> = emptySet(),
    val isEmpty: Boolean = false,
    val error: String? = null
)