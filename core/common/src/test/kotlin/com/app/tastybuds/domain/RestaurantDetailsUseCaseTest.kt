package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.RestaurantDetailsData
import com.app.tastybuds.domain.repository.RestaurantDetailsRepository
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
class RestaurantDetailsUseCaseTest {

    @MockK
    private lateinit var repository: RestaurantDetailsRepository
    private lateinit var useCase: RestaurantDetailsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = RestaurantDetailsUseCase(repository)
    }

    @Test
    fun `invoke delegates to getRestaurantDetailsData`() = runTest {
        coEvery { repository.getRestaurantDetailsData("r1", "u1") } returns Result.Success(mockk<RestaurantDetailsData>())

        val result = useCase("r1", "u1")

        assertTrue(result is Result.Success)
        coVerify { repository.getRestaurantDetailsData("r1", "u1") }
    }

    @Test
    fun `getRestaurantMenuItems delegates to the repository`() = runTest {
        coEvery { repository.getRestaurantMenuItems("r1") } returns Result.Success(emptyList())

        val result = useCase.getRestaurantMenuItems("r1")

        assertTrue(result is Result.Success)
        coVerify { repository.getRestaurantMenuItems("r1") }
    }
}
