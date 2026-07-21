package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.FavoriteMenuItem
import com.app.tastybuds.domain.model.FavoriteRestaurant
import com.app.tastybuds.domain.repository.FavoritesRepository
import com.app.tastybuds.util.Result
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class FavoritesUseCaseTest {

    @MockK
    private lateinit var favoritesRepository: FavoritesRepository

    private lateinit var favoritesUseCase: FavoritesUseCase

    private val testUserId = "user_123"
    private val testMenuItemId = "menu_item_456"
    private val testRestaurantId = "restaurant_789"

    // DTO -> domain mapping is now verified against FavoriteResponseMapper directly
    // (it moved into the data layer, alongside FavoritesRepositoryImpl). The use case
    // is a pure pass-through of whatever domain models the repository returns.
    private val sampleFavoriteRestaurant = FavoriteRestaurant(
        id = 1,
        restaurantId = testRestaurantId,
        name = "Test Restaurant",
        cuisine = "Italian, Pizza",
        rating = 4.5f,
        reviewCount = 100,
        deliveryTime = "30-45 min",
        distance = "2.5 km",
        priceRange = "$$",
        imageUrl = "https://test.com/restaurant.jpg",
        deliveryFee = 3.99f,
        isOpen = true,
        createdAt = "2024-01-01"
    )

    private val sampleFavoriteMenuItem = FavoriteMenuItem(
        id = 2,
        menuItemId = testMenuItemId,
        name = "Test Pizza",
        description = "Delicious test pizza",
        price = 15.99f,
        imageUrl = "https://test.com/pizza.jpg",
        rating = 4.2f,
        reviewCount = 50,
        restaurantName = "Test Restaurant",
        restaurantId = testRestaurantId,
        isPopular = true,
        isSpicy = false,
        createdAt = "2024-01-01"
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        favoritesUseCase = FavoritesUseCase(favoritesRepository)
    }

    @Test
    fun `toggleMenuItemFavorite should add favorite when item is not favorited`() = runTest {
        coEvery {
            favoritesRepository.isMenuItemFavorite(
                testUserId,
                testMenuItemId
            )
        } returns Result.Success(false)
        coEvery {
            favoritesRepository.addFavorite(
                testUserId,
                testMenuItemId,
                testRestaurantId
            )
        } returns Result.Success(true)

        val result =
            favoritesUseCase.toggleMenuItemFavorite(testUserId, testMenuItemId, testRestaurantId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(true, (result as Result.Success).data)
        coVerify { favoritesRepository.isMenuItemFavorite(testUserId, testMenuItemId) }
        coVerify { favoritesRepository.addFavorite(testUserId, testMenuItemId, testRestaurantId) }
        coVerify(exactly = 0) { favoritesRepository.removeFavorite(any(), any(), any()) }
    }

    @Test
    fun `toggleMenuItemFavorite should remove favorite when item is already favorited`() = runTest {
        coEvery {
            favoritesRepository.isMenuItemFavorite(
                testUserId,
                testMenuItemId
            )
        } returns Result.Success(true)
        coEvery {
            favoritesRepository.removeFavorite(
                testUserId,
                testMenuItemId,
                null
            )
        } returns Result.Success(true)

        val result = favoritesUseCase.toggleMenuItemFavorite(testUserId, testMenuItemId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(false, (result as Result.Success).data)
        coVerify { favoritesRepository.isMenuItemFavorite(testUserId, testMenuItemId) }
        coVerify { favoritesRepository.removeFavorite(testUserId, testMenuItemId, null) }
        coVerify(exactly = 0) { favoritesRepository.addFavorite(any(), any(), any()) }
    }

    @Test
    fun `toggleMenuItemFavorite should return error when checking favorite status fails`() =
        runTest {
            val errorMessage = "Failed to check favorite status"
            coEvery {
                favoritesRepository.isMenuItemFavorite(
                    testUserId,
                    testMenuItemId
                )
            } returns Result.Error(errorMessage)

            val result = favoritesUseCase.toggleMenuItemFavorite(testUserId, testMenuItemId)

            assertTrue("Expected Result.Error", result is Result.Error)
            assertEquals(errorMessage, (result as Result.Error).message)
            coVerify { favoritesRepository.isMenuItemFavorite(testUserId, testMenuItemId) }
            coVerify(exactly = 0) { favoritesRepository.addFavorite(any(), any(), any()) }
            coVerify(exactly = 0) { favoritesRepository.removeFavorite(any(), any(), any()) }
        }

    @Test
    fun `toggleMenuItemFavorite should return loading when checking favorite status is loading`() =
        runTest {
            coEvery {
                favoritesRepository.isMenuItemFavorite(
                    testUserId,
                    testMenuItemId
                )
            } returns Result.Loading

            val result = favoritesUseCase.toggleMenuItemFavorite(testUserId, testMenuItemId)

            assertTrue("Expected Result.Loading", result is Result.Loading)
            coVerify { favoritesRepository.isMenuItemFavorite(testUserId, testMenuItemId) }
            coVerify(exactly = 0) { favoritesRepository.addFavorite(any(), any(), any()) }
            coVerify(exactly = 0) { favoritesRepository.removeFavorite(any(), any(), any()) }
        }

    @Test
    fun `toggleMenuItemFavorite should return error when adding favorite fails`() = runTest {
        val errorMessage = "Failed to add favorite"
        coEvery {
            favoritesRepository.isMenuItemFavorite(
                testUserId,
                testMenuItemId
            )
        } returns Result.Success(false)
        coEvery {
            favoritesRepository.addFavorite(
                testUserId,
                testMenuItemId,
                testRestaurantId
            )
        } returns Result.Error(errorMessage)

        val result =
            favoritesUseCase.toggleMenuItemFavorite(testUserId, testMenuItemId, testRestaurantId)

        assertTrue("Expected Result.Error", result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).message)
        coVerify { favoritesRepository.isMenuItemFavorite(testUserId, testMenuItemId) }
        coVerify { favoritesRepository.addFavorite(testUserId, testMenuItemId, testRestaurantId) }
    }

    @Test
    fun `toggleMenuItemFavorite should return error when removing favorite fails`() = runTest {
        val errorMessage = "Failed to remove favorite"
        coEvery {
            favoritesRepository.isMenuItemFavorite(
                testUserId,
                testMenuItemId
            )
        } returns Result.Success(true)
        coEvery {
            favoritesRepository.removeFavorite(
                testUserId,
                testMenuItemId,
                null
            )
        } returns Result.Error(errorMessage)

        val result = favoritesUseCase.toggleMenuItemFavorite(testUserId, testMenuItemId)

        assertTrue("Expected Result.Error", result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).message)
        coVerify { favoritesRepository.isMenuItemFavorite(testUserId, testMenuItemId) }
        coVerify { favoritesRepository.removeFavorite(testUserId, testMenuItemId, null) }
    }

    @Test
    fun `toggleMenuItemFavorite should return loading when adding favorite is loading`() = runTest {
        coEvery {
            favoritesRepository.isMenuItemFavorite(
                testUserId,
                testMenuItemId
            )
        } returns Result.Success(false)
        coEvery {
            favoritesRepository.addFavorite(
                testUserId,
                testMenuItemId,
                testRestaurantId
            )
        } returns Result.Loading

        val result =
            favoritesUseCase.toggleMenuItemFavorite(testUserId, testMenuItemId, testRestaurantId)

        assertTrue("Expected Result.Loading", result is Result.Loading)
        coVerify { favoritesRepository.isMenuItemFavorite(testUserId, testMenuItemId) }
        coVerify { favoritesRepository.addFavorite(testUserId, testMenuItemId, testRestaurantId) }
    }

    @Test
    fun `toggleMenuItemFavorite should return loading when removing favorite is loading`() =
        runTest {
            coEvery {
                favoritesRepository.isMenuItemFavorite(
                    testUserId,
                    testMenuItemId
                )
            } returns Result.Success(true)
            coEvery {
                favoritesRepository.removeFavorite(
                    testUserId,
                    testMenuItemId,
                    null
                )
            } returns Result.Loading

            val result = favoritesUseCase.toggleMenuItemFavorite(testUserId, testMenuItemId)

            assertTrue("Expected Result.Loading", result is Result.Loading)
            coVerify { favoritesRepository.isMenuItemFavorite(testUserId, testMenuItemId) }
            coVerify { favoritesRepository.removeFavorite(testUserId, testMenuItemId, null) }
        }

    @Test
    fun `toggleRestaurantFavorite should add favorite when restaurant is not favorited`() =
        runTest {
            coEvery {
                favoritesRepository.isRestaurantFavorite(
                    testUserId,
                    testRestaurantId
                )
            } returns Result.Success(false)
            coEvery {
                favoritesRepository.addFavorite(
                    testUserId,
                    null,
                    testRestaurantId
                )
            } returns Result.Success(true)

            val result = favoritesUseCase.toggleRestaurantFavorite(testUserId, testRestaurantId)

            assertTrue("Expected Result.Success", result is Result.Success)
            assertEquals(true, (result as Result.Success).data)
            coVerify { favoritesRepository.isRestaurantFavorite(testUserId, testRestaurantId) }
            coVerify { favoritesRepository.addFavorite(testUserId, null, testRestaurantId) }
            coVerify(exactly = 0) { favoritesRepository.removeFavorite(any(), any(), any()) }
        }

    @Test
    fun `toggleRestaurantFavorite should remove favorite when restaurant is already favorited`() =
        runTest {
            coEvery {
                favoritesRepository.isRestaurantFavorite(
                    testUserId,
                    testRestaurantId
                )
            } returns Result.Success(true)
            coEvery {
                favoritesRepository.removeFavorite(
                    testUserId,
                    null,
                    testRestaurantId
                )
            } returns Result.Success(true)

            val result = favoritesUseCase.toggleRestaurantFavorite(testUserId, testRestaurantId)

            assertTrue("Expected Result.Success", result is Result.Success)
            assertEquals(false, (result as Result.Success).data)
            coVerify { favoritesRepository.isRestaurantFavorite(testUserId, testRestaurantId) }
            coVerify { favoritesRepository.removeFavorite(testUserId, null, testRestaurantId) }
            coVerify(exactly = 0) { favoritesRepository.addFavorite(any(), any(), any()) }
        }

    @Test
    fun `toggleRestaurantFavorite should return error when checking favorite status fails`() =
        runTest {
            val errorMessage = "Failed to check restaurant favorite status"
            coEvery {
                favoritesRepository.isRestaurantFavorite(
                    testUserId,
                    testRestaurantId
                )
            } returns Result.Error(errorMessage)

            val result = favoritesUseCase.toggleRestaurantFavorite(testUserId, testRestaurantId)

            assertTrue("Expected Result.Error", result is Result.Error)
            assertEquals(errorMessage, (result as Result.Error).message)
            coVerify { favoritesRepository.isRestaurantFavorite(testUserId, testRestaurantId) }
            coVerify(exactly = 0) { favoritesRepository.addFavorite(any(), any(), any()) }
            coVerify(exactly = 0) { favoritesRepository.removeFavorite(any(), any(), any()) }
        }

    @Test
    fun `toggleRestaurantFavorite should return loading when checking favorite status is loading`() =
        runTest {
            coEvery {
                favoritesRepository.isRestaurantFavorite(
                    testUserId,
                    testRestaurantId
                )
            } returns Result.Loading

            val result = favoritesUseCase.toggleRestaurantFavorite(testUserId, testRestaurantId)

            assertTrue("Expected Result.Loading", result is Result.Loading)
            coVerify { favoritesRepository.isRestaurantFavorite(testUserId, testRestaurantId) }
            coVerify(exactly = 0) { favoritesRepository.addFavorite(any(), any(), any()) }
            coVerify(exactly = 0) { favoritesRepository.removeFavorite(any(), any(), any()) }
        }

    @Test
    fun `toggleRestaurantFavorite should return error when adding favorite fails`() = runTest {
        val errorMessage = "Failed to add restaurant favorite"
        coEvery {
            favoritesRepository.isRestaurantFavorite(
                testUserId,
                testRestaurantId
            )
        } returns Result.Success(false)
        coEvery {
            favoritesRepository.addFavorite(
                testUserId,
                null,
                testRestaurantId
            )
        } returns Result.Error(errorMessage)

        val result = favoritesUseCase.toggleRestaurantFavorite(testUserId, testRestaurantId)

        assertTrue("Expected Result.Error", result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).message)
        coVerify { favoritesRepository.isRestaurantFavorite(testUserId, testRestaurantId) }
        coVerify { favoritesRepository.addFavorite(testUserId, null, testRestaurantId) }
    }

    @Test
    fun `toggleRestaurantFavorite should return error when removing favorite fails`() = runTest {
        val errorMessage = "Failed to remove restaurant favorite"
        coEvery {
            favoritesRepository.isRestaurantFavorite(
                testUserId,
                testRestaurantId
            )
        } returns Result.Success(true)
        coEvery {
            favoritesRepository.removeFavorite(
                testUserId,
                null,
                testRestaurantId
            )
        } returns Result.Error(errorMessage)

        val result = favoritesUseCase.toggleRestaurantFavorite(testUserId, testRestaurantId)

        assertTrue("Expected Result.Error", result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).message)
        coVerify { favoritesRepository.isRestaurantFavorite(testUserId, testRestaurantId) }
        coVerify { favoritesRepository.removeFavorite(testUserId, null, testRestaurantId) }
    }

    @Test
    fun `toggleRestaurantFavorite should return loading when adding favorite is loading`() =
        runTest {
            coEvery {
                favoritesRepository.isRestaurantFavorite(
                    testUserId,
                    testRestaurantId
                )
            } returns Result.Success(false)
            coEvery {
                favoritesRepository.addFavorite(
                    testUserId,
                    null,
                    testRestaurantId
                )
            } returns Result.Loading

            val result = favoritesUseCase.toggleRestaurantFavorite(testUserId, testRestaurantId)

            assertTrue("Expected Result.Loading", result is Result.Loading)
            coVerify { favoritesRepository.isRestaurantFavorite(testUserId, testRestaurantId) }
            coVerify { favoritesRepository.addFavorite(testUserId, null, testRestaurantId) }
        }

    @Test
    fun `toggleRestaurantFavorite should return loading when removing favorite is loading`() =
        runTest {
            coEvery {
                favoritesRepository.isRestaurantFavorite(
                    testUserId,
                    testRestaurantId
                )
            } returns Result.Success(true)
            coEvery {
                favoritesRepository.removeFavorite(
                    testUserId,
                    null,
                    testRestaurantId
                )
            } returns Result.Loading

            val result = favoritesUseCase.toggleRestaurantFavorite(testUserId, testRestaurantId)

            assertTrue("Expected Result.Loading", result is Result.Loading)
            coVerify { favoritesRepository.isRestaurantFavorite(testUserId, testRestaurantId) }
            coVerify { favoritesRepository.removeFavorite(testUserId, null, testRestaurantId) }
        }

    @Test
    fun `getFavoriteRestaurants should pass through domain models from repository on success`() =
        runTest {
            coEvery { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) } returns Result.Success(
                listOf(sampleFavoriteRestaurant)
            )

            val result = favoritesUseCase.getFavoriteRestaurants(testUserId)

            assertTrue("Expected Result.Success", result is Result.Success)
            val data = (result as Result.Success).data
            assertEquals(listOf(sampleFavoriteRestaurant), data)
            coVerify { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) }
        }

    @Test
    fun `getFavoriteRestaurants should return empty list when no favorites exist`() = runTest {
        coEvery { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) } returns Result.Success(
            emptyList()
        )

        val result = favoritesUseCase.getFavoriteRestaurants(testUserId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(0, (result as Result.Success).data.size)
        coVerify { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) }
    }

    @Test
    fun `getFavoriteRestaurants should return error when repository fails`() = runTest {
        val errorMessage = "Failed to load favorite restaurants"
        coEvery { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) } returns Result.Error(
            errorMessage
        )

        val result = favoritesUseCase.getFavoriteRestaurants(testUserId)

        assertTrue("Expected Result.Error", result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).message)
        coVerify { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) }
    }

    @Test
    fun `getFavoriteRestaurants should return loading when repository is loading`() = runTest {
        coEvery { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) } returns Result.Loading

        val result = favoritesUseCase.getFavoriteRestaurants(testUserId)

        assertTrue("Expected Result.Loading", result is Result.Loading)
        coVerify { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) }
    }

    @Test
    fun `getFavoriteMenuItems should pass through domain models from repository on success`() =
        runTest {
            coEvery { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) } returns Result.Success(
                listOf(sampleFavoriteMenuItem)
            )

            val result = favoritesUseCase.getFavoriteMenuItems(testUserId)

            assertTrue("Expected Result.Success", result is Result.Success)
            val data = (result as Result.Success).data
            assertEquals(listOf(sampleFavoriteMenuItem), data)
            coVerify { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) }
        }

    @Test
    fun `getFavoriteMenuItems should return empty list when no favorites exist`() = runTest {
        coEvery { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) } returns Result.Success(
            emptyList()
        )

        val result = favoritesUseCase.getFavoriteMenuItems(testUserId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(0, (result as Result.Success).data.size)
        coVerify { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) }
    }

    @Test
    fun `getFavoriteMenuItems should return error when repository fails`() = runTest {
        val errorMessage = "Failed to load favorite menu items"
        coEvery { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) } returns Result.Error(
            errorMessage
        )

        val result = favoritesUseCase.getFavoriteMenuItems(testUserId)

        assertTrue("Expected Result.Error", result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).message)
        coVerify { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) }
    }

    @Test
    fun `getFavoriteMenuItems should return loading when repository is loading`() = runTest {
        coEvery { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) } returns Result.Loading

        val result = favoritesUseCase.getFavoriteMenuItems(testUserId)

        assertTrue("Expected Result.Loading", result is Result.Loading)
        coVerify { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) }
    }


    @Test
    fun `toggleMenuItemFavorite should handle null restaurantId parameter correctly`() = runTest {
        coEvery {
            favoritesRepository.isMenuItemFavorite(
                testUserId,
                testMenuItemId
            )
        } returns Result.Success(false)
        coEvery {
            favoritesRepository.addFavorite(
                testUserId,
                testMenuItemId,
                null
            )
        } returns Result.Success(true)

        val result =
            favoritesUseCase.toggleMenuItemFavorite(testUserId, testMenuItemId, restaurantId = null)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(true, (result as Result.Success).data)
        coVerify { favoritesRepository.addFavorite(testUserId, testMenuItemId, null) }
    }

    @Test
    fun `getFavoriteRestaurants should handle multiple restaurants correctly`() = runTest {
        val multiple = listOf(
            sampleFavoriteRestaurant,
            sampleFavoriteRestaurant.copy(id = 2, restaurantId = "restaurant_2")
        )
        coEvery { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) } returns Result.Success(
            multiple
        )

        val result = favoritesUseCase.getFavoriteRestaurants(testUserId)

        assertTrue("Expected Result.Success", result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(2, data.size)
        assertEquals(1, data[0].id)
        assertEquals(2, data[1].id)
    }

    @Test
    fun `getFavoriteMenuItems should handle multiple menu items correctly`() = runTest {
        val multiple = listOf(
            sampleFavoriteMenuItem,
            sampleFavoriteMenuItem.copy(id = 3, menuItemId = "menu_item_2")
        )
        coEvery { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) } returns Result.Success(
            multiple
        )

        val result = favoritesUseCase.getFavoriteMenuItems(testUserId)

        assertTrue("Expected Result.Success", result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(2, data.size)
        assertEquals(2, data[0].id)
        assertEquals(3, data[1].id)
    }
}