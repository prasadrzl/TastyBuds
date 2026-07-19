package com.app.tastybuds.domain.repository

import com.app.tastybuds.domain.model.CategoryDetailsData
import com.app.tastybuds.domain.model.CategoryMenuItem
import com.app.tastybuds.domain.model.CategoryRestaurant
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.util.Result
import kotlinx.coroutines.flow.Flow

interface RestaurantRepository {
    fun getRestaurantsByCategory(categoryId: String): Flow<List<Restaurant>>
    fun searchRestaurants(query: String): Flow<List<Restaurant>>
    fun getAllRestaurants(): Flow<List<Restaurant>>

    fun getCategoryDetails(categoryId: String): Flow<CategoryDetailsData>
    fun getTopRestaurantsByCategory(categoryId: String): Flow<List<CategoryRestaurant>>
    fun getMenuItemsByCategory(categoryId: String): Flow<List<CategoryMenuItem>>
    fun getRecommendedRestaurantsByCategory(categoryId: String): Flow<List<CategoryRestaurant>>
    suspend fun getRestaurantsByIds(restaurantIds: String): Result<List<Restaurant>>
}
