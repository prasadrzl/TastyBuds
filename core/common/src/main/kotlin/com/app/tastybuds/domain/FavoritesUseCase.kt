package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.FavoriteMenuItem
import com.app.tastybuds.domain.model.FavoriteRestaurant
import com.app.tastybuds.domain.repository.FavoritesRepository
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

    suspend fun getFavoriteRestaurants(userId: String): Result<List<FavoriteRestaurant>> {
        return favoritesRepository.getFavoriteRestaurantsWithDetails(userId)
    }

    suspend fun getFavoriteMenuItems(userId: String): Result<List<FavoriteMenuItem>> {
        return favoritesRepository.getFavoriteMenuItemsWithDetails(userId)
    }
}

