package com.app.tastybuds.domain.repository

import com.app.tastybuds.domain.model.FoodCustomization
import com.app.tastybuds.domain.model.FoodDetails
import com.app.tastybuds.domain.model.FoodDetailsData
import com.app.tastybuds.util.Result
import kotlinx.coroutines.flow.Flow

interface FoodDetailsRepository {
    suspend fun getFoodDetails(foodItemId: String, userId: String): Result<FoodDetails>
    suspend fun getCustomizationOptions(menuItemId: String): Result<FoodCustomization>
    fun getFoodDetailsData(foodItemId: String, userId: String): Flow<Result<FoodDetailsData>>
}
