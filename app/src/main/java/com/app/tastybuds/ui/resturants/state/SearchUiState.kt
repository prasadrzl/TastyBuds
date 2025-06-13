package com.app.tastybuds.ui.resturants.state

import com.app.tastybuds.data.SearchResult

data class SearchUiState(
    val isLoading: Boolean = false,
    val searchResults: List<SearchResult> = emptyList(),
    val isEmpty: Boolean = false,
    val error: String? = null
)