package com.app.tastybuds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.app.tastybuds.ui.theme.TastyBudsTheme
import com.app.tastybuds.util.AppNavGraph
import com.app.tastybuds.util.BottomBar
import com.app.tastybuds.util.HomeSearchBar
import com.app.tastybuds.util.HomeTopBar

data class BottomNavItem(val route: String, val label: String, val iconRes: Int)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TastyBudsTheme {
                Column {
                    HomeTopBar()
                    HomeSearchBar()
                    TastyBuddyMainScreen()
                }
            }
        }
    }
}

@Composable
fun TastyBuddyMainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            AppNavGraph(navController = navController)
        }
    }
}
