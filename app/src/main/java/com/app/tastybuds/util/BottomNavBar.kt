import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.tastybuds.BottomNavItem

@Composable
fun AppNavGraph(navController: NavHostController, startDestination: String) {
    NavHost(navController, startDestination = startDestination) {
        composable("home") { Text("Home Screen") }
        composable("orders") { Text("Orders Screen") }
        composable("favorites") { Text("Favorites Screen") }
        composable("inbox") { Text("Inbox Screen") }
    }
}


@Composable
fun BottomBar(
    navController: NavHostController,
    items: List<BottomNavItem>,
    startDestination: String
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    NavigationBar {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(item.iconRes),
                        contentDescription = item.label,
                        tint = if (selected) Color(0xFFFF6F00) else Color(0xFF333333)
                    )
                },
                label = { Text(item.label) },
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(startDestination) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}


enum class Screen(val route: String) {
    Home("home"),
    Orders("orders"),
    Favorites("favorites"),
    Inbox("inbox")
}


