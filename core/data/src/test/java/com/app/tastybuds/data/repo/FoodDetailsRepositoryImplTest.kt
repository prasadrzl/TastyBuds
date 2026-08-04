package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.FavoriteResponse
import com.app.tastybuds.data.model.FoodCustomizationResponse
import com.app.tastybuds.data.model.FoodDetailsResponse
import com.app.tastybuds.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class FoodDetailsRepositoryImplTest {

    private lateinit var apiService: TastyBudsApiService
    private lateinit var repository: FoodDetailsRepositoryImpl

    @Before
    fun setUp() {
        apiService = mockk()
        repository = FoodDetailsRepositoryImpl(apiService)
    }

    @Test
    fun `getFoodDetails maps a thrown exception to an error`() = runTest {
        coEvery { apiService.getFoodDetails(any()) } throws RuntimeException("network down")
        assertTrue(repository.getFoodDetails("f1", "u1") is Result.Error)
    }

    @Test
    fun `getCustomizationOptions maps a thrown exception to an error`() = runTest {
        coEvery { apiService.getCustomizationOptions(any()) } throws RuntimeException("network down")
        assertTrue(repository.getCustomizationOptions("f1") is Result.Error)
    }

    @Test
    fun `getFoodDetailsData maps a thrown exception to an error`() = runTest {
        coEvery { apiService.getFoodDetails(any()) } throws RuntimeException("network down")
        assertTrue(repository.getFoodDetailsData("f1", "u1") is Result.Error)
    }

    @Test
    fun `getFoodDetails returns success and resolves favorite state`() = runTest {
        coEvery { apiService.getFoodDetails(any()) } returns FoodDetailsResponse(id = "f1", name = "Burger")
        coEvery { apiService.getUserFavorites(any()) } returns Response.success(emptyList<FavoriteResponse>())

        assertTrue(repository.getFoodDetails("f1", "u1") is Result.Success)
    }

    @Test
    fun `getCustomizationOptions returns success`() = runTest {
        coEvery { apiService.getCustomizationOptions(any()) } returns FoodCustomizationResponse()

        assertTrue(repository.getCustomizationOptions("f1") is Result.Success)
    }

    @Test
    fun `getFoodDetailsData returns success by combining details and customization`() = runTest {
        coEvery { apiService.getFoodDetails(any()) } returns FoodDetailsResponse(id = "f1", name = "Burger")
        coEvery { apiService.getUserFavorites(any()) } returns Response.success(emptyList<FavoriteResponse>())
        coEvery { apiService.getCustomizationOptions(any()) } returns FoodCustomizationResponse()

        assertTrue(repository.getFoodDetailsData("f1", "u1") is Result.Success)
    }
}
