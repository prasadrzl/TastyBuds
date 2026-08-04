    package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.ComboResponse
import com.app.tastybuds.data.model.FavoriteResponse
import com.app.tastybuds.data.model.MenuItemResponse
import com.app.tastybuds.data.model.RestaurantReviewResponse
import com.app.tastybuds.data.model.RestaurantVoucherResponse
import com.app.tastybuds.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class RestaurantDetailsRepositoryImplTest {

    private lateinit var apiService: TastyBudsApiService
    private lateinit var repository: RestaurantDetailsRepositoryImpl

    @Before
    fun setUp() {
        apiService = mockk()
        repository = RestaurantDetailsRepositoryImpl(apiService)
    }

    private fun <T> httpError() = Response.error<T>(500, "err".toResponseBody(null))

    @Test
    fun `getRestaurantMenuItems maps a populated body to success`() = runTest {
        coEvery { apiService.getRestaurantMenuItems(any()) } returns
                Response.success(listOf(MenuItemResponse(id = "m1", name = "Margherita")))
        assertTrue(repository.getRestaurantMenuItems("r1") is Result.Success)
    }

    @Test
    fun `getRestaurantMenuItems maps an error response`() = runTest {
        coEvery { apiService.getRestaurantMenuItems(any()) } returns httpError()
        assertTrue(repository.getRestaurantMenuItems("r1") is Result.Error)
    }

    @Test
    fun `getForYouMenuItems maps a populated body to success`() = runTest {
        coEvery { apiService.getForYouMenuItems(any(), any()) } returns
                Response.success(listOf(MenuItemResponse(id = "m1", name = "Margherita")))
        assertTrue(repository.getForYouMenuItems("r1") is Result.Success)
    }

    @Test
    fun `getRestaurantReviews maps an empty body to success`() = runTest {
        coEvery { apiService.getRestaurantReviews(any(), any()) } returns Response.success(emptyList<RestaurantReviewResponse>())
        assertTrue(repository.getRestaurantReviews("r1") is Result.Success)
    }

    @Test
    fun `getRestaurantReviews maps an error response`() = runTest {
        coEvery { apiService.getRestaurantReviews(any(), any()) } returns httpError()
        assertTrue(repository.getRestaurantReviews("r1") is Result.Error)
    }

    @Test
    fun `getRestaurantVouchers maps an empty body to success`() = runTest {
        coEvery { apiService.getUserRestaurantVouchers(any(), any()) } returns Response.success(emptyList<RestaurantVoucherResponse>())
        assertTrue(repository.getRestaurantVouchers("r1", "u1") is Result.Success)
    }

    @Test
    fun `getRestaurantCombos maps an empty body to success`() = runTest {
        coEvery { apiService.getRestaurantCombos(any(), any()) } returns Response.success(emptyList<ComboResponse>())
        assertTrue(repository.getRestaurantCombos("r1") is Result.Success)
    }

    @Test
    fun `getRestaurantCombos maps an error response`() = runTest {
        coEvery { apiService.getRestaurantCombos(any(), any()) } returns httpError()
        assertTrue(repository.getRestaurantCombos("r1") is Result.Error)
    }

    @Test
    fun `getRestaurantDetails maps an unsuccessful response to an error`() = runTest {
        coEvery { apiService.getUserFavorites(any()) } returns Response.success(emptyList<FavoriteResponse>())
        coEvery { apiService.getRestaurantDetails(any()) } returns httpError()

        assertTrue(repository.getRestaurantDetails("r1", "u1") is Result.Error)
    }

    @Test
    fun `getRestaurantDetails maps a thrown exception to an error`() = runTest {
        coEvery { apiService.getRestaurantDetails(any()) } throws RuntimeException("down")
        assertTrue(repository.getRestaurantDetails("r1", "u1") is Result.Error)
    }
}
