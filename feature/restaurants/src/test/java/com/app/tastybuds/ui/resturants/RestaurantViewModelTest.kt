package com.app.tastybuds.ui.resturants

import com.app.tastybuds.domain.RestaurantUseCase
import com.app.tastybuds.domain.model.CategoryDetailsData
import com.app.tastybuds.util.Result
import com.app.tastybuds.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var restaurantUseCase: RestaurantUseCase

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `loadCategoryDetails populates the UI state on success`() = runTest {
        val details = mockk<CategoryDetailsData> {
            every { topRestaurants } returns listOf(mockk())
            every { menuItems } returns emptyList()
            every { recommendedRestaurants } returns emptyList()
        }
        coEvery { restaurantUseCase.getCategoryDetails("cat_1") } returns Result.Success(details)

        val viewModel = RestaurantViewModel(restaurantUseCase)
        viewModel.loadCategoryDetails("cat_1", "Pizza")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.categoryDetails)
        assertNull(state.error)
    }

    @Test
    fun `loadCategoryDetails exposes an error when the use case fails`() = runTest {
        coEvery { restaurantUseCase.getCategoryDetails("cat_1") } returns Result.Error("boom")

        val viewModel = RestaurantViewModel(restaurantUseCase)
        viewModel.loadCategoryDetails("cat_1", "Pizza")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
    }
}
