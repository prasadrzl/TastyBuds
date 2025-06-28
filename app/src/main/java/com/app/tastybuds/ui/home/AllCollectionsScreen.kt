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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.app.tastybuds.domain.model.Collection
import com.app.tastybuds.ui.checkout.OfferScreenDimensions
import com.app.tastybuds.ui.favorites.FavoritesDimensions
import com.app.tastybuds.ui.theme.Spacing
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.badgeText
import com.app.tastybuds.ui.theme.bodyMedium
import com.app.tastybuds.ui.theme.captionTextColor
import com.app.tastybuds.ui.theme.cardBackgroundColor
import com.app.tastybuds.ui.theme.cardContentColor
import com.app.tastybuds.ui.theme.cardTitle
import com.app.tastybuds.ui.theme.emptyStateDescription
import com.app.tastybuds.ui.theme.emptyStateTitle
import com.app.tastybuds.ui.theme.onSuccessColor
import com.app.tastybuds.ui.theme.successColor
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.app.tastybuds.util.ui.AppTopBar
import com.app.tastybuds.util.ui.ErrorScreen
import com.app.tastybuds.util.ui.LoadingScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

object AllCollectionsDimensions {
    val cardHeight = 120.dp
    val cardCornerRadius = 16.dp
    val cardElevation = 4.dp
    val cardPressedElevation = 8.dp
    val cardContentPadding = 16.dp
    val collectionImageSize = 88.dp
    val imageCornerRadius = 12.dp
    val badgeCornerRadius = 6.dp
}

@Composable
fun AllCollectionsScreen(
    onBackClick: () -> Unit = {},
    onCollectionClick: (String) -> Unit = {},
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
                title = stringResource(id = R.string.all_collections),
                onBackClick = onBackClick
            )

            when {
                uiState.isLoading -> LoadingScreen(message = stringResource(R.string.loading))

                uiState.error != null -> {
                    ErrorScreen(
                        title = uiState.error ?: stringResource(R.string.unknown_error),
                        onRetryClick = { viewModel.retry() }
                    )
                }

                else -> {
                    CollectionsContent(
                        collections = uiState.collections,
                        onCollectionClick = onCollectionClick
                    )
                }
            }
        }
    }
}


@Composable
private fun CollectionsContent(
    collections: List<Collection>,
    onCollectionClick: (String) -> Unit
) {
    if (collections.isEmpty()) {
        EmptyStateContent()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(FavoritesDimensions.cardSpacing)
        ) {
            items(
                items = collections,
                key = { collection -> collection.id }
            ) { collection ->
                CollectionItemCard(
                    collection = collection,
                    onClick = { onCollectionClick(collection.id) }
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
                text = stringResource(R.string.no_collections_available),
                style = emptyStateTitle(),
                color = captionTextColor(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            Text(
                text = stringResource(R.string.check_back_later_for_new_collections),
                style = emptyStateDescription(),
                color = textSecondaryColor(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CollectionItemCard(
    collection: Collection,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(AllCollectionsDimensions.cardHeight)
            .clickable { onClick() }
            .semantics {
                contentDescription = "Collection: ${collection.title}"
            },
        shape = RoundedCornerShape(AllCollectionsDimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor()
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AllCollectionsDimensions.cardElevation,
            pressedElevation = AllCollectionsDimensions.cardPressedElevation
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(AllCollectionsDimensions.cardContentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CollectionImage(
                collection = collection,
                modifier = Modifier.size(AllCollectionsDimensions.collectionImageSize)
            )

            Spacer(modifier = Modifier.width(Spacing.medium))

            CollectionInfo(
                collection = collection,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun CollectionImage(
    collection: Collection,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        GlideImage(
            model = collection.imageUrl,
            contentDescription = stringResource(R.string.cd_food_image),
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(AllCollectionsDimensions.imageCornerRadius)),
            contentScale = ContentScale.Crop,
            failure = placeholder(R.drawable.default_food),
            loading = placeholder(R.drawable.default_food)
        )

        collection.badge?.let { badge ->
            BadgeLabel(
                text = badge,
                modifier = Modifier
                    .padding(OfferScreenDimensions.offerItemSpacing)
                    .align(Alignment.TopStart)
            )
        }
    }
}

@Composable
private fun BadgeLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AllCollectionsDimensions.badgeCornerRadius),
        color = successColor()
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = Spacing.small,
                vertical = Spacing.xs
            ),
            style = badgeText(),
            color = onSuccessColor()
        )
    }
}

@Composable
private fun CollectionInfo(
    collection: Collection,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = collection.title,
            style = cardTitle(),
            color = cardContentColor(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        Text(
            text = collection.subtitle,
            style = bodyMedium(),
            color = textSecondaryColor(),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AllCollectionsScreenPreview() {
    MaterialTheme {
        AllCollectionsScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionItemCardPreview() {
    MaterialTheme {
        CollectionItemCard(
            collection = Collection(
                id = "1",
                title = "Popular Dishes",
                subtitle = "Most ordered items in your area",
                imageUrl = "",
                badge = "NEW"
            ),
            onClick = {}
        )
    }
}