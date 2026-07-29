package com.app.tastybuds.data.repo

import com.app.tastybuds.domain.repository.SearchRepository

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.mapper.SearchResultsMapper
import com.app.tastybuds.domain.model.SearchResult
import javax.inject.Inject
class SearchRepositoryImpl @Inject constructor(
    private val apiService: TastyBudsApiService,
    private val searchResultsMapper: SearchResultsMapper
) : SearchRepository {

    override suspend fun searchMenuItemsWithRestaurants(query: String): List<SearchResult> {
        return try {

            val limit = if (query.isBlank()) 50 else 30

            val result =
                apiService.searchMenuItemsWithRestaurants(nameQuery = query, limit = limit)
            searchResultsMapper.mapToSearchResults(result)
        } catch (e: Exception) {
            emptyList()
        }
    }
}