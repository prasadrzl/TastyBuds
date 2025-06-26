package com.app.tastybuds.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.R
import com.app.tastybuds.domain.model.Deal
import com.app.tastybuds.ui.theme.Spacing
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.captionTextColor
import com.app.tastybuds.ui.theme.cardBackgroundColor
import com.app.tastybuds.ui.theme.cardContentColor
import com.app.tastybuds.ui.theme.discountTextColor
import com.app.tastybuds.ui.theme.emptyStateDescription
import com.app.tastybuds.ui.theme.emptyStateTitle
import com.app.tastybuds.ui.theme.foodItemName
import com.app.tastybuds.ui.theme.foodItemOriginalPrice
import com.app.tastybuds.ui.theme.foodItemPrice
import com.app.tastybuds.ui.theme.loadingIndicatorColor
import com.app.tastybuds.ui.theme.newBadgeColor
import com.app.tastybuds.ui.theme.onErrorColor
import com.app.tastybuds.ui.theme.originalPriceTextColor
import com.app.tastybuds.ui.theme.priceTextColor
import com.app.tastybuds.ui.theme.starRatingColor
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.app.tastybuds.util.ui.AppTopBar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import java.util.Locale

object AllDealsDimensions {
    val gridColumns = 2
    val cardMinHeight = 260.dp
    val cardCornerRadius = 16.dp
    val cardElevation = 4.dp
    val cardPressedElevation = 8.dp
    val cardContentPadding = 12.dp
    val imageHeight = 130.dp
    val badgeCornerRadius = 6.dp
    val loadingIndicatorSize = 48.dp
    val gridSpacingHorizontal = 12.dp
    val gridSpacingVertical = 16.dp
    val starIconSize = 12.dp
    val discountBadgeRadius = 4.dp
}

@Composable
fun AllDealsScreen(
    onBackClick: () -> Unit = {},
    onDealClick: (String, String) -> Unit = { _, _ -> },
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppTopBar(
                title = stringResource(R.string.all_deals),
                onBackClick = onBackClick
            )

            when {
                uiState.isLoading -> {
                    LoadingContent()
                }

                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error ?: stringResource(R.string.unknown_error),
                        onRetry = { viewModel.retry() }
                    )
                }

                else -> {
                    DealsContent(
                        deals = uiState.deals,
                        onDealClick = onDealClick
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "Loading deals" },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = loadingIndicatorColor(),
            modifier = Modifier.size(AllDealsDimensions.loadingIndicatorSize)
        )
    }
}

@Composable
private fun DealsContent(
    deals: List<Deal>,
    onDealClick: (String, String) -> Unit = { _, _ -> },
) {
    if (deals.isEmpty()) {
        EmptyStateContent()
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(AllDealsDimensions.gridColumns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(AllDealsDimensions.gridSpacingHorizontal),
            verticalArrangement = Arrangement.spacedBy(AllDealsDimensions.gridSpacingVertical)
        ) {
            items(
                items = deals,
                key = { deal -> deal.id }
            ) { deal ->
                DealGridItemCard(
                    deal = deal,
                    onClick = { onDealClick(deal.id, deal.menuItemId) }
                )
            }
        }
    }
}

@Composable
private fun EmptyStateContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.large),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.no_deals_available),
                style = emptyStateTitle(),
                color = captionTextColor(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            Text(
                text = stringResource(R.string.check_back_later_for_new_deals),
                style = emptyStateDescription(),
                color = textSecondaryColor(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun DealGridItemCard(
    deal: Deal,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AllDealsDimensions.cardMinHeight)
            .clickable { onClick() },
        shape = RoundedCornerShape(AllDealsDimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor()
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AllDealsDimensions.cardElevation,
            pressedElevation = AllDealsDimensions.cardPressedElevation
        )
    ) {
        Column(
            modifier = Modifier.padding(AllDealsDimensions.cardContentPadding)
        ) {
            Box {
                GlideImage(
                    model = deal.imageUrl,
                    contentDescription = deal.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AllDealsDimensions.imageHeight)
                        .clip(RoundedCornerShape(AllDealsDimensions.cardCornerRadius)),
                    contentScale = ContentScale.Crop,
                    failure = placeholder(R.drawable.default_food)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.small),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    deal.badges.firstOrNull()?.let { badge ->
                        Surface(
                            shape = RoundedCornerShape(AllDealsDimensions.badgeCornerRadius),
                            color = try {
                                Color(badge.backgroundColor.toColorInt())
                            } catch (e: Exception) {
                                newBadgeColor()
                            }
                        ) {
                            Text(
                                text = badge.text,
                                modifier = Modifier.padding(
                                    horizontal = Spacing.small,
                                    vertical = Spacing.xs
                                ),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color.White
                            )
                        }
                    }

                    if (deal.discountPercentage > 0) {
                        Surface(
                            shape = RoundedCornerShape(AllDealsDimensions.discountBadgeRadius),
                            color = discountTextColor()
                        ) {
                            Text(
                                text = "-${deal.discountPercentage}%",
                                modifier = Modifier.padding(
                                    horizontal = Spacing.small,
                                    vertical = Spacing.xs
                                ),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = onErrorColor()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.small))

            Column {
                Text(
                    text = deal.title,
                    style = foodItemName(),
                    color = cardContentColor(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (deal.description.isNotBlank()) {
                    Text(
                        text = deal.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondaryColor(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.small))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.cd_rating_star),
                        modifier = Modifier.size(AllDealsDimensions.starIconSize),
                        tint = starRatingColor()
                    )

                    Spacer(modifier = Modifier.width(Spacing.xs))

                    Text(
                        text = String.format(Locale.getDefault(), "%.1f", deal.rating),
                        style = MaterialTheme.typography.bodySmall,
                        color = cardContentColor(),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.width(Spacing.small))

                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondaryColor()
                    )

                    Spacer(modifier = Modifier.width(Spacing.xs))

                    Text(
                        text = deal.deliveryTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondaryColor()
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.small))

                PriceSection(
                    salePrice = deal.salePrice,
                    originalPrice = deal.originalPrice,
                    discountPercentage = deal.discountPercentage
                )
            }
        }
    }
}

@Composable
private fun PriceSection(
    salePrice: String,
    originalPrice: String,
    discountPercentage: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Text(
            text = salePrice,
            style = foodItemPrice(),
            color = priceTextColor(),
            fontWeight = FontWeight.Bold
        )

        if (originalPrice.isNotBlank() && originalPrice != salePrice) {
            Text(
                text = originalPrice,
                style = foodItemOriginalPrice(),
                color = originalPriceTextColor(),
                textDecoration = TextDecoration.LineThrough
            )
        }

        if (discountPercentage > 0) {
            Text(
                text = "-$discountPercentage%",
                style = MaterialTheme.typography.labelSmall,
                color = discountTextColor(),
                modifier = Modifier
                    .clip(RoundedCornerShape(AllDealsDimensions.discountBadgeRadius))
                    .padding(horizontal = Spacing.xs, vertical = 2.dp)
            )
        }
    }
}