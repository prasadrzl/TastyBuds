package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.mapper.toDomainModel
import com.app.tastybuds.domain.model.CategoryDetailsData
import com.app.tastybuds.domain.model.CategoryMenuItem
import com.app.tastybuds.domain.model.CategoryRestaurant
import com.app.tastybuds.domain.model.Restaurant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.app.tastybuds.util.*

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

class RestaurantRepositoryImpl @Inject constructor(
    private val tastyBudsApiService: TastyBudsApiService
) : RestaurantRepository {

    override fun getRestaurantsByCategory(categoryId: String): Flow<List<Restaurant>> = flow {
        val response = tastyBudsApiService.getRestaurantsByCategoryId(
            categoryId,
            limit = 5
        )
        val restaurants = response.map { it.toDomainModel() }
        emit(restaurants)
    }.catch {
        emit(emptyList())
    }

    override fun searchRestaurants(query: String): Flow<List<Restaurant>> = flow {
        val response = tastyBudsApiService.searchRestaurantsByName("ilike.*$query*")
        val restaurants = response.map { it.toDomainModel() }
        emit(restaurants)
    }.catch {
        emit(emptyList())
    }

    override fun getAllRestaurants(): Flow<List<Restaurant>> = flow {
        val response = tastyBudsApiService.getRecommendedRestaurants()
        val restaurants = response.map { it.toDomainModel() }
        emit(restaurants)
    }.catch {
        emit(emptyList())
    }

    override fun getCategoryDetails(categoryId: String): Flow<CategoryDetailsData> =
        combine(
            getTopRestaurantsByCategory(categoryId),
            getMenuItemsByCategory(categoryId),
            getRecommendedRestaurantsByCategory(categoryId)
        ) { topRestaurants, menuItems, recommendedRestaurants ->
            CategoryDetailsData(
                topRestaurants = topRestaurants,
                menuItems = menuItems,
                recommendedRestaurants = recommendedRestaurants
            )
        }.catch {
            emit(CategoryDetailsData(emptyList(), emptyList(), emptyList()))
        }

    override fun getTopRestaurantsByCategory(categoryId: String): Flow<List<CategoryRestaurant>> =
        flow {
            val response =
                tastyBudsApiService.getTopCategoryRestaurants(categoryId)
            val restaurants = response.map { it.toDomainModel() }
            emit(restaurants)
        }.catch {
            emit(emptyList())
        }

    override fun getMenuItemsByCategory(categoryId: String): Flow<List<CategoryMenuItem>> = flow {
        val response = tastyBudsApiService.getPopularMenuItems(categoryId)
        val menuItems = response.map { it.toDomainModel() }
        emit(menuItems)
    }.catch {
        emit(emptyList())
    }

    override fun getRecommendedRestaurantsByCategory(categoryId: String): Flow<List<CategoryRestaurant>> =
        flow {
            val response =
                tastyBudsApiService.getRecommendedByCategoryExt(categoryId)
            val restaurants = response.map { it.toDomainModel() }
            emit(restaurants)
        }.catch {
            emit(emptyList())
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