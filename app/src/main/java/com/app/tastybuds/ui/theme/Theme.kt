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
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = OnPrimaryLight,
    onSecondary = OnSecondaryLight,
    onBackground = OnBackgroundLight,
    onSurface = OnSurfaceLight,
    error = ErrorColor,
    onError = OnErrorLight,
    outline = BorderLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = OnPrimaryDark,
    onSecondary = OnSecondaryDark,
    onBackground = OnBackgroundDark,
    onSurface = OnSurfaceDark,
    error = ErrorColor,
    onError = OnErrorDark,
    outline = BorderDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark
)

private val LightExtendedColors = ExtendedColors(
    success = SuccessColor,
    onSuccess = OnSuccessLight,
    successContainer = SuccessColor.copy(alpha = CONTAINER_ALPHA_LIGHT),
    onSuccessContainer = SuccessColor,
    info = InfoColor,
    onInfo = OnInfoLight,
    infoContainer = InfoColor.copy(alpha = CONTAINER_ALPHA_LIGHT),
    onInfoContainer = InfoColor,
    warning = WarningColor,
    onWarning = OnWarningLight,
    warningContainer = WarningColor.copy(alpha = CONTAINER_ALPHA_LIGHT),
    onWarningContainer = WarningColor,
    error = ErrorColor,
    onError = OnErrorExtendedLight,
    errorContainer = ErrorColor.copy(alpha = CONTAINER_ALPHA_LIGHT),
    onErrorContainer = ErrorColor,
    rating = RatingColor,
    onRating = OnRatingLight,
    buttonBackground = ButtonBackgroundLight,
    onButtonBackground = PrimaryColor,
    textSecondary = TextSecondary,
    textDisabled = TextDisabled,
    border = BorderLight,
    borderFocus = BorderFocus,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    favorite = ErrorColor,
    onFavorite = OnFavoriteLight,
)

private val DarkExtendedColors = ExtendedColors(
    success = SuccessColor,
    onSuccess = OnSuccessDark,
    successContainer = SuccessColor.copy(alpha = CONTAINER_ALPHA_DARK),
    onSuccessContainer = SuccessColor,
    info = InfoColor,
    onInfo = OnInfoDark,
    infoContainer = InfoColor.copy(alpha = CONTAINER_ALPHA_DARK),
    onInfoContainer = InfoColor,
    warning = WarningColor,
    onWarning = OnWarningDark,
    warningContainer = WarningColor.copy(alpha = CONTAINER_ALPHA_DARK),
    onWarningContainer = WarningColor,
    error = ErrorColor,
    onError = OnErrorExtendedDark,
    errorContainer = ErrorColor.copy(alpha = CONTAINER_ALPHA_DARK),
    onErrorContainer = ErrorColor,
    rating = RatingColor,
    onRating = OnRatingDark,
    buttonBackground = ButtonBackgroundDark,
    onButtonBackground = PrimaryColor,
    textSecondary = TextSecondaryDark,
    textDisabled = TextDisabledDark,
    border = BorderDark,
    borderFocus = BorderFocus,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    favorite = ErrorColor,
    onFavorite = OnFavoriteDark,
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
}

val extendedColors: ExtendedColors
    @Composable
    get() = LocalExtendedColors.current