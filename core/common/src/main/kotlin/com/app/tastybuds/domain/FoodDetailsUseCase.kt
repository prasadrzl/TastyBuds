package com.app.tastybuds.domain

import com.app.tastybuds.domain.repository.FoodDetailsRepository
import com.app.tastybuds.domain.model.FoodDetailsData
import javax.inject.Inject
import com.app.tastybuds.util.Result

class FoodDetailsUseCase @Inject constructor(
    private val repository: FoodDetailsRepository
) {

    suspend operator fun invoke(foodItemId: String, userId: String): Result<FoodDetailsData> {
        return repository.getFoodDetailsData(foodItemId, userId)
    }

    suspend fun getFoodDetails(foodItemId: String, userId: String) =
        repository.getFoodDetails(foodItemId, userId)

    suspend fun getCustomizationOptions(menuItemId: String) =
        repository.getCustomizationOptions(menuItemId)
}