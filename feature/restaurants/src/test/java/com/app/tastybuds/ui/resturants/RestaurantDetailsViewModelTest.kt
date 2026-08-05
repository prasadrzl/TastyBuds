package com.app.tastybuds.ui.resturants

import com.app.tastybuds.domain.FavoritesUseCase
import com.app.tastybuds.domain.RestaurantDetailsUseCase
import com.app.tastybuds.domain.model.RestaurantDetailsData
import com.app.tastybuds.domain.model.RestaurantMenuItem
import com.app.tastybuds.domain.repository.AuthRepository
import com.app.tastybuds.util.Result
import com.app.tastybuds.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var restaurantDetailsUseCase: RestaurantDetailsUseCase

    @MockK
    private lateinit var favoritesUseCase: FavoritesUseCase

    @MockK
    private lateinit var authRepository: AuthRepository

    private lateinit var viewModel: RestaurantDetailsViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = RestaurantDetailsViewModel(restaurantDetailsUseCase, favoritesUseCase, authRepository)
    }

    private fun menuItem(id: String, category: String, isFavorite: Boolean = false) =
        RestaurantMenuItem(
            id = id, name = id, description = "", price = 1.0f, imageUrl = "",
            rating = 0.0f, reviewCount = 0, category = category, isPopular = false,
            isSpicy = false, prepTime = "", isFavorite = isFavorite
        )

    private fun stubDetailsWithMenu(menu: List<RestaurantMenuItem>) {
        coEvery { restaurantDetailsUseCase.invoke("r1", "u1") } returns
                Result.Success(mockk<RestaurantDetailsData>(relaxed = true))
        coEvery { restaurantDetailsUseCase.getRestaurantMenuItems("r1") } returns Result.Success(menu)
    }

    @Test
    fun `loadRestaurantDetails loads details, menu and derives sorted categories`() = runTest {
        stubDetailsWithMenu(listOf(menuItem("m1", "Pizza"), menuItem("m2", "Drinks")))

        viewModel.loadRestaurantDetails("r1", "u1")

        assertFalse(viewModel.uiState.value.isLoading)
        assertNotNull(viewModel.uiState.value.restaurantData)
        assertEquals(2, viewModel.allMenuItems.value.size)
        assertEquals(listOf("Drinks", "Pizza"), viewModel.categories.value)
        assertEquals(2, viewModel.filteredMenuItems.value.size)
    }

    @Test
    fun `loadRestaurantDetails surfaces an error`() = runTest {
        coEvery { restaurantDetailsUseCase.invoke("r1", "u1") } returns Result.Error("boom")

        viewModel.loadRestaurantDetails("r1", "u1")

        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `selectCategory filters the menu items`() = runTest {
        stubDetailsWithMenu(listOf(menuItem("m1", "Pizza"), menuItem("m2", "Drinks")))
        viewModel.loadRestaurantDetails("r1", "u1")

        viewModel.selectCategory("Pizza")

        assertEquals(listOf("m1"), viewModel.filteredMenuItems.value.map { it.id })
    }

    @Test
    fun `toggleMenuItemFavorite flips the favorite flag for the item`() = runTest {
        stubDetailsWithMenu(listOf(menuItem("m1", "Pizza", isFavorite = false)))
        viewModel.loadRestaurantDetails("r1", "u1")
        coEvery { favoritesUseCase.toggleMenuItemFavorite("u1", "m1", "r1") } returns Result.Success(true)

        viewModel.toggleMenuItemFavorite("m1")

        assertTrue(viewModel.allMenuItems.value.first { it.id == "m1" }.isFavorite)
    }

    @Test
    fun `toggleFavorite delegates to the favorites use case`() = runTest {
        stubDetailsWithMenu(emptyList())
        viewModel.loadRestaurantDetails("r1", "u1")
        coEvery { favoritesUseCase.toggleRestaurantFavorite("u1", "r1") } returns Result.Success(true)

        viewModel.toggleFavorite()

        io.mockk.coVerify { favoritesUseCase.toggleRestaurantFavorite("u1", "r1") }
    }

    @Test
    fun `getUserId delegates to the auth repository`() = runTest {
        io.mockk.every { authRepository.getUserId() } returns kotlinx.coroutines.flow.flowOf("u1")
        assertEquals("u1", viewModel.getUserId().first())
    }

    @Test
    fun `retry reloads the restaurant details`() = runTest {
        stubDetailsWithMenu(emptyList())
        viewModel.loadRestaurantDetails("r1", "u1")

        viewModel.retry()

        io.mockk.coVerify(atLeast = 2) { restaurantDetailsUseCase.invoke("r1", "u1") }
    }
}
