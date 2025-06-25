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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.R
import com.app.tastybuds.domain.model.Deal
import com.app.tastybuds.ui.theme.Spacing
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.badgeText
import com.app.tastybuds.ui.theme.captionTextColor
import com.app.tastybuds.ui.theme.cardBackgroundColor
import com.app.tastybuds.ui.theme.cardContentColor
import com.app.tastybuds.ui.theme.discountBadge
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
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.app.tastybuds.util.ui.AppTopBar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

object AllDealsDimensions {
    val gridColumns = 2
    val cardHeight = 220.dp
    val cardCornerRadius = 16.dp
    val cardElevation = 4.dp
    val cardPressedElevation = 8.dp
    val cardContentPadding = 12.dp
    val imageHeight = 130.dp
    val badgeCornerRadius = 6.dp
    val loadingIndicatorSize = 48.dp
    val gridSpacingHorizontal = 12.dp
    val gridSpacingVertical = 16.dp
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

@Composable
private fun DealGridItemCard(
    deal: Deal,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(AllDealsDimensions.cardHeight)
            .clickable { onClick() }
            .semantics {
                contentDescription = "Deal: ${deal.title}"
            },
        shape = RoundedCornerShape(AllDealsDimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor()
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AllDealsDimensions.cardElevation,
            pressedElevation = AllDealsDimensions.cardPressedElevation
        )
    ) {
        Column {
            DealImageSection(
                deal = deal,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AllDealsDimensions.imageHeight)
            )

            DealInfoSection(
                deal = deal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AllDealsDimensions.cardContentPadding)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun DealImageSection(
    deal: Deal,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        GlideImage(
            model = deal.imageUrl,
            contentDescription = stringResource(R.string.cd_food_image),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            failure = placeholder(R.drawable.default_food),
            loading = placeholder(R.drawable.default_food)
        )

        deal.badge?.let { badge ->
            BadgeLabel(
                text = badge,
                backgroundColor = newBadgeColor(),
                textColor = onErrorColor(),
                modifier = Modifier
                    .padding(Spacing.small)
                    .align(Alignment.TopStart)
            )
        }

        deal.discountPercentage?.let { percentage ->
            DiscountBadge(
                percentage = percentage,
                modifier = Modifier
                    .padding(Spacing.small)
                    .align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
private fun BadgeLabel(
    text: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AllDealsDimensions.badgeCornerRadius),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = Spacing.small,
                vertical = Spacing.xs
            ),
            style = badgeText(),
            color = textColor
        )
    }
}

@Composable
private fun DiscountBadge(
    percentage: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AllDealsDimensions.badgeCornerRadius),
        color = discountTextColor()
    ) {
        Text(
            text = "-$percentage%",
            modifier = Modifier.padding(
                horizontal = Spacing.small,
                vertical = Spacing.xs
            ),
            style = discountBadge(),
            color = onErrorColor()
        )
    }
}

@Composable
private fun DealInfoSection(
    deal: Deal,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = deal.title,
            style = foodItemName(),
            color = cardContentColor(),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(Spacing.small))

        PriceSection(
            price = deal.price,
            originalPrice = deal.originalPrice
        )
    }
}

@Composable
private fun PriceSection(
    price: String,
    originalPrice: String? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = price,
            style = foodItemPrice(),
            color = priceTextColor()
        )

        originalPrice?.let { original ->
            Spacer(modifier = Modifier.width(Spacing.small))
            Text(
                text = original,
                style = foodItemOriginalPrice(),
                color = originalPriceTextColor()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AllDealsScreenPreview() {
    MaterialTheme {
        AllDealsScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun DealGridItemCardPreview() {
    MaterialTheme {
        DealGridItemCard(
            deal = Deal(
                id = "1",
                title = "Pizza Margherita Special",
                price = "$12.99",
                originalPrice = "$16.99",
                imageUrl = "",
                badge = "HOT",
                discountPercentage = 25,
                menuItemId = "menu_1"
            ),
            onClick = {}
        )
    }
}