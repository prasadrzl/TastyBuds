package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.toDomainModel
import com.app.tastybuds.domain.model.CategoryDetailsData
import com.app.tastybuds.domain.model.CategoryMenuItem
import com.app.tastybuds.domain.model.CategoryRestaurant
import com.app.tastybuds.domain.model.Restaurant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface RestaurantRepository {
    fun getRestaurantsByCategory(categoryId: String): Flow<List<Restaurant>>
    fun searchRestaurants(query: String): Flow<List<Restaurant>>
    fun getAllRestaurants(): Flow<List<Restaurant>>

    fun getCategoryDetails(categoryId: String): Flow<CategoryDetailsData>
    fun getTopRestaurantsByCategory(categoryId: String): Flow<List<CategoryRestaurant>>
    fun getMenuItemsByCategory(categoryId: String): Flow<List<CategoryMenuItem>>
    fun getRecommendedRestaurantsByCategory(categoryId: String): Flow<List<CategoryRestaurant>>
}

class RestaurantRepositoryImpl @Inject constructor(
    private val tastyBudsApiService: TastyBudsApiService
) : RestaurantRepository {

    private fun String.toCategoryFilter(): String = "cs.[\"$this\"]"

    override fun getRestaurantsByCategory(categoryId: String): Flow<List<Restaurant>> = flow {
        val response = tastyBudsApiService.getRestaurantsByCategory(categoryId.toCategoryFilter())
        val restaurants = response.map { it.toDomainModel() }
        emit(restaurants)
    }.catch {
        emit(emptyList())
    }

    override fun searchRestaurants(query: String): Flow<List<Restaurant>> = flow {
        val response = tastyBudsApiService.searchRestaurants("ilike.*$query*")
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
                tastyBudsApiService.getTopRestaurantsByCategory(categoryId.toCategoryFilter())
            val restaurants = response.map { it.toDomainModel() }
            emit(restaurants)
        }.catch {
            emit(emptyList())
        }

    override fun getMenuItemsByCategory(categoryId: String): Flow<List<CategoryMenuItem>> = flow {
        val response = tastyBudsApiService.getMenuItemsByCategory(categoryId)
        val menuItems = response.map { it.toDomainModel() }
        emit(menuItems)
    }.catch {
        emit(emptyList())
    }

    override fun getRecommendedRestaurantsByCategory(categoryId: String): Flow<List<CategoryRestaurant>> =
        flow {
            val response =
                tastyBudsApiService.getRecommendedRestaurantsByCategory(categoryId.toCategoryFilter())
            val restaurants = response.map { it.toDomainModel() }
            emit(restaurants)
        }.catch {
            emit(emptyList())
        }
}