package com.app.tastybuds.domain

import com.app.tastybuds.data.model.RestaurantCombo
import com.app.tastybuds.data.model.RestaurantDetails
import com.app.tastybuds.data.model.RestaurantDetailsData
import com.app.tastybuds.data.model.RestaurantMenuItem
import com.app.tastybuds.data.model.RestaurantReview
import com.app.tastybuds.data.model.RestaurantVoucher
import com.app.tastybuds.data.repo.RestaurantDetailsRepository
import com.app.tastybuds.domain.model.*
import com.app.tastybuds.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RestaurantDetailsUseCase @Inject constructor(
    private val repository: RestaurantDetailsRepository
) {

    operator fun invoke(restaurantId: String, userId: String): Flow<Result<RestaurantDetailsData>> {
        return repository.getRestaurantDetailsData(restaurantId, userId)
    }

    suspend fun getRestaurantDetails(restaurantId: String): Result<RestaurantDetails> {
        return repository.getRestaurantDetails(restaurantId)
    }

    suspend fun getRestaurantMenuItems(restaurantId: String): Result<List<RestaurantMenuItem>> {
        return repository.getRestaurantMenuItems(restaurantId)
    }

    suspend fun getForYouMenuItems(restaurantId: String): Result<List<RestaurantMenuItem>> {
        return repository.getForYouMenuItems(restaurantId)
    }

    suspend fun getRestaurantReviews(restaurantId: String): Result<List<RestaurantReview>> {
        return repository.getRestaurantReviews(restaurantId)
    }

    suspend fun getRestaurantVouchers(
        restaurantId: String,
        userId: String
    ): Result<List<RestaurantVoucher>> {
        return repository.getRestaurantVouchers(restaurantId, userId)
    }

    suspend fun getRestaurantCombos(restaurantId: String): Result<List<RestaurantCombo>> {
        return repository.getRestaurantCombos(restaurantId)
    }

    suspend fun getRestaurantVoucherCount(restaurantId: String, userId: String): Result<Int> {
        return when (val result = repository.getRestaurantVouchers(restaurantId, userId)) {
            is Result.Success -> Result.Success(result.data.size)
            is Result.Error -> Result.Error(result.message)
            is Result.Loading -> Result.Loading
        }
    }

    suspend fun getActiveVoucherCount(restaurantId: String, userId: String): Result<Int> {
        return when (val result = repository.getRestaurantVouchers(restaurantId, userId)) {
            is Result.Success -> {
                val activeCount = result.data.count { voucher ->
                    true
                }
                Result.Success(activeCount)
            }

            is Result.Error -> Result.Error(result.message)
            is Result.Loading -> Result.Loading
        }
    }

    suspend fun getReviewCount(restaurantId: String): Result<Int> {
        return when (val result = repository.getRestaurantReviews(restaurantId)) {
            is Result.Success -> Result.Success(result.data.size)
            is Result.Error -> Result.Error(result.message)
            is Result.Loading -> Result.Loading
        }
    }

    suspend fun getAverageRating(restaurantId: String): Result<Float> {
        return when (val result = repository.getRestaurantReviews(restaurantId)) {
            is Result.Success -> {
                val averageRating = if (result.data.isNotEmpty()) {
                    result.data.map { it.rating }.average().toFloat()
                } else {
                    0.0f
                }
                Result.Success(averageRating)
            }

            is Result.Error -> Result.Error(result.message)
            is Result.Loading -> Result.Loading
        }
    }

    suspend fun isRestaurantOpen(restaurantId: String): Result<Boolean> {
        return when (val result = repository.getRestaurantDetails(restaurantId)) {
            is Result.Success -> Result.Success(result.data.isOpen)
            is Result.Error -> Result.Error(result.message)
            is Result.Loading -> Result.Loading
        }
    }

    suspend fun getDeliveryInfo(restaurantId: String): Result<DeliveryInfo> {
        return when (val result = repository.getRestaurantDetails(restaurantId)) {
            is Result.Success -> {
                val deliveryInfo = DeliveryInfo(
                    deliveryTime = result.data.deliveryTime,
                    deliveryFee = result.data.deliveryFee,
                    minimumOrder = result.data.minimumOrder,
                    distance = result.data.distance
                )
                Result.Success(deliveryInfo)
            }

            is Result.Error -> Result.Error(result.message)
            is Result.Loading -> Result.Loading
        }
    }

    fun getRestaurantDetailsFlow(
        restaurantId: String,
        userId: String
    ): Flow<Result<RestaurantDetailsData>> {
        return repository.getRestaurantDetailsData(restaurantId, userId)
    }

    fun getVoucherCountFlow(restaurantId: String, userId: String): Flow<Result<Int>> {
        return repository.getRestaurantDetailsData(restaurantId, userId)
            .map { result ->
                when (result) {
                    is Result.Success -> Result.Success(result.data.vouchers.size)
                    is Result.Error -> Result.Error(result.message)
                    is Result.Loading -> Result.Loading
                }
            }
    }

    fun getReviewCountFlow(restaurantId: String, userId: String): Flow<Result<Int>> {
        return repository.getRestaurantDetailsData(restaurantId, userId)
            .map { result ->
                when (result) {
                    is Result.Success -> Result.Success(result.data.reviews.size)
                    is Result.Error -> Result.Error(result.message)
                    is Result.Loading -> Result.Loading
                }
            }
    }
}