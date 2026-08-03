package com.app.tastybuds.ui.resturants

import com.app.tastybuds.domain.RestaurantUseCase
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.util.Result
import com.app.tastybuds.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CollectionListingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var restaurantUseCase: RestaurantUseCase

    private lateinit var viewModel: CollectionListingViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = CollectionListingViewModel(restaurantUseCase)
    }

    @Test
    fun `loadCollectionRestaurants populates restaurants on success`() = runTest {
        coEvery { restaurantUseCase.getRestaurantsByIds("1,2") } returns
                Result.Success(listOf(mockk<Restaurant>(), mockk()))

        viewModel.loadCollectionRestaurants("1,2")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.restaurants.size)
        assertNull(state.error)
    }

    @Test
    fun `loadCollectionRestaurants surfaces an error`() = runTest {
        coEvery { restaurantUseCase.getRestaurantsByIds("1,2") } returns Result.Error("boom")

        viewModel.loadCollectionRestaurants("1,2")

        assertNotNull(viewModel.uiState.value.error)
    }
}
