package com.app.tastybuds.ui.theme

import AppTypography
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = OnBackgroundLight,
    onSurface = OnSurfaceLight,
    error = ErrorColor,
    onError = Color.White,
    outline = BorderLight,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = TextSecondary
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark,
    error = ErrorColor,
    onError = Color.White,
    outline = BorderDark,
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = TextSecondaryDark
)

private val LightExtendedColors = ExtendedColors(
    success = SuccessColor,
    onSuccess = Color.White,
    successContainer = SuccessColor.copy(alpha = 0.1f),
    onSuccessContainer = SuccessColor,
    info = InfoColor,
    onInfo = Color.White,
    infoContainer = InfoColor.copy(alpha = 0.1f),
    onInfoContainer = InfoColor,
    warning = WarningColor,
    onWarning = Color.Black,
    warningContainer = WarningColor.copy(alpha = 0.1f),
    onWarningContainer = WarningColor,
    error = ErrorColor,
    onError = Color.White,
    errorContainer = ErrorColor.copy(alpha = 0.1f),
    onErrorContainer = ErrorColor,
    rating = RatingColor,
    onRating = Color.Black,
    buttonBackground = ButtonBackgroundLight,
    onButtonBackground = PrimaryColor,
    textSecondary = TextSecondary,
    textDisabled = TextDisabled,
    border = BorderLight,
    borderFocus = BorderFocus,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = TextSecondary,
    favorite = ErrorColor,
    onFavorite = Color.White,
)

private val DarkExtendedColors = ExtendedColors(
    success = SuccessColor,
    onSuccess = Color.Black,
    successContainer = SuccessColor.copy(alpha = 0.2f),
    onSuccessContainer = SuccessColor,
    info = InfoColor,
    onInfo = Color.Black,
    infoContainer = InfoColor.copy(alpha = 0.2f),
    onInfoContainer = InfoColor,
    warning = WarningColor,
    onWarning = Color.Black,
    warningContainer = WarningColor.copy(alpha = 0.2f),
    onWarningContainer = WarningColor,
    error = ErrorColor,
    onError = Color.Black,
    errorContainer = ErrorColor.copy(alpha = 0.2f),
    onErrorContainer = ErrorColor,
    rating = RatingColor,
    onRating = Color.Black,
    buttonBackground = ButtonBackgroundDark,
    onButtonBackground = PrimaryColor,
    textSecondary = TextSecondaryDark,
    textDisabled = TextDisabledDark,
    border = BorderDark,
    borderFocus = BorderFocus,
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = TextSecondaryDark,
    favorite = ErrorColor,
    onFavorite = Color.Black,
)

@Composable
fun TastyBudsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
    SetSystemBarColor(colorScheme.primary)
}

val extendedColors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current