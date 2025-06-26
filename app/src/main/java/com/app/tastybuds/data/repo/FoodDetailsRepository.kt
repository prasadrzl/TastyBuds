package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.mapper.toFoodCustomization
import com.app.tastybuds.data.model.mapper.toFoodDetails
import com.app.tastybuds.domain.model.FoodCustomization
import com.app.tastybuds.domain.model.FoodDetails
import com.app.tastybuds.domain.model.FoodDetailsData
import com.app.tastybuds.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton


interface FoodDetailsRepository {
    suspend fun getFoodDetails(foodItemId: String, userId: String): Result<FoodDetails>
    suspend fun getCustomizationOptions(menuItemId: String): Result<FoodCustomization>
    fun getFoodDetailsData(foodItemId: String, userId: String): Flow<Result<FoodDetailsData>>
}

@Singleton
class FoodDetailsRepositoryImpl @Inject constructor(
    private val apiService: TastyBudsApiService
) : FoodDetailsRepository {

    override suspend fun getFoodDetails(foodItemId: String, userId: String): Result<FoodDetails> {
        return try {
            val foodResponse = apiService.getFoodDetails("eq.$foodItemId")
            val favoritesResponse = apiService.getUserFavorites("eq.$userId")

            if (foodResponse.isNotEmpty()) {
                val foodItem = foodResponse.firstOrNull()
                if (foodItem != null) {
                    val favorites = favoritesResponse.body() ?: emptyList()
                    val isFavorite = favorites.any { it.menuItemId == foodItemId }

                    val foodDetailsWithFavorite =
                        foodItem.toFoodDetails().copy(isFavorite = isFavorite)
                    Result.Success(foodDetailsWithFavorite)
                } else {
                    Result.Error("Food item not found")
                }
            } else {
                Result.Error("Failed to get food details")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun getCustomizationOptions(menuItemId: String): Result<FoodCustomization> {
        return try {
            val response = apiService.getCustomizationOptions("eq.$menuItemId")
            val customization = response.firstOrNull()?.toFoodCustomization() ?: FoodCustomization(
                sizes = emptyList(), toppings = emptyList(), spiceLevels = emptyList()
            )
            Result.Success(customization)
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override fun getFoodDetailsData(
        foodItemId: String, userId: String
    ): Flow<Result<FoodDetailsData>> = flow {
        emit(Result.Loading)

        try {
            val foodDetailsResult = getFoodDetails(foodItemId, userId)
            if (foodDetailsResult is Result.Error) {
                emit(Result.Error(foodDetailsResult.message))
                return@flow
            }

            val foodDetails = (foodDetailsResult as Result.Success).data

            val customizationResult = getCustomizationOptions(foodItemId)
            val customization = if (customizationResult is Result.Success) {
                customizationResult.data
            } else {
                FoodCustomization(emptyList(), emptyList(), emptyList())
            }

            val foodDetailsData = FoodDetailsData(
                foodDetails = foodDetails, customization = customization
            )

            emit(Result.Success(foodDetailsData))

        } catch (e: Exception) {
            emit(Result.Error("Failed to load food details: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)
}