package com.app.tastybuds.data.repo

import com.app.tastybuds.domain.repository.RestaurantRepository

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.mapper.toDomainModel
import com.app.tastybuds.domain.model.CategoryDetailsData
import com.app.tastybuds.domain.model.CategoryMenuItem
import com.app.tastybuds.domain.model.CategoryRestaurant
import com.app.tastybuds.domain.model.Restaurant
import javax.inject.Inject
import com.app.tastybuds.util.*

// `RestaurantRepository` interface now lives in :core:common (same package). Every method
// is a single network call, not an ongoing observation — plain suspend functions returning
// Result<T>, not Flow<T>. The old Flow<T> + .catch { emit(emptyList()) } shape silently
// turned network failures into "no results," indistinguishable from an empty response.

class RestaurantRepositoryImpl @Inject constructor(
    private val tastyBudsApiService: TastyBudsApiService
) : RestaurantRepository {

    override suspend fun getRestaurantsByCategory(categoryId: String): Result<List<Restaurant>> {
        return try {
            val response = tastyBudsApiService.getRestaurantsByCategoryId(categoryId, limit = 5)
            Result.Success(response.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun searchRestaurants(query: String): Result<List<Restaurant>> {
        return try {
            val response = tastyBudsApiService.searchRestaurantsByName("ilike.*$query*")
            Result.Success(response.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun getAllRestaurants(): Result<List<Restaurant>> {
        return try {
            val response = tastyBudsApiService.getRecommendedRestaurants()
            Result.Success(response.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun getCategoryDetails(categoryId: String): Result<CategoryDetailsData> {
        return try {
            val topRestaurantsResult = getTopRestaurantsByCategory(categoryId)
            val menuItemsResult = getMenuItemsByCategory(categoryId)
            val recommendedResult = getRecommendedRestaurantsByCategory(categoryId)

            Result.Success(
                CategoryDetailsData(
                    topRestaurants = (topRestaurantsResult as? Result.Success)?.data ?: emptyList(),
                    menuItems = (menuItemsResult as? Result.Success)?.data ?: emptyList(),
                    recommendedRestaurants = (recommendedResult as? Result.Success)?.data
                        ?: emptyList()
                )
            )
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun getTopRestaurantsByCategory(categoryId: String): Result<List<CategoryRestaurant>> {
        return try {
            val response = tastyBudsApiService.getTopCategoryRestaurants(categoryId)
            Result.Success(response.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun getMenuItemsByCategory(categoryId: String): Result<List<CategoryMenuItem>> {
        return try {
            val response = tastyBudsApiService.getPopularMenuItems(categoryId)
            Result.Success(response.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun getRecommendedRestaurantsByCategory(categoryId: String): Result<List<CategoryRestaurant>> {
        return try {
            val response = tastyBudsApiService.getRecommendedByCategoryExt(categoryId)
            Result.Success(response.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun getRestaurantsByIds(restaurantIds: String): Result<List<Restaurant>> {
        return try {
            if (restaurantIds.isEmpty()) {
                return Result.Success(emptyList())
            }

            val response =
                tastyBudsApiService.getRestaurantsByIdsForCollection("in.($restaurantIds)")

            if (response.isSuccessful) {
                val restaurantResponses = response.body() ?: emptyList()
                val restaurants = restaurantResponses.map { it.toDomainModel() }
                Result.Success(restaurants)
            } else {
                Result.Error("Failed to load restaurants: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }
}