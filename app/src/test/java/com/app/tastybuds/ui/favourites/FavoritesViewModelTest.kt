package com.app.tastybuds.ui.favourites

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app.tastybuds.data.model.FavoriteMenuItemUi
import com.app.tastybuds.data.model.FavoriteRestaurantUi
import com.app.tastybuds.domain.FavoritesUseCase
import com.app.tastybuds.ui.favorites.FavoriteError
import com.app.tastybuds.ui.favorites.FavoritesUiState
import com.app.tastybuds.ui.favorites.FavoritesViewModel
import com.app.tastybuds.util.Result
import com.app.tastybuds.utils.MainDispatcherRule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions

@ExperimentalCoroutinesApi
class FavoritesViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var favoritesUseCase: FavoritesUseCase

    private lateinit var viewModel: FavoritesViewModel

    private val sampleRestaurantFavorite = FavoriteRestaurantUi(
        id = 1,
        restaurantId = "restaurant_1",
        name = "Test Restaurant",
        cuisine = "Italian",
        rating = 4.5f,
        reviewCount = 100,
        deliveryTime = "30-45 min",
        distance = "2.5 km",
        priceRange = "$$",
        imageUrl = "https://test.com/image.jpg",
        deliveryFee = 3.99f,
        isOpen = true,
        createdAt = "2024-01-01"
    )

    private val sampleMenuItemFavorite = FavoriteMenuItemUi(
        id = 2,
        menuItemId = "menu_item_1",
        name = "Test Pizza",
        description = "Delicious test pizza",
        price = 15.99f,
        imageUrl = "https://test.com/pizza.jpg",
        rating = 4.2f,
        reviewCount = 50,
        restaurantName = "Test Restaurant",
        restaurantId = "restaurant_1",
        isPopular = true,
        isSpicy = false,
        createdAt = "2024-01-01"
    )

    private val testUserId = "test_user_123"

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        viewModel = FavoritesViewModel(favoritesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUserFavoritesWithDetails should update state with loading true initially`() = runTest {
        coEvery { favoritesUseCase.getFavoriteRestaurantsForUI(testUserId) } returns Result.Loading
        coEvery { favoritesUseCase.getFavoriteMenuItemsForUI(testUserId) } returns Result.Loading

        viewModel.loadUserFavoritesWithDetails(testUserId)
        advanceUntilIdle()

        val initialState = viewModel.uiState.first()
        Assertions.assertFalse(initialState.isLoading)
        Assertions.assertNull(initialState.error)
    }

    @Test
    fun `loadUserFavoritesWithDetails should load both restaurant and menu item favorites successfully`() =
        runTest {
            val restaurantFavorites = listOf(sampleRestaurantFavorite)
            val menuItemFavorites = listOf(sampleMenuItemFavorite)

            coEvery { favoritesUseCase.getFavoriteRestaurantsForUI(testUserId) } returns Result.Success(
                restaurantFavorites
            )
            coEvery { favoritesUseCase.getFavoriteMenuItemsForUI(testUserId) } returns Result.Success(
                menuItemFavorites
            )

            viewModel.loadUserFavoritesWithDetails(testUserId)
            advanceUntilIdle()

            val finalState = viewModel.uiState.first()
            Assertions.assertFalse(finalState.isLoading)
            Assertions.assertEquals(restaurantFavorites, finalState.favoriteRestaurants)
            Assertions.assertEquals(menuItemFavorites, finalState.favoriteMenuItems)
            Assertions.assertTrue(finalState.favoriteRestaurantsWithDetails.isEmpty())
            Assertions.assertTrue(finalState.favoriteMenuItemsWithDetails.isEmpty())
            Assertions.assertNull(finalState.error)
        }

    @Test
    fun `loadUserFavoritesWithDetails should handle restaurant favorites error`() = runTest {
        val errorMessage = "Failed to load restaurant favorites"
        val menuItemFavorites = listOf(sampleMenuItemFavorite)

        coEvery { favoritesUseCase.getFavoriteRestaurantsForUI(testUserId) } returns Result.Error(
            errorMessage
        )
        coEvery { favoritesUseCase.getFavoriteMenuItemsForUI(testUserId) } returns Result.Success(
            menuItemFavorites
        )

        viewModel.loadUserFavoritesWithDetails(testUserId)
        advanceUntilIdle()

        val finalState = viewModel.uiState.first()
        Assertions.assertFalse(finalState.isLoading)
        Assertions.assertTrue(finalState.favoriteRestaurants.isEmpty())
        Assertions.assertTrue(finalState.favoriteMenuItems.isEmpty())
        Assertions.assertEquals(FavoriteError.LOAD_FAVORITES_FAILED, finalState.error)
    }

    @Test
    fun `loadUserFavoritesWithDetails should handle menu item favorites error`() = runTest {
        val errorMessage = "Failed to load menu item favorites"
        val restaurantFavorites = listOf(sampleRestaurantFavorite)

        coEvery { favoritesUseCase.getFavoriteRestaurantsForUI(testUserId) } returns Result.Success(
            restaurantFavorites
        )
        coEvery { favoritesUseCase.getFavoriteMenuItemsForUI(testUserId) } returns Result.Error(
            errorMessage
        )

        viewModel.loadUserFavoritesWithDetails(testUserId)
        advanceUntilIdle()

        val finalState = viewModel.uiState.first()
        Assertions.assertFalse(finalState.isLoading)
        Assertions.assertTrue(finalState.favoriteRestaurants.isEmpty())
        Assertions.assertTrue(finalState.favoriteMenuItems.isEmpty())
        Assertions.assertEquals(FavoriteError.LOAD_FAVORITES_FAILED, finalState.error)
    }

    @Test
    fun `loadUserFavoritesWithDetails should handle both errors and show restaurant error first`() =
        runTest {
            val restaurantErrorMessage = "Restaurant error"
            val menuItemErrorMessage = "Menu item error"

            coEvery { favoritesUseCase.getFavoriteRestaurantsForUI(testUserId) } returns Result.Error(
                restaurantErrorMessage
            )
            coEvery { favoritesUseCase.getFavoriteMenuItemsForUI(testUserId) } returns Result.Error(
                menuItemErrorMessage
            )

            viewModel.loadUserFavoritesWithDetails(testUserId)
            advanceUntilIdle()

            val finalState = viewModel.uiState.first()
            Assertions.assertFalse(finalState.isLoading)
            Assertions.assertTrue(finalState.favoriteRestaurants.isEmpty())
            Assertions.assertTrue(finalState.favoriteMenuItems.isEmpty())
            Assertions.assertEquals(FavoriteError.LOAD_FAVORITES_FAILED, finalState.error)
        }

    @Test
    fun `loadUserFavoritesWithDetails should handle unexpected exception`() = runTest {
        val exceptionMessage = "Network error"
        coEvery { favoritesUseCase.getFavoriteRestaurantsForUI(testUserId) } throws RuntimeException(
            exceptionMessage
        )

        viewModel.loadUserFavoritesWithDetails(testUserId)
        advanceUntilIdle()

        val finalState = viewModel.uiState.first()
        Assertions.assertFalse(finalState.isLoading)
        Assertions.assertTrue(finalState.favoriteRestaurants.isEmpty())
        Assertions.assertTrue(finalState.favoriteMenuItems.isEmpty())
        Assertions.assertEquals(FavoriteError.LOAD_FAVORITES_FAILED, finalState.error)
    }

    @Test
    fun `removeFavorite should remove restaurant favorite successfully`() = runTest {
        val restaurantFavorites = listOf(sampleRestaurantFavorite)
        val initialState = FavoritesUiState(favoriteRestaurants = restaurantFavorites)
        viewModel._uiState.value = initialState

        coEvery {
            favoritesUseCase.toggleRestaurantFavorite(
                testUserId,
                sampleRestaurantFavorite.restaurantId
            )
        } returns Result.Success(false)
        coEvery { favoritesUseCase.getFavoriteRestaurantsForUI(testUserId) } returns Result.Success(
            emptyList()
        )
        coEvery { favoritesUseCase.getFavoriteMenuItemsForUI(testUserId) } returns Result.Success(
            emptyList()
        )

        viewModel.removeFavorite(sampleRestaurantFavorite.id, testUserId)
        advanceUntilIdle()

        coVerify {
            favoritesUseCase.toggleRestaurantFavorite(
                testUserId,
                sampleRestaurantFavorite.restaurantId
            )
        }
        coVerify { favoritesUseCase.getFavoriteRestaurantsForUI(testUserId) }
        coVerify { favoritesUseCase.getFavoriteMenuItemsForUI(testUserId) }
    }

    @Test
    fun `removeFavorite should remove menu item favorite successfully`() = runTest {
        val menuItemFavorites = listOf(sampleMenuItemFavorite)
        val initialState = FavoritesUiState(favoriteMenuItems = menuItemFavorites)
        viewModel._uiState.value = initialState

        coEvery {
            favoritesUseCase.toggleMenuItemFavorite(
                testUserId,
                sampleMenuItemFavorite.menuItemId
            )
        } returns Result.Success(false)
        coEvery { favoritesUseCase.getFavoriteRestaurantsForUI(testUserId) } returns Result.Success(
            emptyList()
        )
        coEvery { favoritesUseCase.getFavoriteMenuItemsForUI(testUserId) } returns Result.Success(
            emptyList()
        )

        viewModel.removeFavorite(sampleMenuItemFavorite.id, testUserId)
        advanceUntilIdle()

        coVerify {
            favoritesUseCase.toggleMenuItemFavorite(
                testUserId,
                sampleMenuItemFavorite.menuItemId
            )
        }
        coVerify { favoritesUseCase.getFavoriteRestaurantsForUI(testUserId) }
        coVerify { favoritesUseCase.getFavoriteMenuItemsForUI(testUserId) }
    }

    @Test
    fun `removeFavorite should handle restaurant favorite removal error`() = runTest {
        val restaurantFavorites = listOf(sampleRestaurantFavorite)
        val initialState = FavoritesUiState(favoriteRestaurants = restaurantFavorites)
        viewModel._uiState.value = initialState

        val errorMessage = "Failed to remove restaurant favorite"
        coEvery {
            favoritesUseCase.toggleRestaurantFavorite(
                testUserId,
                sampleRestaurantFavorite.restaurantId
            )
        } returns Result.Error(errorMessage)
        coEvery { favoritesUseCase.getFavoriteRestaurantsForUI(testUserId) } returns Result.Success(
            emptyList()
        )
        coEvery { favoritesUseCase.getFavoriteMenuItemsForUI(testUserId) } returns Result.Success(
            emptyList()
        )

        viewModel.removeFavorite(sampleRestaurantFavorite.id, testUserId)
        advanceUntilIdle()

        val finalState = viewModel.uiState.first()
        Assertions.assertEquals(FavoriteError.REMOVE_RESTAURANT_FAILED, finalState.error)
    }

    @Test
    fun `removeFavorite should handle menu item favorite removal error`() = runTest {
        val menuItemFavorites = listOf(sampleMenuItemFavorite)
        val initialState = FavoritesUiState(favoriteMenuItems = menuItemFavorites)
        viewModel._uiState.value = initialState

        val errorMessage = "Failed to remove menu item favorite"
        coEvery {
            favoritesUseCase.toggleMenuItemFavorite(
                testUserId,
                sampleMenuItemFavorite.menuItemId
            )
        } returns Result.Error(errorMessage)

        viewModel.removeFavorite(sampleMenuItemFavorite.id, testUserId)
        advanceUntilIdle()

        val finalState = viewModel.uiState.first()
        Assertions.assertEquals(FavoriteError.REMOVE_MENU_ITEM_FAILED, finalState.error)
    }

    @Test
    fun `removeFavorite should handle loading state for restaurant favorite`() = runTest {
        val restaurantFavorites = listOf(sampleRestaurantFavorite)
        val initialState = FavoritesUiState(favoriteRestaurants = restaurantFavorites)
        viewModel._uiState.value = initialState

        coEvery {
            favoritesUseCase.toggleRestaurantFavorite(
                testUserId,
                sampleRestaurantFavorite.restaurantId
            )
        } returns Result.Loading
        coEvery { favoritesUseCase.getFavoriteRestaurantsForUI(testUserId) } returns Result.Success(
            emptyList()
        )
        coEvery { favoritesUseCase.getFavoriteMenuItemsForUI(testUserId) } returns Result.Success(
            emptyList()
        )

        viewModel.removeFavorite(sampleRestaurantFavorite.id, testUserId)
        advanceUntilIdle()

        val finalState = viewModel.uiState.first()
        Assertions.assertFalse(finalState.isLoading)
        Assertions.assertNull(finalState.error)
    }

    @Test
    fun `removeFavorite should handle loading state for menu item favorite`() = runTest {
        val menuItemFavorites = listOf(sampleMenuItemFavorite)
        val initialState = FavoritesUiState(favoriteMenuItems = menuItemFavorites)
        viewModel._uiState.value = initialState

        coEvery {
            favoritesUseCase.toggleMenuItemFavorite(
                testUserId,
                sampleMenuItemFavorite.menuItemId
            )
        } returns Result.Loading
        coEvery { favoritesUseCase.getFavoriteRestaurantsForUI(testUserId) } returns Result.Success(
            emptyList()
        )
        coEvery { favoritesUseCase.getFavoriteMenuItemsForUI(testUserId) } returns Result.Success(
            emptyList()
        )

        viewModel.removeFavorite(sampleMenuItemFavorite.id, testUserId)
        advanceUntilIdle()

        val finalState = viewModel.uiState.first()
        Assertions.assertFalse(finalState.isLoading)
        Assertions.assertNull(finalState.error)
    }

    @Test
    fun `removeFavorite should show error when favorite item not found`() = runTest {
        val initialState = FavoritesUiState()
        viewModel._uiState.value = initialState
        val nonExistentFavoriteId = 999

        viewModel.removeFavorite(nonExistentFavoriteId, testUserId)
        advanceUntilIdle()

        val finalState = viewModel.uiState.first()
        Assertions.assertEquals(FavoriteError.FAVORITE_NOT_FOUND, finalState.error)
    }

    @Test
    fun `removeFavorite should not call use case when favorite item not found`() = runTest {
        val initialState = FavoritesUiState()
        viewModel._uiState.value = initialState
        val nonExistentFavoriteId = 999

        viewModel.removeFavorite(nonExistentFavoriteId, testUserId)
        advanceUntilIdle()

        coVerify(exactly = 0) { favoritesUseCase.toggleRestaurantFavorite(any(), any()) }
        coVerify(exactly = 0) { favoritesUseCase.toggleMenuItemFavorite(any(), any()) }
    }

    @Test
    fun `initial state should be empty and not loading`() {
        val initialState = viewModel.uiState.value
        Assertions.assertFalse(initialState.isLoading)
        Assertions.assertTrue(initialState.favoriteRestaurants.isEmpty())
        Assertions.assertTrue(initialState.favoriteMenuItems.isEmpty())
        Assertions.assertTrue(initialState.favoriteRestaurantsWithDetails.isEmpty())
        Assertions.assertTrue(initialState.favoriteMenuItemsWithDetails.isEmpty())
        Assertions.assertNull(initialState.error)
    }

    @Test
    fun `loadUserFavoritesWithDetails should handle empty results successfully`() = runTest {
        coEvery { favoritesUseCase.getFavoriteRestaurantsForUI(testUserId) } returns Result.Success(
            emptyList()
        )
        coEvery { favoritesUseCase.getFavoriteMenuItemsForUI(testUserId) } returns Result.Success(
            emptyList()
        )

        viewModel.loadUserFavoritesWithDetails(testUserId)
        advanceUntilIdle()

        val finalState = viewModel.uiState.first()
        Assertions.assertFalse(finalState.isLoading)
        Assertions.assertTrue(finalState.favoriteRestaurants.isEmpty())
        Assertions.assertTrue(finalState.favoriteMenuItems.isEmpty())
        Assertions.assertNull(finalState.error)
    }

    @Test
    fun `removeFavorite should handle mixed restaurant and menu item favorites`() = runTest {
        val restaurantFavorites = listOf(sampleRestaurantFavorite)
        val menuItemFavorites = listOf(sampleMenuItemFavorite)
        val initialState = FavoritesUiState(
            favoriteRestaurants = restaurantFavorites,
            favoriteMenuItems = menuItemFavorites
        )
        viewModel._uiState.value = initialState

        coEvery {
            favoritesUseCase.toggleRestaurantFavorite(
                testUserId,
                sampleRestaurantFavorite.restaurantId
            )
        } returns Result.Success(false)
        coEvery { favoritesUseCase.getFavoriteRestaurantsForUI(testUserId) } returns Result.Success(
            emptyList()
        )
        coEvery { favoritesUseCase.getFavoriteMenuItemsForUI(testUserId) } returns Result.Success(
            menuItemFavorites
        )

        viewModel.removeFavorite(sampleRestaurantFavorite.id, testUserId)
        advanceUntilIdle()

        coVerify {
            favoritesUseCase.toggleRestaurantFavorite(
                testUserId,
                sampleRestaurantFavorite.restaurantId
            )
        }
        coVerify(exactly = 0) { favoritesUseCase.toggleMenuItemFavorite(any(), any()) }
    }

    @Test
    fun `error state should clear when new loading starts`() = runTest {
        val initialStateWithError = FavoritesUiState(error = FavoriteError.FAVORITE_NOT_FOUND)
        viewModel._uiState.value = initialStateWithError

        coEvery { favoritesUseCase.getFavoriteRestaurantsForUI(testUserId) } returns Result.Success(
            emptyList()
        )
        coEvery { favoritesUseCase.getFavoriteMenuItemsForUI(testUserId) } returns Result.Success(
            emptyList()
        )

        viewModel.loadUserFavoritesWithDetails(testUserId)
        advanceUntilIdle()

        val finalState = viewModel.uiState.first()
        Assertions.assertNull(finalState.error)
    }
}

private val FavoritesViewModel._uiState: MutableStateFlow<FavoritesUiState>
    get() = this::class.java.getDeclaredField("_uiState").apply { isAccessible = true }
        .get(this) as MutableStateFlow<FavoritesUiState>