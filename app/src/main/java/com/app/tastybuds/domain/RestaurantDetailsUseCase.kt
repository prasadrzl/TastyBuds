package com.app.tastybuds.domain

import com.app.tastybuds.data.model.RestaurantDetailsData
import com.app.tastybuds.data.model.RestaurantMenuItem
import com.app.tastybuds.data.model.RestaurantReview
import com.app.tastybuds.data.repo.RestaurantDetailsRepository
import com.app.tastybuds.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RestaurantDetailsUseCase @Inject constructor(
    private val repository: RestaurantDetailsRepository
) {

    operator fun invoke(restaurantId: String, userId: String): Flow<Result<RestaurantDetailsData>> {
        return repository.getRestaurantDetailsData(restaurantId, userId)
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

    suspend fun getReviewCount(restaurantId: String): Result<Int> {
        return when (val result = repository.getRestaurantReviews(restaurantId)) {
            is Result.Success -> Result.Success(result.data.size)
            is Result.Error -> Result.Error(result.message)
            is Result.Loading -> Result.Loading
        }
    }
}