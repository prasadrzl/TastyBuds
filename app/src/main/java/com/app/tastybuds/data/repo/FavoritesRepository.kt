package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.AddFavoriteRequest
import com.app.tastybuds.data.model.FavoriteResponse
import com.app.tastybuds.data.model.FavoriteWithMenuItemResponse
import com.app.tastybuds.data.model.FavoriteWithRestaurantResponse
import javax.inject.Inject
import javax.inject.Singleton
import com.app.tastybuds.util.Result

interface FavoritesRepository {
    suspend fun getUserFavorites(userId: String): Result<List<FavoriteResponse>>
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
    suspend fun getFavoriteRestaurantsWithDetails(userId: String): Result<List<FavoriteWithRestaurantResponse>>
    suspend fun getFavoriteMenuItemsWithDetails(userId: String): Result<List<FavoriteWithMenuItemResponse>>
}

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val apiService: TastyBudsApiService
) : FavoritesRepository {

    override suspend fun getUserFavorites(userId: String): Result<List<FavoriteResponse>> {
        return try {
            val response = apiService.getUserFavorites("eq.$userId")
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Failed to get favorites: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun addFavorite(
        userId: String,
        menuItemId: String?,
        restaurantId: String?
    ): Result<Boolean> {
        return try {
            val request = AddFavoriteRequest(
                userId = userId,
                menuItemId = menuItemId,
                restaurantId = restaurantId
            )
            val response = apiService.addFavorite(request)
            if (response.isSuccessful) {
                val favorites = response.body()
                if (!favorites.isNullOrEmpty()) {
                    Result.Success(true)
                } else {
                    Result.Error("Failed to add favorite: Empty response")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.Error("Failed to add favorite: ${response.message()} - $errorBody")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun removeFavorite(
        userId: String,
        menuItemId: String?,
        restaurantId: String?
    ): Result<Boolean> {
        return try {
            val response = apiService.removeFavorite(
                userId = "eq.$userId",
                menuItemId = menuItemId?.let { "eq.$it" },
                restaurantId = restaurantId?.let { "eq.$it" }
            )
            if (response.isSuccessful) {
                Result.Success(true)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.Error("Failed to remove favorite: ${response.message()} - $errorBody")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun isMenuItemFavorite(userId: String, menuItemId: String): Result<Boolean> {
        return try {
            val response = apiService.getUserFavorites(
                userId = "eq.$userId",
                select = "menu_item_id"
            )
            if (response.isSuccessful) {
                val favorites = response.body() ?: emptyList()
                val isFavorite = favorites.any { it.menuItemId == menuItemId }
                Result.Success(isFavorite)
            } else {
                Result.Success(false)
            }
        } catch (e: Exception) {
            Result.Success(false)
        }
    }

    override suspend fun isRestaurantFavorite(
        userId: String,
        restaurantId: String
    ): Result<Boolean> {
        return try {
            val response = apiService.getUserFavorites(
                userId = "eq.$userId",
                select = "restaurant_id"
            )
            if (response.isSuccessful) {
                val favorites = response.body() ?: emptyList()
                val isFavorite = favorites.any { it.restaurantId == restaurantId }
                Result.Success(isFavorite)
            } else {
                Result.Success(false)
            }
        } catch (e: Exception) {
            Result.Success(false)
        }
    }

    override suspend fun getFavoriteRestaurantsWithDetails(userId: String): Result<List<FavoriteWithRestaurantResponse>> {
        return try {
            val response = apiService.getFavoriteRestaurantsWithDetails("eq.$userId")
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Failed to get favorite restaurants: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun getFavoriteMenuItemsWithDetails(userId: String): Result<List<FavoriteWithMenuItemResponse>> {
        return try {
            val response = apiService.getFavoriteMenuItemsWithDetails("eq.$userId")
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Failed to get favorite menu items: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }
}