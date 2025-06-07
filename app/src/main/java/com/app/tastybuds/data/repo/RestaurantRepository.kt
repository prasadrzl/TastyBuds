package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.toDomainModel
import com.app.tastybuds.domain.model.Restaurant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface RestaurantRepository {
    fun getRestaurantsByCategory(categoryId: String): Flow<List<Restaurant>>
    fun searchRestaurants(query: String): Flow<List<Restaurant>>
    fun getAllRestaurants(): Flow<List<Restaurant>>
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
}