package com.app.tastybuds.domain.repository

import com.app.tastybuds.domain.model.CategoryDetailsData
import com.app.tastybuds.domain.model.CategoryMenuItem
import com.app.tastybuds.domain.model.CategoryRestaurant
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.util.Result

// Every method here is a single network call, not an ongoing observation — plain suspend
// functions returning Result<T>, not Flow<T>.

interface RestaurantRepository {
    suspend fun getRestaurantsByCategory(categoryId: String): Result<List<Restaurant>>
    suspend fun searchRestaurants(query: String): Result<List<Restaurant>>
    suspend fun getAllRestaurants(): Result<List<Restaurant>>

    suspend fun getCategoryDetails(categoryId: String): Result<CategoryDetailsData>
    suspend fun getTopRestaurantsByCategory(categoryId: String): Result<List<CategoryRestaurant>>
    suspend fun getMenuItemsByCategory(categoryId: String): Result<List<CategoryMenuItem>>
    suspend fun getRecommendedRestaurantsByCategory(categoryId: String): Result<List<CategoryRestaurant>>
    suspend fun getRestaurantsByIds(restaurantIds: String): Result<List<Restaurant>>
}
