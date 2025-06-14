package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.RestaurantCombo
import com.app.tastybuds.data.model.RestaurantDetails
import com.app.tastybuds.data.model.RestaurantDetailsData
import com.app.tastybuds.data.model.RestaurantMenuItem
import com.app.tastybuds.data.model.RestaurantReview
import com.app.tastybuds.data.model.RestaurantVoucher
import com.app.tastybuds.data.model.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import com.app.tastybuds.util.Result

interface RestaurantDetailsRepository {
    suspend fun getRestaurantDetails(restaurantId: String): Result<RestaurantDetails>
    suspend fun getRestaurantMenuItems(restaurantId: String): Result<List<RestaurantMenuItem>>
    suspend fun getForYouMenuItems(restaurantId: String): Result<List<RestaurantMenuItem>>
    suspend fun getRestaurantReviews(restaurantId: String): Result<List<RestaurantReview>>
    suspend fun getRestaurantVouchers(
        restaurantId: String,
        userId: String
    ): Result<List<RestaurantVoucher>>

    suspend fun getRestaurantCombos(restaurantId: String): Result<List<RestaurantCombo>>
    fun getRestaurantDetailsData(
        restaurantId: String,
        userId: String
    ): Flow<Result<RestaurantDetailsData>>
}

@Singleton
class RestaurantDetailsRepositoryImpl @Inject constructor(
    private val apiService: TastyBudsApiService
) : RestaurantDetailsRepository {

    override suspend fun getRestaurantDetails(restaurantId: String): Result<RestaurantDetails> {
        return try {
            val response = apiService.getRestaurantDetails("eq.$restaurantId")
            if (response.isSuccessful) {
                val restaurant = response.body()?.firstOrNull()?.toDomain()
                if (restaurant != null) {
                    Result.Success(restaurant)
                } else {
                    Result.Error("Restaurant not found")
                }
            } else {
                Result.Error("Restaurant not found")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun getRestaurantMenuItems(restaurantId: String): Result<List<RestaurantMenuItem>> {
        return try {
            val response = apiService.getRestaurantMenuItems("eq.$restaurantId")
            if (response.isSuccessful) {
                val menuItems = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(menuItems)
            } else {
                Result.Error("Failed to fetch menu items: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun getForYouMenuItems(restaurantId: String): Result<List<RestaurantMenuItem>> {
        return try {
            val response = apiService.getForYouMenuItems("eq.$restaurantId")
            if (response.isSuccessful) {
                val menuItems = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(menuItems)
            } else {
                Result.Error("Failed to fetch for you items: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun getRestaurantReviews(restaurantId: String): Result<List<RestaurantReview>> {
        return try {
            val response = apiService.getRestaurantReviews("eq.$restaurantId")
            if (response.isSuccessful) {
                val reviews = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(reviews)
            } else {
                Result.Error("Failed to fetch reviews: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun getRestaurantVouchers(
        restaurantId: String,
        userId: String
    ): Result<List<RestaurantVoucher>> {
        return try {
            val response = apiService.getUserRestaurantVouchers("eq.$userId", "eq.$restaurantId")
            if (response.isSuccessful) {
                val vouchers = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(vouchers)
            } else {
                Result.Error("Failed to fetch vouchers: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun getRestaurantCombos(restaurantId: String): Result<List<RestaurantCombo>> {
        return try {
            val response = apiService.getRestaurantCombos("eq.$restaurantId")
            if (response.isSuccessful) {
                val combos = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(combos)
            } else {
                Result.Error("Failed to fetch combos: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override fun getRestaurantDetailsData(
        restaurantId: String,
        userId: String
    ): Flow<Result<RestaurantDetailsData>> = flow {
        emit(Result.Loading)

        try {
            val restaurantResult = getRestaurantDetails(restaurantId)
            if (restaurantResult is Result.Error) {
                emit(Result.Error(restaurantResult.message))
                return@flow
            }

            val restaurant = (restaurantResult as Result.Success).data

            val forYouItemsResult = getForYouMenuItems(restaurantId)
            val menuItemsResult = getRestaurantMenuItems(restaurantId)
            val reviewsResult = getRestaurantReviews(restaurantId)
            val vouchersResult = getRestaurantVouchers(restaurantId, userId)
            val combosResult = getRestaurantCombos(restaurantId)

            val restaurantDetailsData = RestaurantDetailsData(
                restaurant = restaurant,
                forYouItems = if (forYouItemsResult is Result.Success) forYouItemsResult.data else emptyList(),
                menuItems = if (menuItemsResult is Result.Success) menuItemsResult.data else emptyList(),
                reviews = if (reviewsResult is Result.Success) reviewsResult.data else emptyList(),
                vouchers = if (vouchersResult is Result.Success) vouchersResult.data else emptyList(),
                combos = if (combosResult is Result.Success) combosResult.data else emptyList()
            )

            emit(Result.Success(restaurantDetailsData))

        } catch (e: Exception) {
            emit(Result.Error("Failed to load restaurant data: ${e.message}"))
        }
    }
}