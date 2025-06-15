package com.app.tastybuds.domain

import com.app.tastybuds.data.repo.FoodDetailsRepository
import com.app.tastybuds.domain.model.FoodDetailsData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.app.tastybuds.util.Result

class FoodDetailsUseCase @Inject constructor(
    private val repository: FoodDetailsRepository
) {
    
    operator fun invoke(foodItemId: String): Flow<Result<FoodDetailsData>> {
        return repository.getFoodDetailsData(foodItemId)
    }
    
    suspend fun getFoodDetails(foodItemId: String) = 
        repository.getFoodDetails(foodItemId)
    
    suspend fun getCustomizationOptions(menuItemId: String) = 
        repository.getCustomizationOptions(menuItemId)
}