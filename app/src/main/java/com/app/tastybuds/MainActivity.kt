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
import com.app.tastybuds.ui.theme.SetSystemBarColor
import com.app.tastybuds.ui.theme.TastyBudsTheme
import com.app.tastybuds.util.ui.AppNavGraph
import com.app.tastybuds.util.ui.BottomBar
import com.app.tastybuds.util.ui.HomeSearchBar
import com.app.tastybuds.util.ui.HomeTopBar
import com.app.tastybuds.util.ui.ThemeManager
import com.app.tastybuds.util.ui.getNavigationBarColor
import com.app.tastybuds.util.ui.getStatusBarColor
import com.app.tastybuds.util.ui.shouldHideTopBar
import com.app.tastybuds.util.ui.shouldShowBottomBar
import com.app.tastybuds.util.ui.shouldShowHomeTopBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

val LocalThemeManager = compositionLocalOf<ThemeManager> {
    error("ThemeManager not provided")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isDarkMode by themeManager.isDarkMode.collectAsStateWithLifecycle(initialValue = false)
            TastyBudsTheme(darkTheme = isDarkMode, dynamicColor = false) {
                CompositionLocalProvider(LocalThemeManager provides themeManager) {
                    val navController = rememberNavController()
                    TastyBuddyMainScreen(navController)
                }
            }
        }
    }
}

@Composable
fun TastyBuddyMainScreen(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var searchText by rememberSaveable { mutableStateOf("") }

    val statusBarColor = currentRoute.getStatusBarColor()
    val navigationBarColor = currentRoute.getNavigationBarColor()

    SetSystemBarColor(
        statusBarColor = statusBarColor,
        navigationBarColor = navigationBarColor
    )

    Scaffold(
        topBar = {
            when {
                currentRoute.shouldHideTopBar() -> {}

                currentRoute.shouldShowHomeTopBar() -> {
                    Column {
                        HomeTopBar(
                            onProfileClick = { navController.navigate("profile") },
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
            }
        },
        bottomBar = {
            if (currentRoute.shouldShowBottomBar()) {
                BottomBar(navController)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AppNavGraph(navController)
        }
    }
}