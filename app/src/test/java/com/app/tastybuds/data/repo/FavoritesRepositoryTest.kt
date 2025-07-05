package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.AddFavoriteRequest
import com.app.tastybuds.data.model.FavoriteResponse
import com.app.tastybuds.data.model.FavoriteWithMenuItemResponse
import com.app.tastybuds.data.model.FavoriteWithRestaurantResponse
import com.app.tastybuds.util.Result
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class FavoritesRepositoryTest {

    @MockK
    private lateinit var apiService: TastyBudsApiService

    private lateinit var repository: FavoritesRepository

    private val testUserId = "user_123"
    private val testMenuItemId = "menu_item_456"
    private val testRestaurantId = "restaurant_789"

    private val sampleFavoriteResponse = FavoriteResponse(
        id = 1,
        userId = testUserId,
        menuItemId = testMenuItemId,
        restaurantId = null,
        createdAt = "2024-01-01"
    )

    private val sampleFavoriteWithRestaurantResponse = FavoriteWithRestaurantResponse(
        id = 1,
        restaurantId = testRestaurantId,
        createdAt = "2024-01-01",
        restaurant = mockk()
    )

    private val sampleFavoriteWithMenuItemResponse = FavoriteWithMenuItemResponse(
        id = 2,
        menuItemId = testMenuItemId,
        createdAt = "2024-01-01",
        menuItem = mockk()
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        repository = FavoritesRepositoryImpl(apiService)
    }

    @Test
    fun `getUserFavorites should return success with favorites list`() = runTest {
        val favorites = listOf(sampleFavoriteResponse)
        val response = Response.success(favorites)
        coEvery { apiService.getUserFavorites("eq.$testUserId") } returns response

        val result = repository.getUserFavorites(testUserId)

        assertTrue("Expected Result.Success", result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(1, data.size)
        assertEquals(sampleFavoriteResponse, data.first())
        coVerify { apiService.getUserFavorites("eq.$testUserId") }
    }

    @Test
    fun `getUserFavorites should return success with empty list when no favorites`() = runTest {
        val response = Response.success(emptyList<FavoriteResponse>())
        coEvery { apiService.getUserFavorites("eq.$testUserId") } returns response

        val result = repository.getUserFavorites(testUserId)

        assertTrue("Expected Result.Success", result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(0, data.size)
        coVerify { apiService.getUserFavorites("eq.$testUserId") }
    }

    @Test
    fun `getUserFavorites should return success with empty list when response body is null`() =
        runTest {
            val response = Response.success<List<FavoriteResponse>>(null)
            coEvery { apiService.getUserFavorites("eq.$testUserId") } returns response

            val result = repository.getUserFavorites(testUserId)

            assertTrue("Expected Result.Success", result is Result.Success)
            val data = (result as Result.Success).data
            assertEquals(0, data.size)
            coVerify { apiService.getUserFavorites("eq.$testUserId") }
        }

    @Test
    fun `getUserFavorites should return error when API call fails`() = runTest {
        val errorResponse =
            Response.error<List<FavoriteResponse>>(404, "Not found".toResponseBody())
        coEvery { apiService.getUserFavorites("eq.$testUserId") } returns errorResponse

        val result = repository.getUserFavorites(testUserId)

        assertTrue("Expected Result.Error", result is Result.Error)
        val message = (result as Result.Error).message
        assertTrue(
            "Error message should contain 'Failed to get favorites'",
            message.contains("Failed to get favorites")
        )
        coVerify { apiService.getUserFavorites("eq.$testUserId") }
    }

    @Test
    fun `getUserFavorites should return error when network exception occurs`() = runTest {
        val networkException = IOException("Network timeout")
        coEvery { apiService.getUserFavorites("eq.$testUserId") } throws networkException

        val result = repository.getUserFavorites(testUserId)

        assertTrue("Expected Result.Error", result is Result.Error)
        val message = (result as Result.Error).message
        assertEquals("Network error: Network timeout", message)
        coVerify { apiService.getUserFavorites("eq.$testUserId") }
    }

    @Test
    fun `addFavorite should return success when adding menu item favorite`() = runTest {
        val request = AddFavoriteRequest(testUserId, testMenuItemId, testRestaurantId)
        val favorites = listOf(sampleFavoriteResponse)
        val response = Response.success(favorites)
        coEvery { apiService.addFavorite(request) } returns response

        val result = repository.addFavorite(testUserId, testMenuItemId, testRestaurantId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(true, (result as Result.Success).data)
        coVerify { apiService.addFavorite(request) }
    }

    @Test
    fun `addFavorite should return success when adding restaurant favorite`() = runTest {
        val request = AddFavoriteRequest(testUserId, null, testRestaurantId)
        val favorites = listOf(sampleFavoriteResponse)
        val response = Response.success(favorites)
        coEvery { apiService.addFavorite(request) } returns response

        val result = repository.addFavorite(testUserId, null, testRestaurantId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(true, (result as Result.Success).data)
        coVerify { apiService.addFavorite(request) }
    }

    @Test
    fun `addFavorite should return error when response body is empty`() = runTest {
        val request = AddFavoriteRequest(testUserId, testMenuItemId, testRestaurantId)
        val response = Response.success(emptyList<FavoriteResponse>())
        coEvery { apiService.addFavorite(request) } returns response

        val result = repository.addFavorite(testUserId, testMenuItemId, testRestaurantId)

        assertTrue("Expected Result.Error", result is Result.Error)
        val message = (result as Result.Error).message
        assertEquals("Failed to add favorite: Empty response", message)
        coVerify { apiService.addFavorite(request) }
    }

    @Test
    fun `addFavorite should return error when response body is null`() = runTest {
        val request = AddFavoriteRequest(testUserId, testMenuItemId, testRestaurantId)
        val response = Response.success<List<FavoriteResponse>>(null)
        coEvery { apiService.addFavorite(request) } returns response

        val result = repository.addFavorite(testUserId, testMenuItemId, testRestaurantId)

        assertTrue("Expected Result.Error", result is Result.Error)
        val message = (result as Result.Error).message
        assertEquals("Failed to add favorite: Empty response", message)
        coVerify { apiService.addFavorite(request) }
    }

    @Test
    fun `addFavorite should return error when API call fails`() = runTest {
        val request = AddFavoriteRequest(testUserId, testMenuItemId, testRestaurantId)
        val errorResponse =
            Response.error<List<FavoriteResponse>>(400, "Bad request".toResponseBody())
        coEvery { apiService.addFavorite(request) } returns errorResponse

        val result = repository.addFavorite(testUserId, testMenuItemId, testRestaurantId)

        assertTrue("Expected Result.Error", result is Result.Error)
        val message = (result as Result.Error).message
        assertTrue(
            "Error message should contain 'Failed to add favorite'",
            message.contains("Failed to add favorite")
        )
        coVerify { apiService.addFavorite(request) }
    }

    @Test
    fun `addFavorite should return error when network exception occurs`() = runTest {
        val request = AddFavoriteRequest(testUserId, testMenuItemId, testRestaurantId)
        val networkException = IOException("Connection failed")
        coEvery { apiService.addFavorite(request) } throws networkException

        val result = repository.addFavorite(testUserId, testMenuItemId, testRestaurantId)

        assertTrue("Expected Result.Error", result is Result.Error)
        val message = (result as Result.Error).message
        assertEquals("Network error: Connection failed", message)
        coVerify { apiService.addFavorite(request) }
    }

    @Test
    fun `removeFavorite should return success when removing menu item favorite`() = runTest {
        val response = Response.success(listOf<FavoriteResponse>())
        coEvery {
            apiService.removeFavorite("eq.$testUserId", "eq.$testMenuItemId", null)
        } returns response

        val result = repository.removeFavorite(testUserId, testMenuItemId, null)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(true, (result as Result.Success).data)
        coVerify { apiService.removeFavorite("eq.$testUserId", "eq.$testMenuItemId", null) }
    }

    @Test
    fun `removeFavorite should return success when removing restaurant favorite`() = runTest {
        val response = Response.success(listOf<FavoriteResponse>())
        coEvery {
            apiService.removeFavorite("eq.$testUserId", null, "eq.$testRestaurantId")
        } returns response

        val result = repository.removeFavorite(testUserId, null, testRestaurantId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(true, (result as Result.Success).data)
        coVerify { apiService.removeFavorite("eq.$testUserId", null, "eq.$testRestaurantId") }
    }

    @Test
    fun `removeFavorite should return error when API call fails`() = runTest {
        val errorResponse =
            Response.error<List<FavoriteResponse>>(404, "Not found".toResponseBody())
        coEvery {
            apiService.removeFavorite("eq.$testUserId", "eq.$testMenuItemId", null)
        } returns errorResponse

        val result = repository.removeFavorite(testUserId, testMenuItemId, null)

        assertTrue("Expected Result.Error", result is Result.Error)
        val message = (result as Result.Error).message
        assertTrue(
            "Error message should contain 'Failed to remove favorite'",
            message.contains("Failed to remove favorite")
        )
        coVerify { apiService.removeFavorite("eq.$testUserId", "eq.$testMenuItemId", null) }
    }

    @Test
    fun `removeFavorite should return error when network exception occurs`() = runTest {
        val networkException = IOException("Network error")
        coEvery {
            apiService.removeFavorite("eq.$testUserId", "eq.$testMenuItemId", null)
        } throws networkException

        val result = repository.removeFavorite(testUserId, testMenuItemId, null)

        assertTrue("Expected Result.Error", result is Result.Error)
        val message = (result as Result.Error).message
        assertEquals("Network error: Network error", message)
        coVerify { apiService.removeFavorite("eq.$testUserId", "eq.$testMenuItemId", null) }
    }


    @Test
    fun `isMenuItemFavorite should return true when menu item is favorite`() = runTest {
        val favorites = listOf(sampleFavoriteResponse)
        val response = Response.success(favorites)
        coEvery {
            apiService.getUserFavorites("eq.$testUserId", "menu_item_id")
        } returns response

        val result = repository.isMenuItemFavorite(testUserId, testMenuItemId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(true, (result as Result.Success).data)
        coVerify { apiService.getUserFavorites("eq.$testUserId", "menu_item_id") }
    }

    @Test
    fun `isMenuItemFavorite should return false when menu item is not favorite`() = runTest {
        val favorites = listOf(sampleFavoriteResponse.copy(menuItemId = "different_item"))
        val response = Response.success(favorites)
        coEvery {
            apiService.getUserFavorites("eq.$testUserId", "menu_item_id")
        } returns response

        val result = repository.isMenuItemFavorite(testUserId, testMenuItemId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(false, (result as Result.Success).data)
        coVerify { apiService.getUserFavorites("eq.$testUserId", "menu_item_id") }
    }

    @Test
    fun `isMenuItemFavorite should return false when no favorites exist`() = runTest {
        val response = Response.success(emptyList<FavoriteResponse>())
        coEvery {
            apiService.getUserFavorites("eq.$testUserId", "menu_item_id")
        } returns response

        val result = repository.isMenuItemFavorite(testUserId, testMenuItemId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(false, (result as Result.Success).data)
        coVerify { apiService.getUserFavorites("eq.$testUserId", "menu_item_id") }
    }

    @Test
    fun `isMenuItemFavorite should return false when API call fails`() = runTest {
        val errorResponse =
            Response.error<List<FavoriteResponse>>(500, "Server error".toResponseBody())
        coEvery {
            apiService.getUserFavorites("eq.$testUserId", "menu_item_id")
        } returns errorResponse

        val result = repository.isMenuItemFavorite(testUserId, testMenuItemId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(false, (result as Result.Success).data)
        coVerify { apiService.getUserFavorites("eq.$testUserId", "menu_item_id") }
    }

    @Test
    fun `isMenuItemFavorite should return false when network exception occurs`() = runTest {
        val networkException = IOException("Network timeout")
        coEvery {
            apiService.getUserFavorites("eq.$testUserId", "menu_item_id")
        } throws networkException

        val result = repository.isMenuItemFavorite(testUserId, testMenuItemId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(false, (result as Result.Success).data)
        coVerify { apiService.getUserFavorites("eq.$testUserId", "menu_item_id") }
    }

    @Test
    fun `isMenuItemFavorite should return false when response body is null`() = runTest {
        val response = Response.success<List<FavoriteResponse>>(null)
        coEvery {
            apiService.getUserFavorites("eq.$testUserId", "menu_item_id")
        } returns response

        val result = repository.isMenuItemFavorite(testUserId, testMenuItemId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(false, (result as Result.Success).data)
        coVerify { apiService.getUserFavorites("eq.$testUserId", "menu_item_id") }
    }

    @Test
    fun `isRestaurantFavorite should return true when restaurant is favorite`() = runTest {
        val favorites =
            listOf(sampleFavoriteResponse.copy(restaurantId = testRestaurantId, menuItemId = null))
        val response = Response.success(favorites)
        coEvery {
            apiService.getUserFavorites("eq.$testUserId", "restaurant_id")
        } returns response

        val result = repository.isRestaurantFavorite(testUserId, testRestaurantId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(true, (result as Result.Success).data)
        coVerify { apiService.getUserFavorites("eq.$testUserId", "restaurant_id") }
    }

    @Test
    fun `isRestaurantFavorite should return false when restaurant is not favorite`() = runTest {
        val favorites = listOf(sampleFavoriteResponse.copy(restaurantId = "different_restaurant"))
        val response = Response.success(favorites)
        coEvery {
            apiService.getUserFavorites("eq.$testUserId", "restaurant_id")
        } returns response

        val result = repository.isRestaurantFavorite(testUserId, testRestaurantId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(false, (result as Result.Success).data)
        coVerify { apiService.getUserFavorites("eq.$testUserId", "restaurant_id") }
    }

    @Test
    fun `isRestaurantFavorite should return false when no favorites exist`() = runTest {
        val response = Response.success(emptyList<FavoriteResponse>())
        coEvery {
            apiService.getUserFavorites("eq.$testUserId", "restaurant_id")
        } returns response

        val result = repository.isRestaurantFavorite(testUserId, testRestaurantId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(false, (result as Result.Success).data)
        coVerify { apiService.getUserFavorites("eq.$testUserId", "restaurant_id") }
    }

    @Test
    fun `isRestaurantFavorite should return false when API call fails`() = runTest {
        val errorResponse =
            Response.error<List<FavoriteResponse>>(500, "Server error".toResponseBody())
        coEvery {
            apiService.getUserFavorites("eq.$testUserId", "restaurant_id")
        } returns errorResponse

        val result = repository.isRestaurantFavorite(testUserId, testRestaurantId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(false, (result as Result.Success).data)
        coVerify { apiService.getUserFavorites("eq.$testUserId", "restaurant_id") }
    }

    @Test
    fun `isRestaurantFavorite should return false when network exception occurs`() = runTest {
        val networkException = IOException("Connection timeout")
        coEvery {
            apiService.getUserFavorites("eq.$testUserId", "restaurant_id")
        } throws networkException

        val result = repository.isRestaurantFavorite(testUserId, testRestaurantId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(false, (result as Result.Success).data)
        coVerify { apiService.getUserFavorites("eq.$testUserId", "restaurant_id") }
    }

    @Test
    fun `getFavoriteRestaurantsWithDetails should return success with restaurants list`() =
        runTest {
            val restaurants = listOf(sampleFavoriteWithRestaurantResponse)
            val response = Response.success(restaurants)
            coEvery { apiService.getFavoriteRestaurantsWithDetails("eq.$testUserId") } returns response

            val result = repository.getFavoriteRestaurantsWithDetails(testUserId)

            assertTrue("Expected Result.Success", result is Result.Success)
            val data = (result as Result.Success).data
            assertEquals(1, data.size)
            assertEquals(sampleFavoriteWithRestaurantResponse, data.first())
            coVerify { apiService.getFavoriteRestaurantsWithDetails("eq.$testUserId") }
        }

    @Test
    fun `getFavoriteRestaurantsWithDetails should return success with empty list when no restaurants`() =
        runTest {
            val response = Response.success(emptyList<FavoriteWithRestaurantResponse>())
            coEvery { apiService.getFavoriteRestaurantsWithDetails("eq.$testUserId") } returns response

            val result = repository.getFavoriteRestaurantsWithDetails(testUserId)

            assertTrue("Expected Result.Success", result is Result.Success)
            val data = (result as Result.Success).data
            assertEquals(0, data.size)
            coVerify { apiService.getFavoriteRestaurantsWithDetails("eq.$testUserId") }
        }

    @Test
    fun `getFavoriteRestaurantsWithDetails should return success with empty list when response body is null`() =
        runTest {
            val response = Response.success<List<FavoriteWithRestaurantResponse>>(null)
            coEvery { apiService.getFavoriteRestaurantsWithDetails("eq.$testUserId") } returns response

            val result = repository.getFavoriteRestaurantsWithDetails(testUserId)

            assertTrue("Expected Result.Success", result is Result.Success)
            val data = (result as Result.Success).data
            assertEquals(0, data.size)
            coVerify { apiService.getFavoriteRestaurantsWithDetails("eq.$testUserId") }
        }

    @Test
    fun `getFavoriteRestaurantsWithDetails should return error when API call fails`() = runTest {
        val errorResponse =
            Response.error<List<FavoriteWithRestaurantResponse>>(404, "Not found".toResponseBody())
        coEvery { apiService.getFavoriteRestaurantsWithDetails("eq.$testUserId") } returns errorResponse

        val result = repository.getFavoriteRestaurantsWithDetails(testUserId)

        assertTrue("Expected Result.Error", result is Result.Error)
        val message = (result as Result.Error).message
        assertTrue(
            "Error message should contain 'Failed to get favorite restaurants'",
            message.contains("Failed to get favorite restaurants")
        )
        coVerify { apiService.getFavoriteRestaurantsWithDetails("eq.$testUserId") }
    }

    @Test
    fun `getFavoriteRestaurantsWithDetails should return error when network exception occurs`() =
        runTest {
            val networkException = IOException("Network error")
            coEvery { apiService.getFavoriteRestaurantsWithDetails("eq.$testUserId") } throws networkException

            val result = repository.getFavoriteRestaurantsWithDetails(testUserId)

            assertTrue("Expected Result.Error", result is Result.Error)
            val message = (result as Result.Error).message
            assertEquals("Network error: Network error", message)
            coVerify { apiService.getFavoriteRestaurantsWithDetails("eq.$testUserId") }
        }

    @Test
    fun `getFavoriteMenuItemsWithDetails should return success with menu items list`() = runTest {
        val menuItems = listOf(sampleFavoriteWithMenuItemResponse)
        val response = Response.success(menuItems)
        coEvery { apiService.getFavoriteMenuItemsWithDetails("eq.$testUserId") } returns response

        val result = repository.getFavoriteMenuItemsWithDetails(testUserId)

        assertTrue("Expected Result.Success", result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(1, data.size)
        assertEquals(sampleFavoriteWithMenuItemResponse, data.first())
        coVerify { apiService.getFavoriteMenuItemsWithDetails("eq.$testUserId") }
    }

    @Test
    fun `getFavoriteMenuItemsWithDetails should return success with empty list when no menu items`() =
        runTest {
            val response = Response.success(emptyList<FavoriteWithMenuItemResponse>())
            coEvery { apiService.getFavoriteMenuItemsWithDetails("eq.$testUserId") } returns response

            val result = repository.getFavoriteMenuItemsWithDetails(testUserId)

            assertTrue("Expected Result.Success", result is Result.Success)
            val data = (result as Result.Success).data
            assertEquals(0, data.size)
            coVerify { apiService.getFavoriteMenuItemsWithDetails("eq.$testUserId") }
        }

    @Test
    fun `getFavoriteMenuItemsWithDetails should return success with empty list when response body is null`() =
        runTest {
            val response = Response.success<List<FavoriteWithMenuItemResponse>>(null)
            coEvery { apiService.getFavoriteMenuItemsWithDetails("eq.$testUserId") } returns response

            val result = repository.getFavoriteMenuItemsWithDetails(testUserId)

            assertTrue("Expected Result.Success", result is Result.Success)
            val data = (result as Result.Success).data
            assertEquals(0, data.size)
            coVerify { apiService.getFavoriteMenuItemsWithDetails("eq.$testUserId") }
        }

    @Test
    fun `getFavoriteMenuItemsWithDetails should return error when API call fails`() = runTest {
        val errorResponse =
            Response.error<List<FavoriteWithMenuItemResponse>>(500, "Server error".toResponseBody())
        coEvery { apiService.getFavoriteMenuItemsWithDetails("eq.$testUserId") } returns errorResponse

        val result = repository.getFavoriteMenuItemsWithDetails(testUserId)

        assertTrue("Expected Result.Error", result is Result.Error)
        val message = (result as Result.Error).message
        assertTrue(
            "Error message should contain 'Failed to get favorite menu items'",
            message.contains("Failed to get favorite menu items")
        )
        coVerify { apiService.getFavoriteMenuItemsWithDetails("eq.$testUserId") }
    }

    @Test
    fun `getFavoriteMenuItemsWithDetails should return error when network exception occurs`() =
        runTest {
            val networkException = IOException("Connection failed")
            coEvery { apiService.getFavoriteMenuItemsWithDetails("eq.$testUserId") } throws networkException

            val result = repository.getFavoriteMenuItemsWithDetails(testUserId)

            assertTrue("Expected Result.Error", result is Result.Error)
            val message = (result as Result.Error).message
            assertEquals("Network error: Connection failed", message)
            coVerify { apiService.getFavoriteMenuItemsWithDetails("eq.$testUserId") }
        }

    @Test
    fun `addFavorite should handle both menuItemId and restaurantId null parameters`() = runTest {
        val request = AddFavoriteRequest(testUserId, null, null)
        val favorites = listOf(sampleFavoriteResponse)
        val response = Response.success(favorites)
        coEvery { apiService.addFavorite(request) } returns response

        val result = repository.addFavorite(testUserId, null, null)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(true, (result as Result.Success).data)
        coVerify { apiService.addFavorite(request) }
    }

    @Test
    fun `removeFavorite should handle both menuItemId and restaurantId null parameters`() =
        runTest {
            val response = Response.success(listOf<FavoriteResponse>())
            coEvery {
                apiService.removeFavorite("eq.$testUserId", null, null)
            } returns response

            val result = repository.removeFavorite(testUserId, null, null)

            assertTrue("Expected Result.Success", result is Result.Success)
            assertEquals(true, (result as Result.Success).data)
            coVerify { apiService.removeFavorite("eq.$testUserId", null, null) }
        }

    @Test
    fun `getUserFavorites should handle multiple favorites correctly`() = runTest {
        val favorites = listOf(
            sampleFavoriteResponse,
            sampleFavoriteResponse.copy(id = 2, menuItemId = "menu_item_2"),
            sampleFavoriteResponse.copy(id = 3, restaurantId = testRestaurantId, menuItemId = null)
        )
        val response = Response.success(favorites)
        coEvery { apiService.getUserFavorites("eq.$testUserId") } returns response

        val result = repository.getUserFavorites(testUserId)

        assertTrue("Expected Result.Success", result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(3, data.size)
        assertEquals(1, data[0].id)
        assertEquals(2, data[1].id)
        assertEquals(3, data[2].id)
        coVerify { apiService.getUserFavorites("eq.$testUserId") }
    }

    @Test
    fun `isMenuItemFavorite should handle multiple menu items and find correct one`() = runTest {
        val favorites = listOf(
            sampleFavoriteResponse.copy(menuItemId = "menu_item_1"),
            sampleFavoriteResponse.copy(menuItemId = testMenuItemId),
            sampleFavoriteResponse.copy(menuItemId = "menu_item_3")
        )
        val response = Response.success(favorites)
        coEvery {
            apiService.getUserFavorites("eq.$testUserId", "menu_item_id")
        } returns response

        val result = repository.isMenuItemFavorite(testUserId, testMenuItemId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(true, (result as Result.Success).data)
        coVerify { apiService.getUserFavorites("eq.$testUserId", "menu_item_id") }
    }

    @Test
    fun `isRestaurantFavorite should handle multiple restaurants and find correct one`() = runTest {
        val favorites = listOf(
            sampleFavoriteResponse.copy(restaurantId = "restaurant_1"),
            sampleFavoriteResponse.copy(restaurantId = testRestaurantId),
            sampleFavoriteResponse.copy(restaurantId = "restaurant_3")
        )
        val response = Response.success(favorites)
        coEvery {
            apiService.getUserFavorites("eq.$testUserId", "restaurant_id")
        } returns response

        val result = repository.isRestaurantFavorite(testUserId, testRestaurantId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(true, (result as Result.Success).data)
        coVerify { apiService.getUserFavorites("eq.$testUserId", "restaurant_id") }
    }

    @Test
    fun `repository should handle API service parameter formatting correctly for removeFavorite`() =
        runTest {
            // Given
            val response = Response.success(listOf<FavoriteResponse>())
            coEvery {
                apiService.removeFavorite(
                    "eq.$testUserId",
                    "eq.$testMenuItemId",
                    "eq.$testRestaurantId"
                )
            } returns response

            val result = repository.removeFavorite(testUserId, testMenuItemId, testRestaurantId)

            assertTrue("Expected Result.Success", result is Result.Success)
            assertEquals(true, (result as Result.Success).data)
            coVerify {
                apiService.removeFavorite(
                    "eq.$testUserId",
                    "eq.$testMenuItemId",
                    "eq.$testRestaurantId"
                )
            }
        }

    @Test
    fun `addFavorite should verify request object creation with all parameters`() = runTest {
        val expectedRequest = AddFavoriteRequest(
            userId = testUserId,
            menuItemId = testMenuItemId,
            restaurantId = testRestaurantId
        )
        val favorites = listOf(sampleFavoriteResponse)
        val response = Response.success(favorites)
        coEvery { apiService.addFavorite(expectedRequest) } returns response

        val result = repository.addFavorite(testUserId, testMenuItemId, testRestaurantId)

        assertTrue("Expected Result.Success", result is Result.Success)
        assertEquals(true, (result as Result.Success).data)
        coVerify { apiService.addFavorite(expectedRequest) }
    }

    @Test
    fun `error responses should include error body content when available`() = runTest {
        val errorBody = "Detailed error message from server"
        val errorResponse = Response.error<List<FavoriteResponse>>(400, errorBody.toResponseBody())
        coEvery {
            apiService.addFavorite(any())
        } returns errorResponse

        val result = repository.addFavorite(testUserId, testMenuItemId, testRestaurantId)

        assertTrue("Expected Result.Error", result is Result.Error)
        val message = (result as Result.Error).message
        assertTrue(
            "Error message should contain error body content",
            message.contains(errorBody)
        )
        assertTrue(
            "Error message should contain 'Failed to add favorite'",
            message.contains("Failed to add favorite")
        )
    }

    @Test
    fun `all repository methods should handle generic RuntimeException correctly`() = runTest {
        val runtimeException = RuntimeException("Unexpected error")
        coEvery { apiService.getUserFavorites(any()) } throws runtimeException

        val result = repository.getUserFavorites(testUserId)

        assertTrue("Expected Result.Error", result is Result.Error)
        val message = (result as Result.Error).message
        assertEquals("Network error: Unexpected error", message)
    }

    @Test
    fun `getFavoriteRestaurantsWithDetails should handle multiple restaurants correctly`() =
        runTest {
            val restaurants = listOf(
                sampleFavoriteWithRestaurantResponse,
                sampleFavoriteWithRestaurantResponse.copy(id = 2, restaurantId = "restaurant_2"),
                sampleFavoriteWithRestaurantResponse.copy(id = 3, restaurantId = "restaurant_3")
            )
            val response = Response.success(restaurants)
            coEvery { apiService.getFavoriteRestaurantsWithDetails("eq.$testUserId") } returns response

            val result = repository.getFavoriteRestaurantsWithDetails(testUserId)

            assertTrue("Expected Result.Success", result is Result.Success)
            val data = (result as Result.Success).data
            assertEquals(3, data.size)
            assertEquals(1, data[0].id)
            assertEquals(2, data[1].id)
            assertEquals(3, data[2].id)
            coVerify { apiService.getFavoriteRestaurantsWithDetails("eq.$testUserId") }
        }

    @Test
    fun `getFavoriteMenuItemsWithDetails should handle multiple menu items correctly`() = runTest {
        val menuItems = listOf(
            sampleFavoriteWithMenuItemResponse,
            sampleFavoriteWithMenuItemResponse.copy(id = 3, menuItemId = "menu_item_2"),
            sampleFavoriteWithMenuItemResponse.copy(id = 4, menuItemId = "menu_item_3")
        )
        val response = Response.success(menuItems)
        coEvery { apiService.getFavoriteMenuItemsWithDetails("eq.$testUserId") } returns response

        val result = repository.getFavoriteMenuItemsWithDetails(testUserId)

        assertTrue("Expected Result.Success", result is Result.Success)
        val data = (result as Result.Success).data
        assertEquals(3, data.size)
        assertEquals(2, data[0].id)
        assertEquals(3, data[1].id)
        assertEquals(4, data[2].id)
        coVerify { apiService.getFavoriteMenuItemsWithDetails("eq.$testUserId") }
    }
}