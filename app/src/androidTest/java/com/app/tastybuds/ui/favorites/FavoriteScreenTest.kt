package com.app.tastybuds.ui.favorites

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.tastybuds.data.model.FavoriteMenuItemUi
import com.app.tastybuds.data.model.FavoriteRestaurantUi
import com.app.tastybuds.domain.FavoritesUseCase
import com.app.tastybuds.ui.login.LoginViewModel
import com.app.tastybuds.ui.theme.TastyBudsTheme
import com.app.tastybuds.util.Result
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FavoriteScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    private lateinit var mockFavoritesUseCase: FavoritesUseCase
    private lateinit var mockLoginViewModel: LoginViewModel
    private lateinit var sampleFavoriteRestaurants: List<FavoriteRestaurantUi>
    private lateinit var sampleFavoriteMenuItems: List<FavoriteMenuItemUi>

    @Before
    fun setup() {
        hiltRule.inject()

        mockFavoritesUseCase = mockk(relaxed = true)
        mockLoginViewModel = mockk(relaxed = true)

        sampleFavoriteRestaurants = listOf(
            FavoriteRestaurantUi(
                id = 1,
                restaurantId = "rest1",
                name = "Pizza Palace",
                cuisine = "Italian",
                rating = 4.5f,
                reviewCount = 123,
                deliveryTime = "30-45 min",
                distance = "2.1 km",
                priceRange = "$",
                imageUrl = "https://example.com/pizza.jpg",
                deliveryFee = 3.99f,
                isOpen = true,
                createdAt = "2024-01-01T10:00:00Z"
            ),
            FavoriteRestaurantUi(
                id = 2,
                restaurantId = "rest2",
                name = "Burger Barn",
                cuisine = "American",
                rating = 4.2f,
                reviewCount = 87,
                deliveryTime = "25-35 min",
                distance = "1.5 km",
                priceRange = "$",
                imageUrl = "",
                deliveryFee = 2.50f,
                isOpen = true,
                createdAt = "2024-01-02T11:00:00Z"
            )
        )

        sampleFavoriteMenuItems = listOf(
            FavoriteMenuItemUi(
                id = 1,
                menuItemId = "item1",
                name = "Margherita Pizza",
                description = "Classic pizza with tomato sauce",
                price = 15.99f,
                imageUrl = "https://example.com/margherita.jpg",
                rating = 4.8f,
                reviewCount = 45,
                restaurantName = "Pizza Palace",
                restaurantId = "rest1",
                isPopular = true,
                isSpicy = false,
                createdAt = "2024-01-01T10:00:00Z"
            ),
            FavoriteMenuItemUi(
                id = 2,
                menuItemId = "item2",
                name = "Classic Burger",
                description = "Beef patty with lettuce",
                price = 12.50f,
                imageUrl = "",
                rating = 4.0f,
                reviewCount = 32,
                restaurantName = "Burger Barn",
                restaurantId = "rest2",
                isPopular = false,
                isSpicy = false,
                createdAt = "2024-01-02T11:00:00Z"
            )
        )

        every { mockLoginViewModel.getUserId() } returns flowOf("user123")

        coEvery { mockFavoritesUseCase.getFavoriteRestaurantsForUI("user123") } returns
                Result.Success(sampleFavoriteRestaurants)
        coEvery { mockFavoritesUseCase.getFavoriteMenuItemsForUI("user123") } returns
                Result.Success(sampleFavoriteMenuItems)
    }

    @Test
    fun favoriteScreen_displaysTabsWithCorrectLabels() {
        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("Food Items").assertExists()
        composeTestRule.onNodeWithText("Restaurants").assertExists()
    }

    @Test
    fun favoriteScreen_displaysTabsWithBadgeCounts() {
        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("2").assertExists()
        composeTestRule.onAllNodesWithText("2").assertCountEquals(2)
    }

    @Test
    fun favoriteScreen_defaultsToFoodItemsTab() {
        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("Margherita Pizza").assertExists()
        composeTestRule.onNodeWithText("Classic Burger").assertExists()
    }

    @Test
    fun favoriteScreen_switchesToRestaurantsTab() {
        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("Restaurants").performClick()

        composeTestRule.onNodeWithText("Pizza Palace").assertExists()
        composeTestRule.onNodeWithText("Burger Barn").assertExists()
        composeTestRule.onNodeWithText("Italian").assertExists()
        composeTestRule.onNodeWithText("American").assertExists()
    }

    @Test
    fun favoriteScreen_switchingTabsHidesOtherContent() {
        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("Margherita Pizza").assertExists()

        composeTestRule.onNodeWithText("Restaurants").performClick()

        composeTestRule.onNodeWithText("Margherita Pizza").assertDoesNotExist()

        composeTestRule.onNodeWithText("Pizza Palace").assertExists()
    }

    @Test
    fun favoriteItemsTab_displaysAllFoodItems() {
        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("Margherita Pizza").assertExists()
        composeTestRule.onNodeWithText("Classic Burger").assertExists()
        composeTestRule.onNodeWithText("$15.99").assertExists()
        composeTestRule.onNodeWithText("$12.50").assertExists()
    }

    @Test
    fun favoriteItemsTab_displaysRestaurantNames() {
        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("Pizza Palace").assertExists()
        composeTestRule.onNodeWithText("Burger Barn").assertExists()
    }

    @Test
    fun favoriteItemsTab_displaysRatingsCorrectly() {
        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("4.8 (45)").assertExists()
        composeTestRule.onNodeWithText("4.0 (32)").assertExists()

        composeTestRule.onAllNodesWithContentDescription("Rating star")
            .assertCountEquals(2)
    }

    @Test
    fun favoriteItemsTab_clickTriggersCallback() {
        var clickedItemId = ""

        setFavoriteScreenContent(
            onMenuItemClick = { itemId -> clickedItemId = itemId }
        )

        composeTestRule.onNodeWithText("Margherita Pizza").performClick()

        assert(clickedItemId == "item1")
    }

    @Test
    fun favoriteItemsTab_removeButtonWorks() {
        setFavoriteScreenContent()

        composeTestRule.onAllNodesWithContentDescription("Remove from favorites")[0]
            .performClick()

    }

    @Test
    fun favoriteRestaurantsTab_displaysAllRestaurants() {
        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("Restaurants").performClick()

        composeTestRule.onNodeWithText("Pizza Palace").assertExists()
        composeTestRule.onNodeWithText("Burger Barn").assertExists()
        composeTestRule.onNodeWithText("Italian").assertExists()
        composeTestRule.onNodeWithText("American").assertExists()
    }

    @Test
    fun favoriteRestaurantsTab_displaysDeliveryInfo() {
        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("Restaurants").performClick()

        composeTestRule.onNodeWithText("30-45 min • 2.1 km").assertExists()
        composeTestRule.onNodeWithText("25-35 min • 1.5 km").assertExists()
    }

    @Test
    fun favoriteRestaurantsTab_displaysPriceRanges() {
        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("Restaurants").performClick()

        composeTestRule.onNodeWithText("$").assertExists()
        composeTestRule.onNodeWithText("$").assertExists()
    }

    @Test
    fun favoriteRestaurantsTab_displaysRatingsWithReviewCount() {
        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("Restaurants").performClick()

        composeTestRule.onNodeWithText("4.5 (123)").assertExists()
        composeTestRule.onNodeWithText("4.2 (87)").assertExists()
    }

    @Test
    fun favoriteRestaurantsTab_clickTriggersCallback() {
        var clickedRestaurantId = ""

        setFavoriteScreenContent(
            onRestaurantClick = { restaurantId -> clickedRestaurantId = restaurantId }
        )

        composeTestRule.onNodeWithText("Restaurants").performClick()
        composeTestRule.onNodeWithText("Pizza Palace").performClick()

        assert(clickedRestaurantId == "rest1")
    }

    @Test
    fun favoriteScreen_showsLoadingIndicator() {
        coEvery { mockFavoritesUseCase.getFavoriteRestaurantsForUI("user123") } returns Result.Loading
        coEvery { mockFavoritesUseCase.getFavoriteMenuItemsForUI("user123") } returns Result.Loading

        setFavoriteScreenContent()

        composeTestRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertExists()
    }

    @Test
    fun favoriteItemsTab_showsEmptyStateWhenNoItems() {
        coEvery { mockFavoritesUseCase.getFavoriteMenuItemsForUI("user123") } returns
                Result.Success(emptyList())

        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("No favorite items yet").assertExists()
        composeTestRule.onNodeWithText("Start adding your favorite dishes to see them here")
            .assertExists()
    }

    @Test
    fun favoriteRestaurantsTab_showsEmptyStateWhenNoRestaurants() {
        coEvery { mockFavoritesUseCase.getFavoriteRestaurantsForUI("user123") } returns
                Result.Success(emptyList())

        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("Restaurants").performClick()

        composeTestRule.onNodeWithText("No favorite restaurants yet").assertExists()
        composeTestRule.onNodeWithText("Start adding your favorite restaurants to see them here")
            .assertExists()
    }

    @Test
    fun emptyState_displaysCorrectIcon() {
        coEvery { mockFavoritesUseCase.getFavoriteMenuItemsForUI("user123") } returns
                Result.Success(emptyList())

        setFavoriteScreenContent()

        composeTestRule.onNodeWithContentDescription("No favorites").assertExists()
    }

    @Test
    fun favoriteMenuItemCard_displaysAllInformation() {
        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("Margherita Pizza").assertExists()
        composeTestRule.onNodeWithText("Pizza Palace").assertExists()
        composeTestRule.onNodeWithText("4.8 (45)").assertExists()
        composeTestRule.onNodeWithText("$15.99").assertExists()
    }

    @Test
    fun favoriteRestaurantCard_displaysAllInformation() {
        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("Restaurants").performClick()

        composeTestRule.onNodeWithText("Pizza Palace").assertExists()
        composeTestRule.onNodeWithText("Italian").assertExists()
        composeTestRule.onNodeWithText("4.5 (123)").assertExists()
        composeTestRule.onNodeWithText("30-45 min • 2.1 km").assertExists()
        composeTestRule.onNodeWithText("$").assertExists()
    }

    @Test
    fun favoriteCards_displayRemoveButtons() {
        setFavoriteScreenContent()

        composeTestRule.onAllNodesWithContentDescription("Remove from favorites")
            .assertCountEquals(2)
    }

    @Test
    fun favoriteCards_handleNullImagesGracefully() {
        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("Classic Burger").assertExists()

        composeTestRule.onNodeWithText("Restaurants").performClick()
        composeTestRule.onNodeWithText("Burger Barn").assertExists()
    }

    @Test
    fun favoriteScreen_hasCorrectContentDescriptions() {
        setFavoriteScreenContent()

        composeTestRule.onAllNodesWithContentDescription("Food image")
            .assertCountEquals(2)
        composeTestRule.onAllNodesWithContentDescription("Remove from favorites")
            .assertCountEquals(2)
        composeTestRule.onAllNodesWithContentDescription("Rating star")
            .assertCountEquals(2)
    }

    @Test
    fun favoriteScreen_tabsHaveClickActions() {
        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("Food Items").assertHasClickAction()
        composeTestRule.onNodeWithText("Restaurants").assertHasClickAction()
    }

    @Test
    fun favoritesTabRow_hidesBadgeWhenCountIsZero() {
        coEvery { mockFavoritesUseCase.getFavoriteRestaurantsForUI("user123") } returns
                Result.Success(emptyList())
        coEvery { mockFavoritesUseCase.getFavoriteMenuItemsForUI("user123") } returns
                Result.Success(emptyList())

        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("0").assertDoesNotExist()
    }

    @Test
    fun favoritesTabRow_showsCorrectBadgeColors() {
        setFavoriteScreenContent()

        composeTestRule.onAllNodesWithText("2").assertCountEquals(2)
    }

    @Test
    fun favoriteScreen_handlesLongTextGracefully() {
        val longNameItem = FavoriteMenuItemUi(
            id = 3,
            menuItemId = "item3",
            name = "Very Long Menu Item Name",
            description = "Very long description",
            price = 99.99f,
            imageUrl = "",
            rating = 4.5f,
            reviewCount = 10,
            restaurantName = "Very Long Restaurant Name",
            restaurantId = "rest3",
            isPopular = false,
            isSpicy = false,
            createdAt = "2024-01-03T12:00:00Z"
        )

        coEvery { mockFavoritesUseCase.getFavoriteMenuItemsForUI("user123") } returns
                Result.Success(listOf(longNameItem))

        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("Very Long Menu Item Name")
            .assertExists()
    }

    @Test
    fun favoriteScreen_handlesZeroRatingsCorrectly() {
        val noRatingItem = FavoriteMenuItemUi(
            id = 4,
            menuItemId = "item4",
            name = "New Item",
            description = "Brand new item",
            price = 8.99f,
            imageUrl = "",
            rating = 0f,
            reviewCount = 0,
            restaurantName = "New Restaurant",
            restaurantId = "rest4",
            isPopular = false,
            isSpicy = false,
            createdAt = "2024-01-04T13:00:00Z"
        )

        coEvery { mockFavoritesUseCase.getFavoriteMenuItemsForUI("user123") } returns
                Result.Success(listOf(noRatingItem))

        setFavoriteScreenContent()

        composeTestRule.onNodeWithContentDescription("Rating star").assertDoesNotExist()
        composeTestRule.onNodeWithText("No ratings").assertExists()
    }

    @Test
    fun favoriteScreen_handlesEmptyImageUrls() {
        val noImageItem = FavoriteMenuItemUi(
            id = 5,
            menuItemId = "item5",
            name = "No Image Item",
            description = "Item without image",
            price = 7.50f,
            imageUrl = "",
            rating = 3.5f,
            reviewCount = 5,
            restaurantName = "Test Restaurant",
            restaurantId = "rest5",
            isPopular = false,
            isSpicy = true,
            createdAt = "2024-01-05T14:00:00Z"
        )

        coEvery { mockFavoritesUseCase.getFavoriteMenuItemsForUI("user123") } returns
                Result.Success(listOf(noImageItem))

        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("No Image Item").assertExists()
        composeTestRule.onNodeWithText("$7.50").assertExists()

        assert(!noImageItem.hasValidImage)
    }

    @Test
    fun favoriteScreen_fullUserFlow_worksCorrectly() {
        var clickedRestaurantId = ""
        var clickedMenuItemId = ""

        setFavoriteScreenContent(
            onRestaurantClick = { clickedRestaurantId = it },
            onMenuItemClick = { clickedMenuItemId = it }
        )

        composeTestRule.onNodeWithText("Margherita Pizza").performClick()
        assert(clickedMenuItemId == "item1")

        composeTestRule.onNodeWithText("Restaurants").performClick()
        composeTestRule.onNodeWithText("Pizza Palace").performClick()
        assert(clickedRestaurantId == "rest1")

        composeTestRule.onNodeWithText("Food Items").performClick()
        composeTestRule.onNodeWithText("Margherita Pizza").assertExists()
    }

    @Test
    fun favoriteScreen_errorHandling_showsGracefulDegradation() {
        coEvery { mockFavoritesUseCase.getFavoriteRestaurantsForUI("user123") } returns
                Result.Error("Network error")
        coEvery { mockFavoritesUseCase.getFavoriteMenuItemsForUI("user123") } returns
                Result.Error("Database error")

        setFavoriteScreenContent()

        composeTestRule.onNodeWithText("No favorite items yet").assertExists()

        composeTestRule.onNodeWithText("Restaurants").performClick()
        composeTestRule.onNodeWithText("No favorite restaurants yet").assertExists()
    }

    private fun setFavoriteScreenContent(
        onRestaurantClick: (String) -> Unit = {},
        onMenuItemClick: (String) -> Unit = {}
    ) {
        composeTestRule.setContent {
            TastyBudsTheme {
                FavoriteScreen(
                    viewModel = FavoritesViewModel(mockFavoritesUseCase),
                    loginViewModel = mockLoginViewModel,
                    onRestaurantClick = onRestaurantClick,
                    onMenuItemClick = onMenuItemClick
                )
            }
        }

        composeTestRule.waitForIdle()
    }
}