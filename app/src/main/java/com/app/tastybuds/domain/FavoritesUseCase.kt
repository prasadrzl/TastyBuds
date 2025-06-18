package com.app.tastybuds.domain

import com.app.tastybuds.data.model.FavoriteMenuItemUi
import com.app.tastybuds.data.model.FavoriteRestaurantUi
import com.app.tastybuds.data.model.mapper.toMenuItemUiModel
import com.app.tastybuds.data.model.mapper.toRestaurantUiModel
import com.app.tastybuds.data.repo.FavoritesRepository
import com.app.tastybuds.util.Result
import javax.inject.Inject

class FavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend fun toggleMenuItemFavorite(
        userId: String,
        menuItemId: String,
        restaurantId: String? = null
    ): Result<Boolean> {
        return when (val isFavoriteResult =
            favoritesRepository.isMenuItemFavorite(userId, menuItemId)) {
            is Result.Success -> {
                val wasAlreadyFavorite = isFavoriteResult.data
                if (wasAlreadyFavorite) {
                    when (val removeResult =
                        favoritesRepository.removeFavorite(userId, menuItemId, null)) {
                        is Result.Success -> Result.Success(false)
                        is Result.Error -> Result.Error(removeResult.message)
                        is Result.Loading -> Result.Loading
                    }
                } else {
                    when (val addResult =
                        favoritesRepository.addFavorite(userId, menuItemId, restaurantId)) {
                        is Result.Success -> Result.Success(true)
                        is Result.Error -> Result.Error(addResult.message)
                        is Result.Loading -> Result.Loading
                    }
                }
            }

            is Result.Error -> Result.Error(isFavoriteResult.message)
            is Result.Loading -> Result.Loading
        }
    }

    suspend fun toggleRestaurantFavorite(userId: String, restaurantId: String): Result<Boolean> {
        return when (val isFavoriteResult =
            favoritesRepository.isRestaurantFavorite(userId, restaurantId)) {
            is Result.Success -> {
                val wasAlreadyFavorite = isFavoriteResult.data
                if (wasAlreadyFavorite) {
                    when (val removeResult =
                        favoritesRepository.removeFavorite(userId, null, restaurantId)) {
                        is Result.Success -> Result.Success(false)
                        is Result.Error -> Result.Error(removeResult.message)
                        is Result.Loading -> Result.Loading
                    }
                } else {
                    when (val addResult =
                        favoritesRepository.addFavorite(userId, null, restaurantId)) {
                        is Result.Success -> Result.Success(true)
                        is Result.Error -> Result.Error(addResult.message)
                        is Result.Loading -> Result.Loading
                    }
                }
            }

            is Result.Error -> Result.Error(isFavoriteResult.message)
            is Result.Loading -> Result.Loading
        }
    }

    suspend fun getFavoriteRestaurantsForUI(userId: String): Result<List<FavoriteRestaurantUi>> {
        return when (val result = favoritesRepository.getFavoriteRestaurantsWithDetails(userId)) {
            is Result.Success -> {
                val uiModels = result.data.map { it.toRestaurantUiModel() }
                Result.Success(uiModels)
            }

            is Result.Error -> Result.Error(result.message)
            is Result.Loading -> Result.Loading
        }
    }

    suspend fun getFavoriteMenuItemsForUI(userId: String): Result<List<FavoriteMenuItemUi>> {
        return when (val result = favoritesRepository.getFavoriteMenuItemsWithDetails(userId)) {
            is Result.Success -> {
                val uiModels = result.data.map { it.toMenuItemUiModel() }
                Result.Success(uiModels)
            }

            is Result.Error -> Result.Error(result.message)
            is Result.Loading -> Result.Loading
        }
    }
}

