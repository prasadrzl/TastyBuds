package com.app.tastybuds.domain.repository

import com.app.tastybuds.domain.model.Banner
import com.app.tastybuds.domain.model.Category
import com.app.tastybuds.domain.model.Deal
import com.app.tastybuds.domain.model.Restaurant
import kotlinx.coroutines.flow.Flow
import com.app.tastybuds.domain.model.Collection as FoodCollection

interface HomeRepository {
    fun getBanners(): Flow<List<Banner>>
    fun getCategories(): Flow<List<Category>>
    fun getVoucherCount(userId: String): Flow<Int>
    fun getCollections(): Flow<List<FoodCollection>>
    fun getRecommendedRestaurants(): Flow<List<Restaurant>>
    fun getDeals(): Flow<List<Deal>>
}
