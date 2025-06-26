package com.app.tastybuds.domain

import com.app.tastybuds.data.repo.RestaurantRepository
import com.app.tastybuds.domain.model.CategoryDetailsData
import com.app.tastybuds.domain.model.CategoryMenuItem
import com.app.tastybuds.domain.model.CategoryRestaurant
import com.app.tastybuds.domain.model.Restaurant
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.app.tastybuds.util.Result

class RestaurantUseCase @Inject constructor(
    private val repository: RestaurantRepository
) {

    fun getRestaurantsByCategory(categoryId: String): Flow<List<Restaurant>> {
        return repository.getRestaurantsByCategory(categoryId)
    }

    fun searchRestaurants(query: String): Flow<List<Restaurant>> {
        return if (query.isBlank()) {
            repository.getAllRestaurants()
        } else {
            repository.searchRestaurants(query)
        }
    }

    fun getAllRestaurants(): Flow<List<Restaurant>> {
        return repository.getAllRestaurants()
    }

    fun getCategoryDetails(categoryId: String): Flow<CategoryDetailsData> {
        return repository.getCategoryDetails(categoryId)
    }

    fun getTopRestaurantsByCategory(categoryId: String): Flow<List<CategoryRestaurant>> {
        return repository.getTopRestaurantsByCategory(categoryId)
    }

    fun getMenuItemsByCategory(categoryId: String): Flow<List<CategoryMenuItem>> {
        return repository.getMenuItemsByCategory(categoryId)
    }

    fun getRecommendedRestaurantsByCategory(categoryId: String): Flow<List<CategoryRestaurant>> {
        return repository.getRecommendedRestaurantsByCategory(categoryId)
    }

    suspend fun getRestaurantsByIds(restaurantIds: String): Result<List<Restaurant>> {
        return repository.getRestaurantsByIds(restaurantIds)
    }
}