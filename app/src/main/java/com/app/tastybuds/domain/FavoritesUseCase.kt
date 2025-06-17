package com.app.tastybuds.domain

import com.app.tastybuds.data.repo.FavoritesRepository
import javax.inject.Inject
import com.app.tastybuds.util.Result

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
                if (isFavoriteResult.data) {
                    favoritesRepository.removeFavorite(userId, menuItemId, null)
                } else {
                    favoritesRepository.addFavorite(userId, menuItemId, restaurantId)
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
                if (isFavoriteResult.data) {
                    favoritesRepository.removeFavorite(userId, null, restaurantId)
                } else {
                    favoritesRepository.addFavorite(userId, null, restaurantId)
                }
            }

            is Result.Error -> Result.Error(isFavoriteResult.message)
            is Result.Loading -> Result.Loading
        }
    }

    suspend fun getUserFavorites(userId: String) = favoritesRepository.getUserFavorites(userId)

    suspend fun isMenuItemFavorite(userId: String, menuItemId: String) =
        favoritesRepository.isMenuItemFavorite(userId, menuItemId)

    suspend fun isRestaurantFavorite(userId: String, restaurantId: String) =
        favoritesRepository.isRestaurantFavorite(userId, restaurantId)
}