package com.app.tastybuds.data.repo

import com.app.tastybuds.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import androidx.compose.ui.graphics.Color
import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.domain.model.Banner
import com.app.tastybuds.domain.model.Category
import com.app.tastybuds.domain.model.Deal
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.domain.model.Collection as FoodCollection

interface HomeRepository {
    fun getBanners(): Flow<List<Banner>>
    fun getCategories(): Flow<List<Category>>
    fun getVoucherCount(userId: String): Flow<Int>
    fun getCollections(): Flow<List<FoodCollection>>
    fun getRecommendedRestaurants(): Flow<List<Restaurant>>
    fun getDeals(): Flow<List<Deal>>
    fun searchRestaurants(query: String): Flow<List<Restaurant>>
}

class HomeRepositoryImpl @Inject constructor(
    private val tastyBudsApiService: TastyBudsApiService
) : HomeRepository {

    override fun getBanners(): Flow<List<Banner>> = flow {
        val result = tastyBudsApiService.getBanners()
        val banners = result.map { it.toDomainModel() }
        emit(banners)
    }.catch {
        emit(emptyList())
    }

    override fun getCategories(): Flow<List<Category>> = flow {
        val response = tastyBudsApiService.getCategories()
        val categories = response.map { it.toDomainModel() }
        emit(categories)
    }.catch {
        emit(emptyList())
    }

    override fun getVoucherCount(userId: String): Flow<Int> = flow {
        val response = tastyBudsApiService.getVoucherCount(userId)
        val count = response.count { !it.isUsed }
        emit(count)
    }.catch {
        emit(0)
    }

    override fun getCollections(): Flow<List<FoodCollection>> = flow {
        val response = tastyBudsApiService.getCollections()
        val collections = response.map { it.toDomainModel() }
        emit(collections)
    }.catch {
        emit(emptyList())
    }

    override fun getRecommendedRestaurants(): Flow<List<Restaurant>> = flow {
        val response = tastyBudsApiService.getRecommendedRestaurants()
        val restaurants = response.map { it.toDomainModel() }
        emit(restaurants)
    }.catch {
        emit(emptyList())
    }

    override fun getDeals(): Flow<List<Deal>> = flow {
        val response = tastyBudsApiService.getDeals()
        val deals = response.map { it.toDomainModel() }
        emit(deals)
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
}
