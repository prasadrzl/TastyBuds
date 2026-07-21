package com.app.tastybuds.domain.repository

import com.app.tastybuds.domain.model.Banner
import com.app.tastybuds.domain.model.Category
import com.app.tastybuds.domain.model.Deal
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.util.Result
import com.app.tastybuds.domain.model.Collection as FoodCollection

// Every method here is a single network call, not an ongoing observation — plain suspend
// functions returning Result<T>, not Flow<T>. The old Flow<T> + .catch { emit(emptyList()) }
// shape silently turned network failures into "no results," indistinguishable from an
// empty response; Result<T> lets callers actually see the difference.

interface HomeRepository {
    suspend fun getBanners(): Result<List<Banner>>
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getVoucherCount(userId: String): Result<Int>
    suspend fun getCollections(): Result<List<FoodCollection>>
    suspend fun getRecommendedRestaurants(): Result<List<Restaurant>>
    suspend fun getDeals(): Result<List<Deal>>
}
