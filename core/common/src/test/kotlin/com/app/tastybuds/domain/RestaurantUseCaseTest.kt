package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.domain.repository.RestaurantRepository
import com.app.tastybuds.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantUseCaseTest {

    @MockK
    private lateinit var repository: RestaurantRepository

    private lateinit var useCase: RestaurantUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = RestaurantUseCase(repository)
    }

    @Test
    fun `searchRestaurants returns all restaurants for a blank query`() = runTest {
        coEvery { repository.getAllRestaurants() } returns Result.Success(listOf(mockk<Restaurant>()))

        val result = useCase.searchRestaurants("   ")

        assertTrue(result is Result.Success)
        coVerify { repository.getAllRestaurants() }
        coVerify(exactly = 0) { repository.searchRestaurants(any()) }
    }

    @Test
    fun `searchRestaurants queries the repository for a non-blank query`() = runTest {
        coEvery { repository.searchRestaurants("pizza") } returns Result.Success(emptyList())

        useCase.searchRestaurants("pizza")

        coVerify { repository.searchRestaurants("pizza") }
        coVerify(exactly = 0) { repository.getAllRestaurants() }
    }

    @Test
    fun `getRestaurantsByCategory delegates to the repository`() = runTest {
        coEvery { repository.getRestaurantsByCategory("cat_1") } returns Result.Success(emptyList())

        val result = useCase.getRestaurantsByCategory("cat_1")

        assertTrue(result is Result.Success)
        coVerify { repository.getRestaurantsByCategory("cat_1") }
    }
}
