package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.FoodCustomization
import com.app.tastybuds.domain.model.FoodDetails
import com.app.tastybuds.domain.model.FoodDetailsData
import com.app.tastybuds.domain.repository.FoodDetailsRepository
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
class FoodDetailsUseCaseTest {

    @MockK
    private lateinit var repository: FoodDetailsRepository
    private lateinit var useCase: FoodDetailsUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = FoodDetailsUseCase(repository)
    }

    @Test
    fun `invoke delegates to getFoodDetailsData`() = runTest {
        coEvery { repository.getFoodDetailsData("f1", "u1") } returns Result.Success(mockk<FoodDetailsData>())

        val result = useCase("f1", "u1")

        assertTrue(result is Result.Success)
        coVerify { repository.getFoodDetailsData("f1", "u1") }
    }

    @Test
    fun `getFoodDetails delegates to the repository`() = runTest {
        coEvery { repository.getFoodDetails("f1", "u1") } returns Result.Success(mockk<FoodDetails>())
        useCase.getFoodDetails("f1", "u1")
        coVerify { repository.getFoodDetails("f1", "u1") }
    }

    @Test
    fun `getCustomizationOptions delegates to the repository`() = runTest {
        coEvery { repository.getCustomizationOptions("m1") } returns Result.Success(mockk<FoodCustomization>())
        useCase.getCustomizationOptions("m1")
        coVerify { repository.getCustomizationOptions("m1") }
    }
}
