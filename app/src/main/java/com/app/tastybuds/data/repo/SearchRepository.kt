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
            // Log the query being executed
            android.util.Log.d("SearchRepository", "Searching for: $query")

            val formattedQuery = "ilike.*$query*"
            android.util.Log.d("SearchRepository", "Formatted query: $formattedQuery")

            // Log the full API call
            android.util.Log.d("SearchRepository", "Making API call to: menu_items?name=$formattedQuery&select=*,restaurants(*)")

            val result = apiService.searchMenuItemsWithRestaurants(nameQuery = formattedQuery)

            // Log successful response
            android.util.Log.d("SearchRepository", "API call successful. Result count: ${result.size}")
            result.forEach { item ->
                android.util.Log.d("SearchRepository", "Item: ${item.name}, Restaurant: ${item.restaurant?.name}")
            }

            result

        } catch (e: Exception) {
            // Log detailed error information
            android.util.Log.e("SearchRepository", "API call failed", e)
            android.util.Log.e("SearchRepository", "Error type: ${e.javaClass.simpleName}")
            android.util.Log.e("SearchRepository", "Error message: ${e.message}")

            // Log stack trace for debugging
            e.printStackTrace()

            // Check for specific error types
            when (e) {
                is java.net.UnknownHostException -> {
                    android.util.Log.e("SearchRepository", "Network error: Cannot reach server")
                }
                is java.net.SocketTimeoutException -> {
                    android.util.Log.e("SearchRepository", "Network error: Request timeout")
                }
                is retrofit2.HttpException -> {
                    android.util.Log.e("SearchRepository", "HTTP error: ${e.code()} - ${e.message()}")
                    android.util.Log.e("SearchRepository", "Response body: ${e.response()?.errorBody()?.string()}")
                }
                is com.squareup.moshi.JsonDataException -> {
                    android.util.Log.e("SearchRepository", "JSON parsing error: ${e.message}")
                }
                else -> {
                    android.util.Log.e("SearchRepository", "Unknown error: ${e.message}")
                }
            }

            emptyList()
        }
    }
}