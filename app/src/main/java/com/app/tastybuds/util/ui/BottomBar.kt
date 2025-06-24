package com.app.tastybuds.util.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.tastybuds.ui.theme.navigationBarBackgroundColor
import com.app.tastybuds.ui.theme.navigationBarContentColor
import com.app.tastybuds.ui.theme.navigationBarSelectedColor
import com.app.tastybuds.ui.theme.navigationBarUnselectedColor
import com.app.tastybuds.ui.theme.primaryColor

@Composable
fun BottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = navigationBarBackgroundColor(),
        contentColor = navigationBarContentColor()
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label,
                        tint = if (selected) {
                            navigationBarSelectedColor()
                        } else {
                            navigationBarUnselectedColor()
                        }
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (selected) {
                            navigationBarSelectedColor()
                        } else {
                            navigationBarUnselectedColor()
                        }
                    )
                },
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = navigationBarSelectedColor(),
                    unselectedIconColor = navigationBarUnselectedColor(),
                    selectedTextColor = navigationBarSelectedColor(),
                    unselectedTextColor = navigationBarUnselectedColor(),
                    indicatorColor = primaryColor().copy(alpha = 0.12f)
                )
            )
        }
    }
}