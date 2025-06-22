package com.app.tastybuds.util.ui

import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.app.tastybuds.BottomNavItem
import com.app.tastybuds.R
import com.app.tastybuds.data.SearchResultType.FOOD_ITEM
import com.app.tastybuds.data.SearchResultType.RESTAURANT
import com.app.tastybuds.ui.favorites.FavoriteScreen
import com.app.tastybuds.ui.home.AllCollectionsScreen
import com.app.tastybuds.ui.home.AllDealsScreen
import com.app.tastybuds.ui.home.AllRestaurantsScreen
import com.app.tastybuds.ui.home.AllVouchersScreen
import com.app.tastybuds.ui.home.HomeScreen
import com.app.tastybuds.ui.inbox.ChatBoxScreen
import com.app.tastybuds.ui.location.LocationTrackerScreen
import com.app.tastybuds.ui.location.OrderTrackingScreen
import com.app.tastybuds.ui.login.AuthCheckScreen
import com.app.tastybuds.ui.login.LoginScreen
import com.app.tastybuds.ui.onboarding.OnboardingViewModel
import com.app.tastybuds.ui.orders.CartViewModel
import com.app.tastybuds.ui.orders.FoodDetailsScreen
import com.app.tastybuds.ui.orders.OrderDetailsScreen
import com.app.tastybuds.ui.orders.OrderReviewScreen
import com.app.tastybuds.ui.orders.OrdersScreen
import com.app.tastybuds.ui.profile.ProfileScreen
import com.app.tastybuds.ui.profile.ProfileSettingsScreen
import com.app.tastybuds.ui.resturants.CategoryDetailsScreen
import com.app.tastybuds.ui.resturants.RestaurantDetailsScreen
import com.app.tastybuds.ui.resturants.SeeAllScreen
import com.app.tastybuds.ui.resturants.search.SearchResultsScreen
import com.app.tastybuds.ui.splash.TastyBudsSplashScreen
import com.app.tastybuds.ui.onboarding.OnboardingScreen
import kotlinx.coroutines.launch

val items = listOf(
    BottomNavItem("home", "Home", R.drawable.ic_bn_home),
    BottomNavItem("orders", "My order", R.drawable.ic_bn_order),
    BottomNavItem("favorites", "Favorites", R.drawable.ic_bn_favorite),
    BottomNavItem("inbox", "Inbox", R.drawable.ic_bn_inbox)
)

@Composable
fun AppNavGraph(navController: NavHostController) {
    val sharedCartViewModel: CartViewModel = hiltViewModel()
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {

            TastyBudsSplashScreen(
                onSplashComplete = {

                    val destination = when {
                        !onboardingViewModel.isOnboardingCompleted() -> "onboarding"
                        onboardingViewModel.isUserLoggedIn() -> "home"
                        else -> "auth_check"
                    }

                    navController.navigate(destination) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                onNavigateToLogin = {
                    onboardingViewModel.markOnboardingCompleted()
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("profile") {
            val scope = rememberCoroutineScope()
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onEditProfile = { navController.navigate("profile_edit") },
                onSignOut = {
                    scope.launch {
                        onboardingViewModel.clearUserSession()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("auth_check") {
            AuthCheckScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("auth_check") { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("auth_check") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            Log.d("Navigation", "📱 LoginScreen displayed")

            LoginScreen(
                onLoginSuccess = {
                    Log.d("Navigation", "✅ Login successful!")

                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
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
                onBannerClick = { _ ->
                    navController.navigate("all_deals")
                },
                onCollectionClick = { collectionId ->
                    navController.navigate("food_listing/collection/$collectionId")
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
                    navController.navigate("food_listing/collection/$collectionId")
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

        composable("orders") {

            OrdersScreen(
                onOrderClick = { orderId ->
                    navController.navigate("order_details/$orderId")
                },
                onTrackOrder = { orderId ->
                    navController.navigate("order_tracking/$orderId")
                },
                onReorderClick = { order ->
                    if (order.restaurantId?.isNotEmpty() == true) {
                        navController.navigate("restaurant_details/${order.restaurantId}")
                    }
                }
            )
        }

        composable("favorites") {
            FavoriteScreen(
                onRestaurantClick = { restaurantId ->
                    navController.navigate("restaurant_details/$restaurantId")
                },
                onMenuItemClick = { menuItemId ->
                    navController.navigate("food_details/$menuItemId")
                }
            )
        }

        composable("inbox") { ChatBoxScreen() }

        composable("profile_edit") {
            ProfileSettingsScreen(
                viewModel = hiltViewModel(),
                onDismiss = { navController.popBackStack() },
                onSaveChanges = { navController.popBackStack() }
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

        composable(
            "search_results/{searchTerm}",
            arguments = listOf(
                navArgument("searchTerm") {
                    type = NavType.StringType
                    defaultValue = "all"
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val searchTerm = backStackEntry.arguments?.getString("searchTerm") ?: "all"
            SearchResultsScreen(
                initialSearchTerm = if (searchTerm == "all") "" else searchTerm,
                onBackClick = {
                    navController.popBackStack()
                },
                onFilterClick = {},
                onResultClick = { resultId, resultType ->
                    when (resultType) {
                        RESTAURANT -> {
                            navController.navigate("restaurant_details/$resultId")
                        }

                        FOOD_ITEM -> {
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

        composable(
            "food_details/{foodId}",
            arguments = listOf(navArgument("foodId") { type = NavType.StringType })
        ) { backStackEntry ->
            val foodId = backStackEntry.arguments?.getString("foodId") ?: ""

            FoodDetailsScreen(
                foodItemId = foodId,
                onBackClick = { navController.popBackStack() },
                onAddToCart = { cartItem ->
                    sharedCartViewModel.addToCart(cartItem)

                    navController.navigate("order_review")
                }
            )
        }

        composable(
            "order_details/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""

            OrderDetailsScreen(
                orderId = orderId,
                onBackClick = {
                    navController.popBackStack()
                },
                onTrackOrder = {
                    navController.navigate("order_tracking/$orderId")
                },
                onContactRestaurant = {}
            )
        }

        composable("order_review") {
            val cartItems by sharedCartViewModel.cartItems.collectAsState()

            OrderReviewScreen(
                cartItems = cartItems,
                onBackClick = {
                    navController.popBackStack()
                },
                onOrderSuccess = { orderId ->
                    sharedCartViewModel.clearCart()
                    navController.navigate("order_tracking/$orderId") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onChangeAddress = {
                    navController.navigate("location")
                },
                onSelectOffer = {
                    navController.navigate("select_offers")
                },
                onAddMore = { restaurantId ->
                    if (restaurantId != null) {
                        navController.navigate("restaurant_details/$restaurantId")
                    } else {
                        navController.navigate("home")
                    }
                },
                onEditItem = { cartItem ->
                    sharedCartViewModel.setEditingItem(cartItem)
                    navController.navigate("food_details/${cartItem.menuItemId}")
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

        composable(
            "order_tracking/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderTrackingScreen(
                orderId = orderId,
                onBackClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
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