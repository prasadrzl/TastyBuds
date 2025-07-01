package com.app.tastybuds.ui.favorites

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.app.tastybuds.data.model.FavoriteMenuItemUi
import com.app.tastybuds.data.model.FavoriteRestaurantUi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleRestaurants = listOf(
        FavoriteRestaurantUi(
            id = 1,
            restaurantId = "rest1",
            name = "restaurant_name_1",
            cuisine = "cuisine_1",
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
            name = "restaurant_name_1",
            cuisine = "cuisine_1",
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

    private val sampleMenuItems = listOf(
        FavoriteMenuItemUi(
            id = 1,
            menuItemId = "item1",
            name = "menu_item_name_1",
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
            name = "menu_item_name_2",
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

    @Test
    fun favoriteContent_showsLoadingStateInMenuItemsTab() {
        composeTestRule.setContent {
            FavoriteContent(
                favoriteMenuItems = emptyList(),
                favoriteRestaurants = emptyList(),
                isLoading = true,
                onRestaurantClick = {},
                onMenuItemClick = {},
                onRemoveFavorite = {}
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun favoriteContent_showsLoadingStateInRestaurantsTab() {
        composeTestRule.setContent {
            FavoriteContent(
                favoriteMenuItems = emptyList(),
                favoriteRestaurants = emptyList(),
                isLoading = true,
                onRestaurantClick = {},
                onMenuItemClick = {},
                onRemoveFavorite = {}
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("restaurants_tab").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()
    }

    @Test
    fun favoriteContent_showsEmptyMenuItemsState() {
        composeTestRule.setContent {
            FavoriteContent(
                favoriteMenuItems = emptyList(),
                favoriteRestaurants = sampleRestaurants,
                isLoading = false,
                onRestaurantClick = {},
                onMenuItemClick = {},
                onRemoveFavorite = {}
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("empty_menu_items_state").assertIsDisplayed()
        composeTestRule.onNodeWithTag("empty_state_title").assertIsDisplayed()
        composeTestRule.onNodeWithTag("empty_state_description").assertIsDisplayed()
    }

    @Test
    fun favoriteContent_showsEmptyRestaurantsState() {
        composeTestRule.setContent {
            FavoriteContent(
                favoriteMenuItems = sampleMenuItems,
                favoriteRestaurants = emptyList(),
                isLoading = false,
                onRestaurantClick = {},
                onMenuItemClick = {},
                onRemoveFavorite = {}
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("restaurants_tab").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("empty_restaurants_state").assertIsDisplayed()
    }


    @Test
    fun favoriteContent_hidesBadgesWhenEmpty() {
        composeTestRule.setContent {
            FavoriteContent(
                favoriteMenuItems = emptyList(),
                favoriteRestaurants = emptyList(),
                isLoading = false,
                onRestaurantClick = {},
                onMenuItemClick = {},
                onRemoveFavorite = {}
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("badge_0").assertDoesNotExist()
    }


    @Test
    fun favoriteContent_menuItemClickTriggersCallback() {
        var clickedMenuItemId = ""

        composeTestRule.setContent {
            FavoriteContent(
                favoriteMenuItems = sampleMenuItems,
                favoriteRestaurants = emptyList(),
                isLoading = false,
                onRestaurantClick = {},
                onMenuItemClick = { clickedMenuItemId = it },
                onRemoveFavorite = {}
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("menu_item_card_1").performClick()
        assert(clickedMenuItemId == "item1")
    }


    @Test
    fun favoriteContent_removeMenuItemTriggersCallback() {
        var removedItemId = -1

        composeTestRule.setContent {
            FavoriteContent(
                favoriteMenuItems = sampleMenuItems,
                favoriteRestaurants = emptyList(),
                isLoading = false,
                onRestaurantClick = {},
                onMenuItemClick = {},
                onRemoveFavorite = { removedItemId = it }
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("remove_menu_item_1").performClick()
        assert(removedItemId == 1)
    }


    @Test
    fun favoriteContent_showsCorrectEmptyStateMessages() {
        composeTestRule.setContent {
            FavoriteContent(
                favoriteMenuItems = emptyList(),
                favoriteRestaurants = emptyList(),
                isLoading = false,
                onRestaurantClick = {},
                onMenuItemClick = {},
                onRemoveFavorite = {}
            )
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("empty_menu_items_state").assertIsDisplayed()

        composeTestRule.onNodeWithTag("restaurants_tab").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("empty_restaurants_state").assertIsDisplayed()
    }
}