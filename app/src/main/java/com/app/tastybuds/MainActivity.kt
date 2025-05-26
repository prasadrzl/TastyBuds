package com.app.tastybuds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.app.tastybuds.ui.favorites.FavoriteScreen
import com.app.tastybuds.ui.home.HomeScreen
import com.app.tastybuds.ui.inbox.ChatBoxScreen
import com.app.tastybuds.ui.orders.OrderScreen
import com.app.tastybuds.ui.theme.TastyBudsTheme
import com.app.tastybuds.util.BottomNavBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TastyBudsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BottomNavBar("home", {})
                }
            }
        }
    }
}

@Composable
fun TastyBuddyMainScreen() {
    var selectedRoute by rememberSaveable { mutableStateOf("home") }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedRoute = selectedRoute,
                onItemClick = { selectedRoute = it }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedRoute) {
                "home" -> HomeScreen()
                "orders" -> OrderScreen()
                "favorites" -> FavoriteScreen()
                "inbox" -> ChatBoxScreen()
            }
        }
    }
}

