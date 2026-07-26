package com.app.tastybuds.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SetSystemBarColor(
    statusBarColor: Color = primaryColor(),
    navigationBarColor: Color = primaryColor()
) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = statusBarColor.luminance() > 0.5f

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = useDarkIcons
        )
        systemUiController.setNavigationBarColor(
            color = navigationBarColor,
            darkIcons = useDarkIcons
        )
    }
}