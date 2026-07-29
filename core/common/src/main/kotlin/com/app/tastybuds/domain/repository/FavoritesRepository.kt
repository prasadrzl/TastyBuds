package com.app.tastybuds.domain.repository

import com.app.tastybuds.domain.model.FavoriteMenuItem
import com.app.tastybuds.domain.model.FavoriteRestaurant
import com.app.tastybuds.util.Result

interface FavoritesRepository {
    suspend fun addFavorite(
        userId: String,
        menuItemId: String?,
        restaurantId: String?
    ): Result<Boolean>

    suspend fun removeFavorite(
        userId: String,
        menuItemId: String?,
        restaurantId: String?
    ): Result<Boolean>

    suspend fun isMenuItemFavorite(userId: String, menuItemId: String): Result<Boolean>
    suspend fun isRestaurantFavorite(userId: String, restaurantId: String): Result<Boolean>
    suspend fun getFavoriteRestaurantsWithDetails(userId: String): Result<List<FavoriteRestaurant>>
    suspend fun getFavoriteMenuItemsWithDetails(userId: String): Result<List<FavoriteMenuItem>>
}
