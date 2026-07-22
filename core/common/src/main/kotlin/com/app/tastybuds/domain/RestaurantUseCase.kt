package com.app.tastybuds.domain

import com.app.tastybuds.domain.repository.RestaurantRepository
import com.app.tastybuds.domain.model.CategoryDetailsData
import com.app.tastybuds.domain.model.CategoryMenuItem
import com.app.tastybuds.domain.model.CategoryRestaurant
import com.app.tastybuds.domain.model.Restaurant
import javax.inject.Inject
import com.app.tastybuds.util.Result

class RestaurantUseCase @Inject constructor(
    private val repository: RestaurantRepository
) {

    suspend fun getRestaurantsByCategory(categoryId: String): Result<List<Restaurant>> {
        return repository.getRestaurantsByCategory(categoryId)
    }

    suspend fun searchRestaurants(query: String): Result<List<Restaurant>> {
        return if (query.isBlank()) {
            repository.getAllRestaurants()
        } else {
            repository.searchRestaurants(query)
        }
    }

    suspend fun getAllRestaurants(): Result<List<Restaurant>> {
        return repository.getAllRestaurants()
    }

    suspend fun getCategoryDetails(categoryId: String): Result<CategoryDetailsData> {
        return repository.getCategoryDetails(categoryId)
    }

    suspend fun getTopRestaurantsByCategory(categoryId: String): Result<List<CategoryRestaurant>> {
        return repository.getTopRestaurantsByCategory(categoryId)
    }

    suspend fun getMenuItemsByCategory(categoryId: String): Result<List<CategoryMenuItem>> {
        return repository.getMenuItemsByCategory(categoryId)
    }

    suspend fun getRecommendedRestaurantsByCategory(categoryId: String): Result<List<CategoryRestaurant>> {
        return repository.getRecommendedRestaurantsByCategory(categoryId)
    }

    suspend fun getRestaurantsByIds(restaurantIds: String): Result<List<Restaurant>> {
        return repository.getRestaurantsByIds(restaurantIds)
    }
}