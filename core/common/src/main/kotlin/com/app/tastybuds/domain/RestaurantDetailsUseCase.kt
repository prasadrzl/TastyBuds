package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.RestaurantDetailsData
import com.app.tastybuds.domain.model.RestaurantMenuItem
import com.app.tastybuds.domain.repository.RestaurantDetailsRepository
import com.app.tastybuds.util.Result
import javax.inject.Inject

class RestaurantDetailsUseCase @Inject constructor(
    private val repository: RestaurantDetailsRepository
) {

    suspend operator fun invoke(restaurantId: String, userId: String): Result<RestaurantDetailsData> {
        return repository.getRestaurantDetailsData(restaurantId, userId)
    }

    suspend fun getRestaurantMenuItems(restaurantId: String): Result<List<RestaurantMenuItem>> {
        return repository.getRestaurantMenuItems(restaurantId)
    }
}