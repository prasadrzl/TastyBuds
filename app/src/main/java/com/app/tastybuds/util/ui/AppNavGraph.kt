package com.app.tastybuds.util.ui

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
import com.app.tastybuds.ui.home.AllCollectionsScreen
import com.app.tastybuds.ui.home.AllRestaurantsScreen
import com.app.tastybuds.ui.home.AllDealsScreen
import com.app.tastybuds.ui.home.AllVouchersScreen
import com.app.tastybuds.ui.inbox.ChatBoxScreen
import com.app.tastybuds.ui.profile.ProfileScreen
import com.app.tastybuds.ui.resturants.SearchResultType
import com.app.tastybuds.ui.resturants.SearchResultsScreen
import com.app.tastybuds.ui.resturants.RestaurantDetailsScreen
import com.app.tastybuds.ui.orders.FoodDetailsScreen
import com.app.tastybuds.ui.location.LocationTrackerScreen
import com.app.tastybuds.ui.location.OrderTrackingScreen
import com.app.tastybuds.ui.orders.OrderReviewScreen
import com.app.tastybuds.TastyBudsSplashScreen
import com.app.tastybuds.ui.resturants.CategoryDetailsScreen
import com.app.tastybuds.ui.resturants.SeeAllScreen

val items = listOf(
    BottomNavItem("home", "Home", R.drawable.ic_bn_home),
    BottomNavItem("orders", "My order", R.drawable.ic_bn_order),
    BottomNavItem("favorites", "Favorites", R.drawable.ic_bn_favorite),
    BottomNavItem("inbox", "Inbox", R.drawable.ic_bn_inbox)
)

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            TastyBudsSplashScreen(
                onSplashComplete = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onCategoryClick = { categoryId, categoryName ->
                    navController.navigate("food_listing/$categoryName/$categoryId")
                },
                onProfileClick = {
                    navController.navigate("profile")
                },
                onSearchClick = { searchTerm ->
                    if (searchTerm.isNotBlank()) {
                        navController.navigate("search_results/$searchTerm")
                    }
                },
                onRestaurantClick = { restaurantId ->
                    navController.navigate("restaurant_details/$restaurantId")
                },
                onViewAllCollections = {
                    navController.navigate("all_collections")
                },
                onViewAllRestaurants = {
                    navController.navigate("all_restaurants")
                },
                onViewAllDeals = {
                    navController.navigate("all_deals")
                },
                onViewAllVouchers = {
                    navController.navigate("all_vouchers")
                },
                onBannerClick = { bannerId ->
                    navController.navigate("all_deals")
                },
                onCollectionClick = { collectionId ->
                    navController.navigate("food_listing/Collection")
                },
                onDealClick = { dealId ->
                    navController.navigate("food_details/$dealId")
                }
            )
        }

        composable("all_collections") {
            AllCollectionsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCollectionClick = { collectionId ->
                    navController.navigate("food_listing/Collection")
                }
            )
        }

        composable("all_restaurants") {
            AllRestaurantsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onRestaurantClick = { restaurantId ->
                    navController.navigate("restaurant_details/$restaurantId")
                }
            )
        }

        composable("all_deals") {
            AllDealsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onDealClick = { dealId ->
                    navController.navigate("food_details/$dealId")
                }
            )
        }

        composable("all_vouchers") {
            AllVouchersScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onVoucherClick = { voucherId ->
                    navController.popBackStack()
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

        composable("food_listing/{categoryName}/{categoryId}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: "Food"
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            CategoryDetailsScreen(
                categoryName = categoryName,
                categoryId = categoryId,
                onBackClick = {
                    navController.popBackStack()
                },
                onRestaurantClick = { restaurantId ->
                    navController.navigate("restaurant_details/$restaurantId")
                },
                onSeeAllClick = { title, type ->
                    val encodedTitle = java.net.URLEncoder.encode(title, "UTF-8")
                    navController.navigate("see_all/$categoryId/$type/$encodedTitle")
                }
            )
        }

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
                onFilterClick = {},
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

        composable("location") {
            LocationTrackerScreen(
                onConfirm = {
                    navController.popBackStack()
                }
            )
        }

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

        composable("food_details/{foodId}") { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId") ?: ""
            FoodDetailsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onAddToCart = { totalPrice, quantity ->
                    navController.navigate("order_review")
                }
            )
        }

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
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("see_all/{categoryId}/{type}/{title}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val type = backStackEntry.arguments?.getString("type") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""

            SeeAllScreen(
                categoryId = categoryId,
                type = type,
                title = title,
                onBackClick = { navController.popBackStack() },
                onRestaurantClick = { restaurantId ->
                    navController.navigate("restaurant_details/$restaurantId")
                },
                onMenuItemClick = { menuItemId ->
                    navController.navigate("menu_item_details/$menuItemId")
                }
            )
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screensWithoutBottomBar = listOf(
        "profile",
        "food_listing/",
        "search_results/",
        "restaurant_details/",
        "food_details/",
        "location",
        "order_review",
        "splash",
        "all_collections",
        "all_restaurants",
        "all_deals",
        "all_vouchers",
        "see_all/"
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