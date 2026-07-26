package com.app.tastybuds.data.repo

import com.app.tastybuds.domain.repository.FoodDetailsRepository

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.mapper.toFoodCustomization
import com.app.tastybuds.data.model.mapper.toFoodDetails
import com.app.tastybuds.domain.model.FoodCustomization
import com.app.tastybuds.domain.model.FoodDetails
import com.app.tastybuds.domain.model.FoodDetailsData
import com.app.tastybuds.util.Result
import javax.inject.Inject
import javax.inject.Singleton

// `FoodDetailsRepository` interface now lives in :core:common (same package). Every method
// is one-shot (getFoodDetailsData just sequences two suspend calls), not Flow<Result<T>> —
// suspend Retrofit calls already don't block the calling thread, so no flowOn(IO) is needed.

@Singleton
class FoodDetailsRepositoryImpl @Inject constructor(
    private val apiService: TastyBudsApiService
) : FoodDetailsRepository {

    override suspend fun getFoodDetails(foodItemId: String, userId: String): Result<FoodDetails> {
        return try {
            val foodItem = apiService.getFoodDetails(foodItemId)
            val favoritesResponse = apiService.getUserFavorites(userId)

            val favorites = favoritesResponse.body() ?: emptyList()
            val isFavorite = favorites.any { it.menuItemId == foodItemId }

            val foodDetailsWithFavorite =
                foodItem.toFoodDetails().copy(isFavorite = isFavorite)
            Result.Success(foodDetailsWithFavorite)
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun getCustomizationOptions(menuItemId: String): Result<FoodCustomization> {
        return try {
            val response = apiService.getCustomizationOptions(menuItemId)
            Result.Success(response.toFoodCustomization())
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun getFoodDetailsData(
        foodItemId: String, userId: String
    ): Result<FoodDetailsData> {
        return try {
            val foodDetailsResult = getFoodDetails(foodItemId, userId)
            if (foodDetailsResult is Result.Error) {
                return Result.Error(foodDetailsResult.message)
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

            Result.Success(foodDetailsData)
        } catch (e: Exception) {
            Result.Error("Failed to load food details: ${e.message}")
        }
    }
}