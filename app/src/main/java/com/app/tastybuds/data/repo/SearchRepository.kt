package com.app.tastybuds.data.repo

import com.app.tastybuds.domain.repository.SearchRepository

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.mapper.SearchResultsMapper
import com.app.tastybuds.domain.model.SearchResult
import javax.inject.Inject

// `SearchRepository` interface now lives in :core:common (same package). DTO->domain
// mapping happens here (data layer) instead of in the UseCase, per Clean Architecture.

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