package com.app.tastybuds.ui.theme

import androidx.compose.ui.unit.dp

// Card/offer dimension sets shared across features (favorites + home reuse the card
// metrics; checkout + home reuse the offer metrics). Hosted in :core:ui so features
// don't depend on one another.
object FavoritesDimensions {
    val cardImageSize = 60.dp
    val cardCornerRadius = 12.dp
    val cardElevation = 2.dp
    val cardContentPadding = 12.dp
    val cardImageCornerRadius = 8.dp
    val cardSpacing = 12.dp
    val emptyStateIconSize = 64.dp
    val favoriteIconSize = 20.dp
    val ratingIconSize = 12.dp
    val favoriteButtonSize = 32.dp
    val badgePaddingHorizontal = 6.dp
    val badgePaddingVertical = 2.dp
}

object OfferScreenDimensions {
    val offerIconSize = 60.dp
    val offerItemCornerRadius = 8.dp
    val offerItemSpacing = 6.dp
    val offerItemContentPadding = 16.dp
    val offerIconSpacing = 12.dp
}
