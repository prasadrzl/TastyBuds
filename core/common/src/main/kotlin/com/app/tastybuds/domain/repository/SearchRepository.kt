package com.app.tastybuds.domain.repository

import com.app.tastybuds.domain.model.SearchResult

interface SearchRepository {
    suspend fun searchMenuItemsWithRestaurants(query: String): List<SearchResult>
}
