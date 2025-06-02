// Updated MainActivity.kt - Fix Orange Header Issue
package com.app.tastybuds

import AudioCallScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.app.tastybuds.ui.location.LocationScreen
import com.app.tastybuds.ui.location.OrderTrackingScreen
import com.app.tastybuds.ui.theme.PrimaryColor
import com.app.tastybuds.ui.theme.TastyBudsTheme
import com.app.tastybuds.util.AppNavGraph
import com.app.tastybuds.util.BottomBar
import com.app.tastybuds.util.HomeSearchBar
import com.app.tastybuds.util.HomeTopBar
import com.app.tastybuds.util.SetSystemBarColor
import com.app.tastybuds.util.shouldHideTopBar

data class BottomNavItem(val route: String, val label: String, val iconRes: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TastyBudsTheme(darkTheme = false) {
                SetSystemBarColor(PrimaryColor)
                val navController = rememberNavController()
                TastyBuddyMainScreen(navController)
            }
        }
    }
}

@Composable
fun TastyBuddyMainScreen(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var searchText by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            // Hide top bar for specific screens
            val hideTopBar = currentRoute == "profile" ||
                    currentRoute?.startsWith("food_listing/") == true ||
                    currentRoute?.startsWith("search_results/") == true ||
                    currentRoute?.startsWith("restaurant_details/") == true

            if (!hideTopBar) {
                Column {
                    HomeTopBar(onProfileClick = {
                        navController.navigate("profile")
                    })
                    HomeSearchBar(
                        value = searchText,
                        onValueChange = { newText -> searchText = newText },
                        onSearchClick = { searchTerm ->
                            if (searchTerm.isNotBlank()) {
                                navController.navigate("search_results/$searchTerm")
                            }
                        },
                        onSearchBarClick = {
                            // Navigate to search screen even with empty search
                            navController.navigate("search_results/${searchText.ifBlank { "food" }}")
                        }
                    )
                }
            }
        },
        bottomBar = {
            // Hide bottom bar for specific screens
            val hideBottomBar = currentRoute == "profile" ||
                    currentRoute?.startsWith("food_listing/") == true ||
                    currentRoute?.startsWith("search_results/") == true ||
                    currentRoute?.startsWith("restaurant_details/") == true

            if (!hideBottomBar) {
                BottomBar(navController = navController)
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AppNavGraph(navController = navController)
        }
    }
}