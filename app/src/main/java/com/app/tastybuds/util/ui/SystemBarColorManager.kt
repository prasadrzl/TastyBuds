package com.app.tastybuds.util.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.navigationBarBackgroundColor
import com.app.tastybuds.ui.theme.primaryColor

object SystemBarColorManager {

    private val primaryStatusBarRoutes = setOf(
        "home",
        "splash"
    )

    private val backgroundNavigationBarRoutes = setOf(
        "profile",
        "profile_edit"
    )

    private val bottomNavRoutes = setOf(
        "home",
        "orders",
        "favorites",
        "inbox"
    )

    private val primaryNavigationBarRoutes = setOf(
        "splash"
    )

    @Composable
    fun getStatusBarColor(currentRoute: String?): Color {
        return when {
            currentRoute in primaryStatusBarRoutes -> primaryColor()
            else -> backgroundColor()
        }
    }

    @Composable
    fun getNavigationBarColor(currentRoute: String?): Color {
        return when (currentRoute) {
            in primaryNavigationBarRoutes -> primaryColor()
            in backgroundNavigationBarRoutes -> backgroundColor()
            in bottomNavRoutes -> navigationBarBackgroundColor()
            else -> backgroundColor()
        }
    }

    fun shouldShowBottomBar(currentRoute: String?): Boolean {
        return currentRoute in bottomNavRoutes
    }

    fun shouldHideTopBar(currentRoute: String?): Boolean {
        val hideTopBarRoutes = setOf(
            "splash",
            "onboarding",
            "profile",
            "orders",
            "inbox",
            "profile_edit",
            "login"
        )

        return currentRoute in hideTopBarRoutes ||
                currentRoute?.startsWith("food_listing/") == true ||
                currentRoute?.startsWith("search_results/") == true ||
                currentRoute?.startsWith("restaurant_details/") == true ||
                currentRoute?.startsWith("food_details/") == true ||
                currentRoute?.startsWith("see_all/") == true ||
                currentRoute?.startsWith("order_review/") == true ||
                currentRoute?.startsWith("order_tracking/") == true ||
                currentRoute?.startsWith("location_tracker") == true
    }

    fun shouldShowHomeTopBar(currentRoute: String?): Boolean {
        return currentRoute == "home"
    }
}

@Composable
fun String?.getStatusBarColor(): Color = SystemBarColorManager.getStatusBarColor(this)

@Composable
fun String?.getNavigationBarColor(): Color = SystemBarColorManager.getNavigationBarColor(this)

fun String?.shouldShowBottomBar(): Boolean = SystemBarColorManager.shouldShowBottomBar(this)

fun String?.shouldHideTopBar(): Boolean = SystemBarColorManager.shouldHideTopBar(this)

fun String?.shouldShowHomeTopBar(): Boolean = SystemBarColorManager.shouldShowHomeTopBar(this)