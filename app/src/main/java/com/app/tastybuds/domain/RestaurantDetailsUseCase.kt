package com.app.tastybuds.domain

import com.app.tastybuds.data.model.RestaurantDetailsData
import com.app.tastybuds.data.model.RestaurantMenuItem
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
}