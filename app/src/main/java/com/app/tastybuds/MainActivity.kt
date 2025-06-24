package com.app.tastybuds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.tastybuds.ui.theme.PrimaryColor
import com.app.tastybuds.ui.theme.SetSystemBarColor
import com.app.tastybuds.ui.theme.TastyBudsTheme
import com.app.tastybuds.util.ui.AppNavGraph
import com.app.tastybuds.util.ui.BottomBar
import com.app.tastybuds.util.ui.HomeSearchBar
import com.app.tastybuds.util.ui.HomeTopBar
import com.app.tastybuds.util.ui.ThemeManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isDarkMode by themeManager.isDarkMode.collectAsStateWithLifecycle(initialValue = false)
            TastyBudsTheme(darkTheme = isDarkMode, dynamicColor = false) {
                SetSystemBarColor(PrimaryColor)
                val navController = rememberNavController()
                TastyBuddyMainScreen(navController, themeManager)
            }
        }
    }
}

@Composable
fun TastyBuddyMainScreen(navController: NavHostController, themeManager: ThemeManager) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var searchText by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            val hideTopBar = currentRoute == "splash" ||
                    currentRoute == "onboarding" ||
                    currentRoute == "profile" ||
                    currentRoute == "orders" ||
                    currentRoute == "inbox" ||
                    currentRoute == "profile_edit" ||
                    currentRoute == "login" ||
                    currentRoute?.startsWith("food_listing/") == true ||
                    currentRoute?.startsWith("search_results/") == true ||
                    currentRoute?.startsWith("restaurant_details/") == true ||
                    currentRoute?.startsWith("food_details/") == true ||
                    currentRoute?.startsWith("see_all/") == true ||
                    currentRoute?.startsWith("order_tracking/") == true ||
                    currentRoute?.startsWith("menu_list/") == true ||
                    currentRoute == "location" ||
                    currentRoute == "favorites" ||
                    currentRoute == "order_review" ||
                    currentRoute == "all_collections" ||
                    currentRoute == "all_restaurants" ||
                    currentRoute == "all_deals" ||
                    currentRoute == "all_vouchers"

            if (!hideTopBar) {
                Column {
                    HomeTopBar(
                        onProfileClick = {
                            navController.navigate("profile")
                        },
                        onLocationClick = {
                            navController.navigate("location")
                        }
                    )

                    HomeSearchBar(
                        value = searchText,
                        onValueChange = { newText -> searchText = newText },
                        onSearchBarClick = {
                            navController.navigate("search_results/${searchText.ifBlank { "all" }}")
                        }
                    )
                }
            }
        },
        bottomBar = {
            val hideBottomBar = currentRoute == "splash" ||
                    currentRoute == "onboarding" ||
                    currentRoute == "profile" ||
                    currentRoute == "profile_edit" ||
                    currentRoute == "login" ||
                    currentRoute?.startsWith("food_listing/") == true ||
                    currentRoute?.startsWith("search_results/") == true ||
                    currentRoute?.startsWith("restaurant_details/") == true ||
                    currentRoute?.startsWith("food_details/") == true ||
                    currentRoute?.startsWith("order_tracking/") == true ||
                    currentRoute?.startsWith("menu_list/") == true ||
                    currentRoute == "location" ||
                    currentRoute == "order_review" ||
                    currentRoute == "all_collections" ||
                    currentRoute == "all_restaurants" ||
                    currentRoute == "all_deals" ||
                    currentRoute == "all_vouchers"

            if (!hideBottomBar) {
                BottomBar(navController = navController)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.then(
                if (currentRoute != "splash") Modifier.padding(padding) else Modifier
            )
        ) {
            CompositionLocalProvider(LocalThemeManager provides themeManager) {
                AppNavGraph(navController = navController)
            }
        }
    }
}

data class BottomNavItem(val route: String, val label: String, val iconRes: Int)

val LocalThemeManager = compositionLocalOf<ThemeManager> {
    error("ThemeManager not provided")
}