package com.app.tastybuds.domain.repository

import com.app.tastybuds.domain.model.RestaurantCombo
import com.app.tastybuds.domain.model.RestaurantDetails
import com.app.tastybuds.domain.model.RestaurantDetailsData
import com.app.tastybuds.domain.model.RestaurantMenuItem
import com.app.tastybuds.domain.model.RestaurantReview
import com.app.tastybuds.domain.model.RestaurantVoucher
import com.app.tastybuds.util.Result

// Every method here is one-shot (getRestaurantDetailsData just sequences several suspend
// calls), not an ongoing observation — plain suspend functions, not Flow<Result<T>>.

interface RestaurantDetailsRepository {
    suspend fun getRestaurantDetails(
        restaurantId: String, userId: String
    ): Result<RestaurantDetails>

    suspend fun getRestaurantMenuItems(restaurantId: String): Result<List<RestaurantMenuItem>>
    suspend fun getForYouMenuItems(restaurantId: String): Result<List<RestaurantMenuItem>>
    suspend fun getRestaurantReviews(restaurantId: String): Result<List<RestaurantReview>>
    suspend fun getRestaurantVouchers(
        restaurantId: String, userId: String
    ): Result<List<RestaurantVoucher>>

    suspend fun getRestaurantCombos(restaurantId: String): Result<List<RestaurantCombo>>
    suspend fun getRestaurantDetailsData(
        restaurantId: String, userId: String
    ): Result<RestaurantDetailsData>
}
