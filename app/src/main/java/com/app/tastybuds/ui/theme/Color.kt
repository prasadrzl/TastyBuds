package com.app.tastybuds.ui.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Primary Colors
val PrimaryColor = Color(0xFFFF7700)
val SecondaryColor = Color(0xFFFFCC32)

// Light Theme Colors
val BackgroundLight = Color(0xFFFFFFFF)
val SurfaceLight = Color(0xFFFFFFFF)
val OnBackgroundLight = Color(0xFF000000)
val OnSurfaceLight = Color(0xFF000000)

// Dark Theme Colors
val BackgroundDark = Color(0xFF121212)
val SurfaceDark = Color(0xFF1E1E1E)
val OnBackgroundDark = Color(0xFFFFFFFF)
val OnSurfaceDark = Color(0xFFFFFFFF)

// Surface Variant Colors
val SurfaceVariantLight = Color(0xFFF5F5F5)
val SurfaceVariantDark = Color(0xFF2D2D2D)

// On-Color Constants
val OnPrimaryLight = Color.White
val OnPrimaryDark = Color.White
val OnSecondaryLight = Color.Black
val OnSecondaryDark = Color.Black
val OnErrorLight = Color.White
val OnErrorDark = Color.Black

// Extended Color On-Colors
val OnSuccessLight = Color.White
val OnSuccessDark = Color.Black
val OnInfoLight = Color.White
val OnInfoDark = Color.Black
val OnWarningLight = Color.Black
val OnWarningDark = Color.Black
val OnErrorExtendedLight = Color.White
val OnErrorExtendedDark = Color.Black
val OnRatingLight = Color.Black
val OnRatingDark = Color.Black
val OnFavoriteLight = Color.White
val OnFavoriteDark = Color.Black

// Alpha Constants (replaces hardcoded 0.1f and 0.2f)
const val CONTAINER_ALPHA_LIGHT = 0.1f
const val CONTAINER_ALPHA_DARK = 0.2f

// Semantic Colors
val InfoColor = Color(0xFF379AE6)
val SuccessColor = Color(0xFF1DD75B)
val ErrorColor = Color(0xFFDE3B40)
val WarningColor = Color(0xFFFFCC32)

// Special UI Colors
val RatingColor = Color(0xFFFFC107)
val ButtonBackgroundLight = Color(0xFFFFE7D2)
val ButtonBackgroundDark = Color(0xFF2D1B0A)

// Text Colors
val TextPrimary = Color(0xFF000000)
val TextSecondary = Color(0xFF757575)
val TextDisabled = Color(0xFFBDBDBD)
val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFFB3B3B3)
val TextDisabledDark = Color(0xFF666666)

// Border Colors
val BorderLight = Color(0xFFE0E0E0)
val BorderDark = Color(0xFF404040)
val BorderFocus = PrimaryColor

// Extended Color Scheme
@Stable
class ExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val info: Color,
    val onInfo: Color,
    val infoContainer: Color,
    val onInfoContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val rating: Color,
    val onRating: Color,
    val buttonBackground: Color,
    val onButtonBackground: Color,
    val textSecondary: Color,
    val textDisabled: Color,
    val border: Color,
    val borderFocus: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val favorite: Color,
    val onFavorite: Color,
) {
    fun copy(
        success: Color = this.success,
        onSuccess: Color = this.onSuccess,
        successContainer: Color = this.successContainer,
        onSuccessContainer: Color = this.onSuccessContainer,
        info: Color = this.info,
        onInfo: Color = this.onInfo,
        infoContainer: Color = this.infoContainer,
        onInfoContainer: Color = this.onInfoContainer,
        warning: Color = this.warning,
        onWarning: Color = this.onWarning,
        warningContainer: Color = this.warningContainer,
        onWarningContainer: Color = this.onWarningContainer,
        error: Color = this.error,
        onError: Color = this.onError,
        errorContainer: Color = this.errorContainer,
        onErrorContainer: Color = this.onErrorContainer,
        rating: Color = this.rating,
        onRating: Color = this.onRating,
        buttonBackground: Color = this.buttonBackground,
        onButtonBackground: Color = this.onButtonBackground,
        textSecondary: Color = this.textSecondary,
        textDisabled: Color = this.textDisabled,
        border: Color = this.border,
        borderFocus: Color = this.borderFocus,
        surfaceVariant: Color = this.surfaceVariant,
        onSurfaceVariant: Color = this.onSurfaceVariant,
        favorite: Color = this.favorite,
        onFavorite: Color = this.onFavorite,
    ): ExtendedColors = ExtendedColors(
        success = success,
        onSuccess = onSuccess,
        successContainer = successContainer,
        onSuccessContainer = onSuccessContainer,
        info = info,
        onInfo = onInfo,
        infoContainer = infoContainer,
        onInfoContainer = onInfoContainer,
        warning = warning,
        onWarning = onWarning,
        warningContainer = warningContainer,
        onWarningContainer = onWarningContainer,
        error = error,
        onError = onError,
        errorContainer = errorContainer,
        onErrorContainer = onErrorContainer,
        rating = rating,
        onRating = onRating,
        buttonBackground = buttonBackground,
        onButtonBackground = onButtonBackground,
        textSecondary = textSecondary,
        textDisabled = textDisabled,
        border = border,
        borderFocus = borderFocus,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        favorite = favorite,
        onFavorite = onFavorite,
    )
}

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        success = Color.Unspecified,
        onSuccess = Color.Unspecified,
        successContainer = Color.Unspecified,
        onSuccessContainer = Color.Unspecified,
        info = Color.Unspecified,
        onInfo = Color.Unspecified,
        infoContainer = Color.Unspecified,
        onInfoContainer = Color.Unspecified,
        warning = Color.Unspecified,
        onWarning = Color.Unspecified,
        warningContainer = Color.Unspecified,
        onWarningContainer = Color.Unspecified,
        error = Color.Unspecified,
        onError = Color.Unspecified,
        errorContainer = Color.Unspecified,
        onErrorContainer = Color.Unspecified,
        rating = Color.Unspecified,
        onRating = Color.Unspecified,
        buttonBackground = Color.Unspecified,
        onButtonBackground = Color.Unspecified,
        textSecondary = Color.Unspecified,
        textDisabled = Color.Unspecified,
        border = Color.Unspecified,
        borderFocus = Color.Unspecified,
        surfaceVariant = Color.Unspecified,
        onSurfaceVariant = Color.Unspecified,
        favorite = Color.Unspecified,
        onFavorite = Color.Unspecified,
    )
}