package com.app.tastybuds.domain

import com.app.tastybuds.data.repo.HomeRepository
import com.app.tastybuds.domain.model.Banner
import com.app.tastybuds.domain.model.Category
import com.app.tastybuds.domain.model.Deal
import com.app.tastybuds.domain.model.Restaurant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.app.tastybuds.domain.model.Collection as FoodCollection

class HomeUseCase @Inject constructor(private val repository: HomeRepository) {
    private fun getBanners(): Flow<List<Banner>> = repository.getBanners()
    
    private fun getCategories(): Flow<List<Category>> = repository.getCategories()
    
    private fun getVoucherCount(userId: String = "user_001"): Flow<Int> =
        repository.getVoucherCount(userId)
    
    private fun getCollections(): Flow<List<FoodCollection>> = repository.getCollections()
    
    fun getRecommendedRestaurants(): Flow<List<Restaurant>> =
        repository.getRecommendedRestaurants()
    
    private fun getDeals(): Flow<List<Deal>> = repository.getDeals()
    
    fun searchRestaurants(query: String): Flow<List<Restaurant>> =
        repository.searchRestaurants(query)
    
    fun getHomeData(userId: String = "user_001"): Flow<HomeData> = flow {
        try {
            val banners = getBanners().first()
            val categories = getCategories().first()
            val voucherCount = getVoucherCount(userId).first()
            val collections = getCollections().first()
            val restaurants = getRecommendedRestaurants().first()
            val deals = getDeals().first()
            
            emit(HomeData(
                banners = banners,
                categories = categories,
                voucherCount = voucherCount,
                collections = collections,
                recommendedRestaurants = restaurants,
                deals = deals
            ))
        } catch (e: Exception) {
            throw e
        }
    }
}

data class HomeData(
    val banners: List<Banner>,
    val categories: List<Category>,
    val voucherCount: Int,
    val collections: List<FoodCollection>,
    val recommendedRestaurants: List<Restaurant>,
    val deals: List<Deal>
)