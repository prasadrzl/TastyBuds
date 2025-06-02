// Updated AppNavGraph.kt - Add Navigation for Location and Restaurant Details
package com.app.tastybuds.util

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.app.tastybuds.BottomNavItem
import com.app.tastybuds.R
import com.app.tastybuds.ui.favorites.FavoriteScreen
import com.app.tastybuds.ui.home.HomeScreen
import com.app.tastybuds.ui.inbox.ChatBoxScreen
import com.app.tastybuds.ui.profile.ProfileScreen
import com.app.tastybuds.ui.foodlisting.FoodListingScreen
import com.app.tastybuds.ui.home.SearchResultType
import com.app.tastybuds.ui.home.SearchResultsScreen
import com.app.tastybuds.ui.restaurant.RestaurantDetailsScreen
import com.app.tastybuds.ui.orders.FoodDetailsScreen
import com.app.tastybuds.ui.location.LocationTrackerScreen
import com.app.tastybuds.ui.location.OrderTrackingScreen
import com.app.tastybuds.ui.orders.OrderReviewScreen

val items = listOf(
    BottomNavItem("home", "Home", R.drawable.ic_bn_home),
    BottomNavItem("orders", "My order", R.drawable.ic_bn_order),
    BottomNavItem("favorites", "Favorites", R.drawable.ic_bn_favorite),
    BottomNavItem("inbox", "Inbox", R.drawable.ic_bn_inbox)
)

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onCategoryClick = { categoryId, categoryName ->
                    navController.navigate("food_listing/$categoryName")
                },
                onProfileClick = {
                    navController.navigate("profile")
                },
                onSearchClick = { searchTerm ->
                    if (searchTerm.isNotBlank()) {
                        navController.navigate("search_results/$searchTerm")
                    }
                },
                // NEW: Add restaurant click handler for "Recommended for you" section
                onRestaurantClick = { restaurantId ->
                    navController.navigate("restaurant_details/$restaurantId")
                }
            )
        }

        composable("orders") { OrderTrackingScreen() }
        composable("favorites") { FavoriteScreen() }
        composable("inbox") { ChatBoxScreen() }

        composable("profile") {
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Food Listing Screen
        composable("food_listing/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Food"
            FoodListingScreen(
                categoryName = categoryName,
                onBackClick = {
                    navController.popBackStack()
                },
                onRestaurantClick = { restaurantId ->
                    navController.navigate("restaurant_details/$restaurantId")
                }
            )
        }

        // Search Results Screen
        composable("search_results/{searchTerm}") { backStackEntry ->
            val searchTerm = backStackEntry.arguments?.getString("searchTerm") ?: ""
            SearchResultsScreen(
                initialSearchTerm = searchTerm,
                onBackClick = {
                    navController.popBackStack()
                },
                onCloseClick = {
                    navController.popBackStack()
                },
                onFilterClick = {
                    // TODO: Open filter modal
                },
                onResultClick = { resultId, resultType ->
                    when (resultType) {
                        SearchResultType.RESTAURANT -> {
                            navController.navigate("restaurant_details/$resultId")
                        }
                        SearchResultType.FOOD_ITEM -> {
                            navController.navigate("food_details/$resultId")
                        }
                    }
                }
            )
        }

        // NEW: Location Tracker Screen
        composable("location") {
            LocationTrackerScreen(
                onConfirm = {
                    // After location is confirmed, go back to home
                    navController.popBackStack()
                }
            )
        }

        // NEW: Restaurant Details Screen
        composable("restaurant_details/{restaurantId}") { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: ""
            RestaurantDetailsScreen(
                restaurantId = restaurantId,
                onBackClick = {
                    navController.popBackStack()
                },
                onFoodItemClick = { foodItemId ->
                    navController.navigate("food_details/$foodItemId")
                },
                onComboClick = { comboId ->
                    navController.navigate("food_details/$comboId")
                }
            )
        }

        // Food Details Screen (for customization)
        composable("food_details/{foodId}") { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId") ?: ""
            FoodDetailsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onAddToCart = { totalPrice, quantity ->
                    // Navigate to order review or show success message
                    navController.navigate("order_review")
                }
            )
        }

        // NEW: Order Review Screen
        composable("order_review") {
            OrderReviewScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onChangeAddress = {
                    navController.navigate("location")
                },
                onAddMore = {
                    navController.popBackStack()
                },
                onEditItem = { itemId ->
                    navController.navigate("food_details/$itemId")
                },
                onAlsoOrderedClick = { itemId ->
                    navController.navigate("food_details/$itemId")
                },
                onOrderNow = {
                    // TODO: Navigate to payment or order confirmation
                    // For now, go back to home
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Don't show bottom bar on certain screens
    val screensWithoutBottomBar = listOf(
        "profile",
        "food_listing/",
        "search_results/",
        "restaurant_details/",
        "food_details/",
        "location",
        "order_review"
    )
    val shouldShowBottomBar = !screensWithoutBottomBar.any { route ->
        currentRoute?.startsWith(route) == true
    }

    if (shouldShowBottomBar) {
        NavigationBar {
            items.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.label,
                            tint = if (selected) Color(0xFFFF6F00) else Color(0xFF333333)
                        )
                    },
                    label = { Text(item.label) },
                    selected = selected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}

// Helper function to check if screen should hide top bar
fun shouldHideTopBar(currentRoute: String?): Boolean {
    val screensWithoutTopBar = listOf(
        "profile",
        "food_listing/",
        "restaurant_details/",
        "food_details/",
        "location",
        "order_review"
    )
    return screensWithoutTopBar.any { route ->
        currentRoute?.startsWith(route) == true
    }
}