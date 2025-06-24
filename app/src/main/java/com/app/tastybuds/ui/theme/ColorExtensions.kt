package com.app.tastybuds.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// =============================================
// Material 3 ColorScheme Extensions
// =============================================

@Composable
fun primaryColor(): Color = MaterialTheme.colorScheme.primary

@Composable
fun onPrimaryColor(): Color = MaterialTheme.colorScheme.onPrimary

@Composable
fun primaryContainerColor(): Color = MaterialTheme.colorScheme.primaryContainer

@Composable
fun onPrimaryContainerColor(): Color = MaterialTheme.colorScheme.onPrimaryContainer

@Composable
fun secondaryColor(): Color = MaterialTheme.colorScheme.secondary

@Composable
fun onSecondaryColor(): Color = MaterialTheme.colorScheme.onSecondary

@Composable
fun secondaryContainerColor(): Color = MaterialTheme.colorScheme.secondaryContainer

@Composable
fun onSecondaryContainerColor(): Color = MaterialTheme.colorScheme.onSecondaryContainer

@Composable
fun tertiaryColor(): Color = MaterialTheme.colorScheme.tertiary

@Composable
fun onTertiaryColor(): Color = MaterialTheme.colorScheme.onTertiary

@Composable
fun tertiaryContainerColor(): Color = MaterialTheme.colorScheme.tertiaryContainer

@Composable
fun onTertiaryContainerColor(): Color = MaterialTheme.colorScheme.onTertiaryContainer

@Composable
fun backgroundColor(): Color = MaterialTheme.colorScheme.background

@Composable
fun onBackgroundColor(): Color = MaterialTheme.colorScheme.onBackground

@Composable
fun surfaceColor(): Color = MaterialTheme.colorScheme.surface

@Composable
fun onSurfaceColor(): Color = MaterialTheme.colorScheme.onSurface

@Composable
fun surfaceVariantColor(): Color = MaterialTheme.colorScheme.surfaceVariant

@Composable
fun onSurfaceVariantColor(): Color = MaterialTheme.colorScheme.onSurfaceVariant

@Composable
fun surfaceTintColor(): Color = MaterialTheme.colorScheme.surfaceTint

@Composable
fun inverseSurfaceColor(): Color = MaterialTheme.colorScheme.inverseSurface

@Composable
fun inverseOnSurfaceColor(): Color = MaterialTheme.colorScheme.inverseOnSurface

@Composable
fun inversePrimaryColor(): Color = MaterialTheme.colorScheme.inversePrimary

@Composable
fun errorColor(): Color = MaterialTheme.colorScheme.error

@Composable
fun onErrorColor(): Color = MaterialTheme.colorScheme.onError

@Composable
fun errorContainerColor(): Color = MaterialTheme.colorScheme.errorContainer

@Composable
fun onErrorContainerColor(): Color = MaterialTheme.colorScheme.onErrorContainer

@Composable
fun outlineColor(): Color = MaterialTheme.colorScheme.outline

@Composable
fun outlineVariantColor(): Color = MaterialTheme.colorScheme.outlineVariant

@Composable
fun scrimColor(): Color = MaterialTheme.colorScheme.scrim

@Composable
fun surfaceBrightColor(): Color = MaterialTheme.colorScheme.surfaceBright

@Composable
fun surfaceDimColor(): Color = MaterialTheme.colorScheme.surfaceDim

@Composable
fun surfaceContainerColor(): Color = MaterialTheme.colorScheme.surfaceContainer

@Composable
fun surfaceContainerHighColor(): Color = MaterialTheme.colorScheme.surfaceContainerHigh

@Composable
fun surfaceContainerHighestColor(): Color = MaterialTheme.colorScheme.surfaceContainerHighest

@Composable
fun surfaceContainerLowColor(): Color = MaterialTheme.colorScheme.surfaceContainerLow

@Composable
fun surfaceContainerLowestColor(): Color = MaterialTheme.colorScheme.surfaceContainerLowest

// =============================================
// Extended Colors Extensions
// =============================================

@Composable
fun successColor(): Color = extendedColors.success

@Composable
fun onSuccessColor(): Color = extendedColors.onSuccess

@Composable
fun successContainerColor(): Color = extendedColors.successContainer

@Composable
fun onSuccessContainerColor(): Color = extendedColors.onSuccessContainer

@Composable
fun infoColor(): Color = extendedColors.info

@Composable
fun onInfoColor(): Color = extendedColors.onInfo

@Composable
fun infoContainerColor(): Color = extendedColors.infoContainer

@Composable
fun onInfoContainerColor(): Color = extendedColors.onInfoContainer

@Composable
fun warningColor(): Color = extendedColors.warning

@Composable
fun onWarningColor(): Color = extendedColors.onWarning

@Composable
fun warningContainerColor(): Color = extendedColors.warningContainer

@Composable
fun onWarningContainerColor(): Color = extendedColors.onWarningContainer

@Composable
fun extendedErrorColor(): Color = extendedColors.error

@Composable
fun onExtendedErrorColor(): Color = extendedColors.onError

@Composable
fun extendedErrorContainerColor(): Color = extendedColors.errorContainer

@Composable
fun onExtendedErrorContainerColor(): Color = extendedColors.onErrorContainer

@Composable
fun ratingColor(): Color = extendedColors.rating

@Composable
fun onRatingColor(): Color = extendedColors.onRating

@Composable
fun buttonBackgroundColor(): Color = extendedColors.buttonBackground

@Composable
fun onButtonBackgroundColor(): Color = extendedColors.onButtonBackground

@Composable
fun textSecondaryColor(): Color = extendedColors.textSecondary

@Composable
fun textDisabledColor(): Color = extendedColors.textDisabled

@Composable
fun borderColor(): Color = extendedColors.border

@Composable
fun borderFocusColor(): Color = extendedColors.borderFocus

@Composable
fun extendedSurfaceVariantColor(): Color = extendedColors.surfaceVariant

@Composable
fun onExtendedSurfaceVariantColor(): Color = extendedColors.onSurfaceVariant

@Composable
fun favoriteColor(): Color = extendedColors.favorite

@Composable
fun onFavoriteColor(): Color = extendedColors.onFavorite

// =============================================
// Convenience Extensions for Common UI Patterns
// =============================================

@Composable
fun cardBackgroundColor(): Color = surfaceColor()

@Composable
fun cardContentColor(): Color = onSurfaceColor()

@Composable
fun dialogBackgroundColor(): Color = surfaceColor()

@Composable
fun dialogContentColor(): Color = onSurfaceColor()

@Composable
fun bottomSheetBackgroundColor(): Color = surfaceContainerLowColor()

@Composable
fun bottomSheetContentColor(): Color = onSurfaceColor()

@Composable
fun topAppBarBackgroundColor(): Color = surfaceColor()

@Composable
fun topAppBarContentColor(): Color = onSurfaceColor()

@Composable
fun bottomAppBarBackgroundColor(): Color = surfaceContainerHighColor()

@Composable
fun bottomAppBarContentColor(): Color = onSurfaceColor()

@Composable
fun navigationBarBackgroundColor(): Color = surfaceContainerColor()

@Composable
fun navigationBarContentColor(): Color = onSurfaceVariantColor()

@Composable
fun navigationBarSelectedColor(): Color = primaryColor()

@Composable
fun navigationBarUnselectedColor(): Color = onSurfaceVariantColor()

@Composable
fun dividerColor(): Color = outlineVariantColor()

@Composable
fun rippleColor(): Color = primaryColor().copy(alpha = 0.12f)

@Composable
fun focusedBorderColor(): Color = borderFocusColor()

@Composable
fun unfocusedBorderColor(): Color = borderColor()

@Composable
fun disabledBorderColor(): Color = borderColor().copy(alpha = 0.38f)

@Composable
fun enabledTextColor(): Color = onSurfaceColor()

@Composable
fun disabledTextColor(): Color = textDisabledColor()

@Composable
fun placeholderTextColor(): Color = onSurfaceVariantColor()

@Composable
fun captionTextColor(): Color = textSecondaryColor()

@Composable
fun linkTextColor(): Color = primaryColor()

// =============================================
// Status Colors for Common UI States
// =============================================

@Composable
fun loadingIndicatorColor(): Color = primaryColor()

@Composable
fun progressBarColor(): Color = primaryColor()

@Composable
fun chipSelectedBackgroundColor(): Color = secondaryContainerColor()

@Composable
fun chipSelectedContentColor(): Color = onSecondaryContainerColor()

@Composable
fun chipUnselectedBackgroundColor(): Color = surfaceVariantColor()

@Composable
fun chipUnselectedContentColor(): Color = onSurfaceVariantColor()

@Composable
fun toggleButtonSelectedColor(): Color = primaryColor()

@Composable
fun toggleButtonUnselectedColor(): Color = surfaceVariantColor()

@Composable
fun checkboxSelectedColor(): Color = primaryColor()

@Composable
fun checkboxUnselectedColor(): Color = onSurfaceVariantColor()

@Composable
fun radioButtonSelectedColor(): Color = primaryColor()

@Composable
fun radioButtonUnselectedColor(): Color = onSurfaceVariantColor()

@Composable
fun sliderActiveColor(): Color = primaryColor()

@Composable
fun sliderInactiveColor(): Color = primaryColor().copy(alpha = 0.24f)

@Composable
fun switchThumbSelectedColor(): Color = primaryColor()

@Composable
fun switchThumbUnselectedColor(): Color = outlineColor()

@Composable
fun switchTrackSelectedColor(): Color = primaryColor().copy(alpha = 0.24f)

@Composable
fun switchTrackUnselectedColor(): Color = surfaceVariantColor()

// =============================================
// Food App Specific Colors
// =============================================

@Composable
fun deliveryStatusColor(): Color = infoColor()

@Composable
fun deliveredStatusColor(): Color = successColor()

@Composable
fun cancelledStatusColor(): Color = errorColor()

@Composable
fun preparingStatusColor(): Color = warningColor()

@Composable
fun badgeBackgroundColor(): Color = primaryColor()

@Composable
fun badgeTextColor(): Color = onPrimaryColor()

@Composable
fun priceTextColor(): Color = primaryColor()

@Composable
fun originalPriceTextColor(): Color = textSecondaryColor()

@Composable
fun discountTextColor(): Color = errorColor()

@Composable
fun starRatingColor(): Color = ratingColor()

@Composable
fun emptyStarColor(): Color = onSurfaceVariantColor()

@Composable
fun heartFavoriteColor(): Color = favoriteColor()

@Composable
fun emptyHeartColor(): Color = onSurfaceVariantColor()

@Composable
fun categoryChipSelectedColor(): Color = primaryColor()

@Composable
fun categoryChipUnselectedColor(): Color = surfaceVariantColor()

@Composable
fun offerBackgroundColor(): Color = warningContainerColor()

@Composable
fun offerTextColor(): Color = onWarningContainerColor()

@Composable
fun freeshippingBadgeColor(): Color = successColor()

@Composable
fun popularBadgeColor(): Color = primaryColor()

@Composable
fun newBadgeColor(): Color = errorColor()

@Composable
fun searchBarBackgroundColor(): Color = surfaceContainerHighColor()

@Composable
fun searchBarTextColor(): Color = onSurfaceColor()

@Composable
fun searchBarHintColor(): Color = onSurfaceVariantColor()

@Composable
fun orderTotalBackgroundColor(): Color = primaryContainerColor()

@Composable
fun orderTotalTextColor(): Color = onPrimaryContainerColor()

@Composable
fun paymentMethodSelectedColor(): Color = primaryContainerColor()

@Composable
fun paymentMethodUnselectedColor(): Color = surfaceVariantColor()

@Composable
fun deliveryTrackingActiveColor(): Color = primaryColor()

@Composable
fun deliveryTrackingInactiveColor(): Color = surfaceVariantColor()

@Composable
fun reviewStarFilledColor(): Color = ratingColor()

@Composable
fun reviewStarEmptyColor(): Color = outlineVariantColor()

@Composable
fun quantityButtonBackgroundColor(): Color = primaryContainerColor()

@Composable
fun quantityButtonTextColor(): Color = onPrimaryContainerColor()

@Composable
fun addToCartButtonColor(): Color = primaryColor()

@Composable
fun addToCartButtonTextColor(): Color = onPrimaryColor()

@Composable
fun removeFromCartButtonColor(): Color = errorColor()

@Composable
fun removeFromCartButtonTextColor(): Color = onErrorColor()