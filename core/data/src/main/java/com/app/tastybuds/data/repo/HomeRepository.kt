package com.app.tastybuds.data.repo

import com.app.tastybuds.domain.repository.HomeRepository

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.mapper.toDeal
import com.app.tastybuds.data.model.mapper.toDomainModel
import com.app.tastybuds.domain.model.Banner
import com.app.tastybuds.domain.model.Category
import com.app.tastybuds.domain.model.Deal
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.util.ErrorHandler
import com.app.tastybuds.util.Result
import javax.inject.Inject
import com.app.tastybuds.domain.model.Collection as FoodCollection

// `HomeRepository` interface now lives in :core:common (same package). Every method is a
// single network call, not an ongoing observation — plain suspend functions returning
// Result<T>, not Flow<T>.

class HomeRepositoryImpl @Inject constructor(
    private val tastyBudsApiService: TastyBudsApiService
) : HomeRepository {

    override suspend fun getBanners(): Result<List<Banner>> {
        return try {
            Result.Success(tastyBudsApiService.getBanners().map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            Result.Success(tastyBudsApiService.getCategories().map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun getVoucherCount(userId: String): Result<Int> {
        return try {
            Result.Success(tastyBudsApiService.getVoucherCount(userId).count { !it.isUsed })
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun getCollections(): Result<List<FoodCollection>> {
        return try {
            Result.Success(tastyBudsApiService.getCollections().map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun getRecommendedRestaurants(): Result<List<Restaurant>> {
        return try {
            Result.Success(tastyBudsApiService.getRecommendedRestaurants().map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun getDeals(): Result<List<Deal>> {
        return try {
            Result.Success(tastyBudsApiService.getDeals().map { it.toDeal() })
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }
}
