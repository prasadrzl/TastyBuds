package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.SearchResult
import com.app.tastybuds.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchResultsUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {

    fun searchMenuItemsGroupedByRestaurant(query: String): Flow<List<SearchResult>> = flow {
        try {
            val searchResults = searchRepository.searchMenuItemsWithRestaurants(query.trim())

            val processedResults = searchResults.filter { searchResult ->
                searchResult.menuItemList.isNotEmpty()
            }

            emit(processedResults)

        } catch (exception: Exception) {
            exception.printStackTrace()
            emit(emptyList())
        }
    }.debounce(300)
        .distinctUntilChanged()
}