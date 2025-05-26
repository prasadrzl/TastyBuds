package com.app.tastybuds.util

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.app.tastybuds.R
import com.app.tastybuds.ui.BottomNavItem

val items = listOf(
    BottomNavItem("home", "Home", R.drawable.ic_bn_home),
    BottomNavItem("orders", "My order", R.drawable.ic_bn_order),
    BottomNavItem("favorites", "Favorites", R.drawable.ic_bn_favorite),
    BottomNavItem("inbox", "Inbox", R.drawable.ic_bn_inbox)
)

@Composable
fun BottomNavBar(
    selectedRoute: String,
    onItemClick: (String) -> Unit,
    activeColor: Color = Color(0xFFFF6F00),
    inactiveColor: Color = Color(0xFF333333)
) {
    NavigationBar {
        items.forEach { item ->
            val isSelected = item.route == selectedRoute
            val iconPainter = painterResource(id = item.iconRes)

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = iconPainter,
                        contentDescription = item.label,
                        tint = if (isSelected) activeColor else inactiveColor
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (isSelected) activeColor else inactiveColor
                    )
                },
                selected = isSelected,
                onClick = { onItemClick(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent, // no highlight background
                    selectedIconColor = activeColor,
                    unselectedIconColor = inactiveColor,
                    selectedTextColor = activeColor,
                    unselectedTextColor = inactiveColor
                )
            )
        }
    }
}

