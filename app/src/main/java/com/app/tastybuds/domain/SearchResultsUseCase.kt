package com.app.tastybuds.domain

import com.app.tastybuds.data.SearchResult
import com.app.tastybuds.data.SearchResultsMapper
import com.app.tastybuds.data.repo.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchResultsUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
    private val searchResultsMapper: SearchResultsMapper
) {

    fun searchMenuItemsGroupedByRestaurant(query: String): Flow<List<SearchResult>> = flow {
        try {
            val menuItemsResponse = searchRepository.searchMenuItemsWithRestaurants(query.trim())

            val searchResults = searchResultsMapper.mapToSearchResults(menuItemsResponse)

            val processedResults = searchResults.filter { searchResult ->
                searchResult.menuItemList.isNotEmpty()
            }

            emit(processedResults)

        } catch (exception: Exception) {
            android.util.Log.e("SearchResultsUseCase", "Search failed", exception)
            emit(emptyList())
        }
    }.debounce(300)
        .distinctUntilChanged()
}