// Updated AppNavGraph.kt - Add Search Results Navigation
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
import com.app.tastybuds.ui.orders.OrderScreen
import com.app.tastybuds.ui.profile.ProfileScreen
import com.app.tastybuds.ui.foodlisting.FoodListingScreen
import com.app.tastybuds.ui.home.SearchResultType
import com.app.tastybuds.ui.home.SearchResultsScreen

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
                }
            )
        }
        composable("orders") { OrderScreen() }
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
                searchTerm = searchTerm,
                resultsCount = 359, // This would come from actual search results
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

        // Future screens to be added
        composable("restaurant_details/{restaurantId}") { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: ""
            // RestaurantDetailsScreen will be implemented next
            // Placeholder for now
        }

        composable("food_details/{foodId}") { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId") ?: ""
            // FoodDetailsScreen will be implemented later
            // Placeholder for now
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
        "food_details/"
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
        "restaurant_details/"
    )
    return screensWithoutTopBar.any { route ->
        currentRoute?.startsWith(route) == true
    }
}