package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.FavoriteResponse
import com.app.tastybuds.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesRepositoryTest {

    private lateinit var apiService: TastyBudsApiService
    private lateinit var repository: FavoritesRepositoryImpl

    private val userId = "user_123"

    @Before
    fun setUp() {
        apiService = mockk()
        repository = FavoritesRepositoryImpl(apiService)
    }

    private fun <T> errorResponse(code: Int = 400): Response<T> =
        Response.error(code, "error".toResponseBody("text/plain".toMediaTypeOrNull()))

    // ---------- addFavorite ----------

    @Test
    fun `addFavorite returns Success(true) when API returns a non-empty body`() = runTest {
        coEvery { apiService.addFavorite(any()) } returns
            Response.success(listOf(FavoriteResponse(id = 1, userId = userId, menuItemId = "m1")))

        val result = repository.addFavorite(userId, menuItemId = "m1", restaurantId = null)

        assertTrue(result is Result.Success)
        assertEquals(true, (result as Result.Success).data)
    }

    @Test
    fun `addFavorite returns Error when API body is empty`() = runTest {
        coEvery { apiService.addFavorite(any()) } returns Response.success(emptyList())

        val result = repository.addFavorite(userId, "m1", null)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `addFavorite returns Error when API call is unsuccessful`() = runTest {
        coEvery { apiService.addFavorite(any()) } returns errorResponse()

        val result = repository.addFavorite(userId, "m1", null)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `addFavorite returns Error on network exception`() = runTest {
        coEvery { apiService.addFavorite(any()) } throws RuntimeException("boom")

        val result = repository.addFavorite(userId, "m1", null)

        assertTrue(result is Result.Error)
    }

    // ---------- removeFavorite ----------

    @Test
    fun `removeFavorite returns Success(true) when API is successful`() = runTest {
        coEvery { apiService.removeFavorite(userId, "m1", null) } returns Response.success(emptyList())

        val result = repository.removeFavorite(userId, "m1", null)

        assertTrue(result is Result.Success)
        assertEquals(true, (result as Result.Success).data)
    }

    @Test
    fun `removeFavorite returns Error when API is unsuccessful`() = runTest {
        coEvery { apiService.removeFavorite(any(), any(), any()) } returns errorResponse()

        val result = repository.removeFavorite(userId, "m1", null)

        assertTrue(result is Result.Error)
    }

    // ---------- isMenuItemFavorite / isRestaurantFavorite ----------

    @Test
    fun `isMenuItemFavorite returns true when the item is in the user's favorites`() = runTest {
        coEvery { apiService.getUserFavorites(userId) } returns Response.success(
            listOf(FavoriteResponse(menuItemId = "m1"), FavoriteResponse(menuItemId = "m2"))
        )

        val result = repository.isMenuItemFavorite(userId, "m2")

        assertEquals(true, (result as Result.Success).data)
    }

    @Test
    fun `isMenuItemFavorite returns false when the item is not favorited`() = runTest {
        coEvery { apiService.getUserFavorites(userId) } returns
            Response.success(listOf(FavoriteResponse(menuItemId = "m1")))

        val result = repository.isMenuItemFavorite(userId, "nope")

        assertEquals(false, (result as Result.Success).data)
    }

    @Test
    fun `isMenuItemFavorite returns false when the API call fails`() = runTest {
        coEvery { apiService.getUserFavorites(userId) } returns errorResponse()

        val result = repository.isMenuItemFavorite(userId, "m1")

        assertEquals(false, (result as Result.Success).data)
    }

    @Test
    fun `isRestaurantFavorite returns true when the restaurant is favorited`() = runTest {
        coEvery { apiService.getUserFavorites(userId) } returns
            Response.success(listOf(FavoriteResponse(restaurantId = "r1")))

        val result = repository.isRestaurantFavorite(userId, "r1")

        assertEquals(true, (result as Result.Success).data)
    }

    // ---------- withDetails ----------

    @Test
    fun `getFavoriteRestaurantsWithDetails returns Error when API is unsuccessful`() = runTest {
        coEvery { apiService.getFavoriteRestaurantsWithDetails(userId) } returns errorResponse()

        val result = repository.getFavoriteRestaurantsWithDetails(userId)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `getFavoriteMenuItemsWithDetails returns an empty list for an empty successful body`() = runTest {
        coEvery { apiService.getFavoriteMenuItemsWithDetails(userId) } returns Response.success(emptyList())

        val result = repository.getFavoriteMenuItemsWithDetails(userId)

        assertTrue(result is Result.Success)
        assertEquals(emptyList<Any>(), (result as Result.Success).data)
    }
}
