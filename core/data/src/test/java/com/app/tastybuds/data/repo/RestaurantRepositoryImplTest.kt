package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.CategoryMenuItemResponse
import com.app.tastybuds.data.model.CategoryRestaurantResponse
import com.app.tastybuds.data.model.RestaurantResponse
import com.app.tastybuds.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class RestaurantRepositoryImplTest {

    private lateinit var apiService: TastyBudsApiService
    private lateinit var repository: RestaurantRepositoryImpl

    @Before
    fun setUp() {
        apiService = mockk()
        repository = RestaurantRepositoryImpl(apiService)
    }

    @Test
    fun `getRestaurantsByCategory maps a populated list to success`() = runTest {
        coEvery { apiService.getRestaurantsByCategory(any()) } returns
                listOf(RestaurantResponse(id = "r1", name = "Pizza Place"))
        assertTrue(repository.getRestaurantsByCategory("c1") is Result.Success)
    }

    @Test
    fun `getRestaurantsByCategory maps a thrown exception to an error`() = runTest {
        coEvery { apiService.getRestaurantsByCategory(any()) } throws RuntimeException("down")
        assertTrue(repository.getRestaurantsByCategory("c1") is Result.Error)
    }

    @Test
    fun `searchRestaurants maps a populated list to success`() = runTest {
        coEvery { apiService.searchRestaurants(any()) } returns
                listOf(RestaurantResponse(id = "r1", name = "Pizza Place"))
        assertTrue(repository.searchRestaurants("pizza") is Result.Success)
    }

    @Test
    fun `getAllRestaurants maps a populated list to success`() = runTest {
        coEvery { apiService.getRecommendedRestaurants() } returns
                listOf(RestaurantResponse(id = "r1", name = "Pizza Place"))
        assertTrue(repository.getAllRestaurants() is Result.Success)
    }

    @Test
    fun `getTopRestaurantsByCategory maps a populated list to success`() = runTest {
        coEvery { apiService.getTopRestaurantsByCategory(any()) } returns
                listOf(CategoryRestaurantResponse(id = "r1", name = "Pizza Place"))
        assertTrue(repository.getTopRestaurantsByCategory("c1") is Result.Success)
    }

    @Test
    fun `getMenuItemsByCategory maps a populated list to success`() = runTest {
        coEvery { apiService.getMenuItemsByCategory(any(), any()) } returns
                listOf(CategoryMenuItemResponse(id = "m1", name = "Margherita"))
        assertTrue(repository.getMenuItemsByCategory("c1") is Result.Success)
    }

    @Test
    fun `getRecommendedRestaurantsByCategory maps a populated list to success`() = runTest {
        coEvery { apiService.getRecommendedRestaurantsByCategory(any()) } returns
                listOf(CategoryRestaurantResponse(id = "r1", name = "Pizza Place"))
        assertTrue(repository.getRecommendedRestaurantsByCategory("c1") is Result.Success)
    }

    @Test
    fun `getCategoryDetails aggregates the sub-queries`() = runTest {
        coEvery { apiService.getTopRestaurantsByCategory(any()) } returns emptyList()
        coEvery { apiService.getMenuItemsByCategory(any(), any()) } returns emptyList()
        coEvery { apiService.getRecommendedRestaurantsByCategory(any()) } returns emptyList()

        assertTrue(repository.getCategoryDetails("c1") is Result.Success)
    }

    @Test
    fun `getCategoryDetails degrades failing sub-queries to empty sections`() = runTest {
        // Each sub-query catches its own error and returns Result.Error, which
        // getCategoryDetails coalesces to an empty section -> overall still Success.
        coEvery { apiService.getTopRestaurantsByCategory(any()) } throws RuntimeException("down")
        coEvery { apiService.getMenuItemsByCategory(any(), any()) } returns emptyList()
        coEvery { apiService.getRecommendedRestaurantsByCategory(any()) } returns emptyList()

        assertTrue(repository.getCategoryDetails("c1") is Result.Success)
    }

    @Test
    fun `getRestaurantsByIds short-circuits to success for a blank id string`() = runTest {
        assertTrue(repository.getRestaurantsByIds("") is Result.Success)
    }

    @Test
    fun `getRestaurantsByIds maps an empty body to success`() = runTest {
        coEvery { apiService.getRestaurantsByIdsForCollection(any()) } returns Response.success(emptyList<RestaurantResponse>())
        assertTrue(repository.getRestaurantsByIds("1,2") is Result.Success)
    }

    @Test
    fun `getRestaurantsByIds maps an unsuccessful response to an error`() = runTest {
        coEvery { apiService.getRestaurantsByIdsForCollection(any()) } returns
                Response.error(500, "err".toResponseBody(null))
        assertTrue(repository.getRestaurantsByIds("1,2") is Result.Error)
    }
}
