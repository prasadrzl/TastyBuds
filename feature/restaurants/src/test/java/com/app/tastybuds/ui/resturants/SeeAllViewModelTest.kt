package com.app.tastybuds.ui.resturants

import com.app.tastybuds.domain.RestaurantUseCase
import com.app.tastybuds.domain.model.CategoryMenuItem
import com.app.tastybuds.domain.model.CategoryRestaurant
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SeeAllViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var restaurantUseCase: RestaurantUseCase

    private lateinit var viewModel: SeeAllViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = SeeAllViewModel(restaurantUseCase)
    }

    @Test
    fun `loadItems loads top restaurants`() = runTest {
        coEvery { restaurantUseCase.getTopRestaurantsByCategory("c1") } returns
                Result.Success(listOf(mockk<CategoryRestaurant>()))

        viewModel.loadItems("c1", "restaurants")

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.restaurants.size)
    }

    @Test
    fun `loadItems loads menu items`() = runTest {
        coEvery { restaurantUseCase.getMenuItemsByCategory("c1") } returns
                Result.Success(listOf(mockk<CategoryMenuItem>(), mockk()))

        viewModel.loadItems("c1", "popular_items")

        assertEquals(2, viewModel.uiState.value.menuItems.size)
    }

    @Test
    fun `loadItems rejects an unknown type`() = runTest {
        viewModel.loadItems("c1", "widgets")

        assertEquals("Unknown content type: widgets", viewModel.uiState.value.error)
    }

    @Test
    fun `loadItems surfaces a use-case error`() = runTest {
        coEvery { restaurantUseCase.getTopRestaurantsByCategory("c1") } returns Result.Error("boom")

        viewModel.loadItems("c1", "top_restaurants")

        assertNotNull(viewModel.uiState.value.error)
    }
}
