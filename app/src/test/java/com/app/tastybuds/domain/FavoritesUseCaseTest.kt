package com.app.tastybuds.domain

import com.app.tastybuds.data.model.FavoriteWithMenuItemResponse
import com.app.tastybuds.data.model.FavoriteWithRestaurantResponse
import com.app.tastybuds.data.repo.FavoritesRepository
import com.app.tastybuds.util.Result
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
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

    private val sampleFavoriteWithRestaurantResponse = FavoriteWithRestaurantResponse(
        id = 1,
        restaurantId = testRestaurantId,
        createdAt = "2024-01-01",
        restaurant = mockk {
            every { name } returns "Test Restaurant"
            every { cuisine } returns listOf("Italian", "Pizza")
            every { rating } returns 4.5f
            every { reviewCount } returns 100
            every { deliveryTime } returns "30-45 min"
            every { distance } returns "2.5 km"
            every { priceRange } returns "$$"
            every { imageUrl } returns "https://test.com/restaurant.jpg"
            every { deliveryFee } returns 3.99f
            every { isOpen } returns true
        }
    )

    private val sampleFavoriteWithMenuItemResponse = FavoriteWithMenuItemResponse(
        id = 2,
        menuItemId = testMenuItemId,
        createdAt = "2024-01-01",
        menuItem = mockk {
            every { name } returns "Test Pizza"
            every { description } returns "Delicious test pizza"
            every { price } returns 15.99
            every { image } returns "https://test.com/pizza.jpg"
            every { rating } returns 4.2f
            every { reviewCount } returns 50
            every { isPopular } returns true
            every { isSpicy } returns false
            every { restaurant } returns mockk {
                every { name } returns "Test Restaurant"
                every { id } returns testRestaurantId
            }
        }
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
    fun `getFavoriteRestaurantsForUI should return mapped restaurant UI models on success`() =
        runTest {
            val favoriteResponses = listOf(sampleFavoriteWithRestaurantResponse)
            coEvery { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) } returns Result.Success(
                favoriteResponses
            )

            val result = favoritesUseCase.getFavoriteRestaurantsForUI(testUserId)

            assertTrue("Expected Result.Success", result is Result.Success)
            val data = (result as Result.Success).data
            assertEquals(1, data.size)

            val restaurantUi = data.first()
            assertEquals(1, restaurantUi.id)
            assertEquals(testRestaurantId, restaurantUi.restaurantId)
            assertEquals("Test Restaurant", restaurantUi.name)
            assertEquals("Italian, Pizza", restaurantUi.cuisine)
            assertEquals(4.5f, restaurantUi.rating, 0.01f)
            assertEquals(100, restaurantUi.reviewCount)
            assertEquals("30-45 min", restaurantUi.deliveryTime)
            assertEquals("2.5 km", restaurantUi.distance)
            assertEquals("$$", restaurantUi.priceRange)
            assertEquals("https://test.com/restaurant.jpg", restaurantUi.imageUrl)
            assertEquals(3.99f, restaurantUi.deliveryFee, 0.01f)
            assertEquals(true, restaurantUi.isOpen)
            assertEquals("2024-01-01", restaurantUi.createdAt)

            coVerify { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) }
        }

    @Test
    fun `getFavoriteRestaurantsForUI should return empty list when no favorites exist`() = runTest {
        coEvery { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) } returns Result.Success(
            emptyList()
        )

        val result = favoritesUseCase.getFavoriteRestaurantsForUI(testUserId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(0, (result as Result.Success).data.size)
        coVerify { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) }
    }

    @Test
    fun `getFavoriteRestaurantsForUI should return error when repository fails`() = runTest {
        val errorMessage = "Failed to load favorite restaurants"
        coEvery { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) } returns Result.Error(
            errorMessage
        )

        val result = favoritesUseCase.getFavoriteRestaurantsForUI(testUserId)

        assertTrue("Expected Result.Error", result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).message)
        coVerify { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) }
    }

    @Test
    fun `getFavoriteRestaurantsForUI should return loading when repository is loading`() = runTest {
        coEvery { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) } returns Result.Loading

        val result = favoritesUseCase.getFavoriteRestaurantsForUI(testUserId)

        assertTrue("Expected Result.Loading", result is Result.Loading)
        coVerify { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) }
    }

    @Test
    fun `getFavoriteMenuItemsForUI should return mapped menu item UI models on success`() =
        runTest {
            val favoriteResponses = listOf(sampleFavoriteWithMenuItemResponse)
            coEvery { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) } returns Result.Success(
                favoriteResponses
            )

            val result = favoritesUseCase.getFavoriteMenuItemsForUI(testUserId)

            assertTrue("Expected Result.Success", result is Result.Success)
            val data = (result as Result.Success).data
            assertEquals(1, data.size)

            val menuItemUi = data.first()
            assertEquals(2, menuItemUi.id)
            assertEquals(testMenuItemId, menuItemUi.menuItemId)
            assertEquals("Test Pizza", menuItemUi.name)
            assertEquals("Delicious test pizza", menuItemUi.description)
            assertEquals(15.99f, menuItemUi.price, 0.01f)
            assertEquals("https://test.com/pizza.jpg", menuItemUi.imageUrl)
            assertEquals(4.2f, menuItemUi.rating, 0.01f)
            assertEquals(50, menuItemUi.reviewCount)
            assertEquals("Test Restaurant", menuItemUi.restaurantName)
            assertEquals(testRestaurantId, menuItemUi.restaurantId)
            assertEquals(true, menuItemUi.isPopular)
            assertEquals(false, menuItemUi.isSpicy)
            assertEquals("2024-01-01", menuItemUi.createdAt)

            coVerify { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) }
        }

    @Test
    fun `getFavoriteMenuItemsForUI should return empty list when no favorites exist`() = runTest {
        coEvery { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) } returns Result.Success(
            emptyList()
        )

        val result = favoritesUseCase.getFavoriteMenuItemsForUI(testUserId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(0, (result as Result.Success).data.size)
        coVerify { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) }
    }

    @Test
    fun `getFavoriteMenuItemsForUI should return error when repository fails`() = runTest {
        val errorMessage = "Failed to load favorite menu items"
        coEvery { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) } returns Result.Error(
            errorMessage
        )

        val result = favoritesUseCase.getFavoriteMenuItemsForUI(testUserId)

        assertTrue("Expected Result.Error", result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).message)
        coVerify { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) }
    }

    @Test
    fun `getFavoriteMenuItemsForUI should return loading when repository is loading`() = runTest {
        coEvery { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) } returns Result.Loading

        val result = favoritesUseCase.getFavoriteMenuItemsForUI(testUserId)

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
    fun `getFavoriteRestaurantsForUI should handle multiple restaurants correctly`() = runTest {
        val multipleResponses = listOf(
            sampleFavoriteWithRestaurantResponse,
            sampleFavoriteWithRestaurantResponse.copy(id = 2, restaurantId = "restaurant_2")
        )
        coEvery { favoritesRepository.getFavoriteRestaurantsWithDetails(testUserId) } returns Result.Success(
            multipleResponses
        )

        val result = favoritesUseCase.getFavoriteRestaurantsForUI(testUserId)

        assertTrue("Expected Result.Success", result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(2, data.size)
        assertEquals(1, data[0].id)
        assertEquals(2, data[1].id)
    }

    @Test
    fun `getFavoriteMenuItemsForUI should handle multiple menu items correctly`() = runTest {
        val multipleResponses = listOf(
            sampleFavoriteWithMenuItemResponse,
            sampleFavoriteWithMenuItemResponse.copy(id = 3, menuItemId = "menu_item_2")
        )
        coEvery { favoritesRepository.getFavoriteMenuItemsWithDetails(testUserId) } returns Result.Success(
            multipleResponses
        )

        val result = favoritesUseCase.getFavoriteMenuItemsForUI(testUserId)

        assertTrue("Expected Result.Success", result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(2, data.size)
        assertEquals(2, data[0].id)
        assertEquals(3, data[1].id)
    }
}