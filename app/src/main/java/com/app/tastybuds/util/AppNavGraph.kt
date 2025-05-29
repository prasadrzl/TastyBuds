package com.app.tastybuds.util

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.app.chat.ChatScreen
import com.app.tastybuds.BottomNavItem
import com.app.tastybuds.R
import com.app.tastybuds.ui.favorites.FavoriteScreen
import com.app.tastybuds.ui.home.HomeScreen
import com.app.tastybuds.ui.inbox.ChatBoxScreen
import com.app.tastybuds.ui.orders.OrderScreen
import com.app.tastybuds.ui.profile.ProfileScreen

val items = listOf(
    BottomNavItem("home", "Home", R.drawable.ic_bn_home),
    BottomNavItem("orders", "My order", R.drawable.ic_bn_order),
    BottomNavItem("favorites", "Favorites", R.drawable.ic_bn_favorite),
    BottomNavItem("inbox", "Inbox", R.drawable.ic_bn_inbox)
)

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen() }
        composable("orders") { OrderScreen() }
        composable("favorites") { FavoriteScreen() }
        composable("inbox") { ChatBoxScreen() }
        composable("profile") {
            //ChatScreen()
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