package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.MenuItemWithRestaurantResponse
import javax.inject.Inject

interface SearchRepository {
    suspend fun searchMenuItemsWithRestaurants(query: String): List<MenuItemWithRestaurantResponse>
}

class SearchRepositoryImpl @Inject constructor(
    private val apiService: TastyBudsApiService
) : SearchRepository {

    override suspend fun searchMenuItemsWithRestaurants(query: String): List<MenuItemWithRestaurantResponse> {
        return try {

            val formattedQuery = if (query.isNotBlank()) "ilike.*$query*" else null
            val limit = if (query.isBlank()) 50 else 30

            val result =
                apiService.searchMenuItemsWithRestaurants(nameQuery = formattedQuery, limit = limit)

            result

        } catch (e: Exception) {
            emptyList()
        }
    }
}