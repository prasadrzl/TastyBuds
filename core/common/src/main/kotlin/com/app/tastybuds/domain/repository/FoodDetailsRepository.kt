package com.app.tastybuds.domain.repository

import com.app.tastybuds.domain.model.FoodCustomization
import com.app.tastybuds.domain.model.FoodDetails
import com.app.tastybuds.domain.model.FoodDetailsData
import com.app.tastybuds.util.Result

// Every method here is a single network call (or two sequential ones), not an ongoing
// observation — plain suspend functions returning Result<T>, not Flow<Result<T>>.

interface FoodDetailsRepository {
    suspend fun getFoodDetails(foodItemId: String, userId: String): Result<FoodDetails>
    suspend fun getCustomizationOptions(menuItemId: String): Result<FoodCustomization>
    suspend fun getFoodDetailsData(foodItemId: String, userId: String): Result<FoodDetailsData>
}
