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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.ui.checkout.OfferScreenDimensions
import com.app.tastybuds.ui.favorites.FavoritesDimensions
import com.app.tastybuds.ui.theme.Spacing
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.captionTextColor
import com.app.tastybuds.ui.theme.cardBackgroundColor
import com.app.tastybuds.ui.theme.cardContentColor
import com.app.tastybuds.ui.theme.dividerColor
import com.app.tastybuds.ui.theme.emptyStateDescription
import com.app.tastybuds.ui.theme.emptyStateTitle
import com.app.tastybuds.ui.theme.onPrimaryColor
import com.app.tastybuds.ui.theme.popularBadge
import com.app.tastybuds.ui.theme.popularBadgeColor
import com.app.tastybuds.ui.theme.restaurantCuisine
import com.app.tastybuds.ui.theme.restaurantDeliveryTime
import com.app.tastybuds.ui.theme.restaurantName
import com.app.tastybuds.ui.theme.restaurantRating
import com.app.tastybuds.ui.theme.starRatingColor
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.app.tastybuds.util.ui.AppTopBar
import com.app.tastybuds.util.ui.ErrorScreen
import com.app.tastybuds.util.ui.LoadingScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

object AllRestaurantsDimensions {
    val cardHeight = 140.dp
    val cardCornerRadius = 16.dp
    val cardElevation = 4.dp
    val cardPressedElevation = 8.dp
    val cardContentPadding = 16.dp
    val restaurantImageSize = 108.dp
    val imageCornerRadius = 12.dp
    val badgeCornerRadius = 6.dp
    val ratingIconSize = 16.dp
    val separatorSpacing = 8.dp
    val metadataSpacing = 16.dp
}

@Composable
fun AllRestaurantsScreen(
    onBackClick: () -> Unit = {},
    onRestaurantClick: (String) -> Unit = {},
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
                title = stringResource(R.string.all_restaurants),
                onBackClick = onBackClick
            )

            when {
                uiState.isLoading -> {
                    LoadingScreen(message = stringResource(R.string.loading_restaurants))
                }

                uiState.error != null -> {
                    ErrorScreen(
                        title = uiState.error ?: stringResource(R.string.unknown_error),
                        onRetryClick = { viewModel.retry() }
                    )
                }

                else -> {
                    RestaurantsContent(
                        restaurants = uiState.recommendedRestaurants,
                        onRestaurantClick = onRestaurantClick
                    )
                }
            }
        }
    }
}

@Composable
private fun RestaurantsContent(
    restaurants: List<Restaurant>,
    onRestaurantClick: (String) -> Unit
) {
    if (restaurants.isEmpty()) {
        EmptyStateContent()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(FavoritesDimensions.cardSpacing)
        ) {
            items(
                items = restaurants,
                key = { restaurant -> restaurant.id }
            ) { restaurant ->
                RestaurantListItemCard(
                    restaurant = restaurant,
                    onClick = { onRestaurantClick(restaurant.id) }
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
                text = stringResource(R.string.no_restaurants_available),
                style = emptyStateTitle(),
                color = captionTextColor(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            Text(
                text = stringResource(R.string.check_back_later_for_new_restaurants),
                style = emptyStateDescription(),
                color = textSecondaryColor(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RestaurantListItemCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(AllRestaurantsDimensions.cardHeight)
            .clickable { onClick() }
            .semantics {
                contentDescription = "Restaurant: ${restaurant.name}"
            },
        shape = RoundedCornerShape(AllRestaurantsDimensions.cardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor()
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = AllRestaurantsDimensions.cardElevation,
            pressedElevation = AllRestaurantsDimensions.cardPressedElevation
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(AllRestaurantsDimensions.cardContentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RestaurantImage(
                restaurant = restaurant,
                modifier = Modifier.size(AllRestaurantsDimensions.restaurantImageSize)
            )

            Spacer(modifier = Modifier.width(Spacing.medium))

            RestaurantInfo(
                restaurant = restaurant,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun RestaurantImage(
    restaurant: Restaurant,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        GlideImage(
            model = restaurant.imageUrl,
            contentDescription = stringResource(R.string.cd_restaurant_image),
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(AllRestaurantsDimensions.imageCornerRadius)),
            contentScale = ContentScale.Crop,
            failure = placeholder(R.drawable.default_food),
            loading = placeholder(R.drawable.default_food)
        )

        restaurant.badge?.let { badge ->
            RestaurantBadge(
                text = badge,
                modifier = Modifier
                    .padding(OfferScreenDimensions.offerItemSpacing)
                    .align(Alignment.TopStart)
            )
        }
    }
}

@Composable
private fun RestaurantBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AllRestaurantsDimensions.badgeCornerRadius),
        color = popularBadgeColor()
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = Spacing.small,
                vertical = Spacing.xs
            ),
            style = popularBadge(),
            color = onPrimaryColor()
        )
    }
}

@Composable
private fun RestaurantInfo(
    restaurant: Restaurant,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        RestaurantBasicInfo(restaurant = restaurant)

        Spacer(modifier = Modifier.height(FavoritesDimensions.cardSpacing))

        RestaurantMetadata(restaurant = restaurant)
    }
}

@Composable
private fun RestaurantBasicInfo(
    restaurant: Restaurant
) {
    Column {
        Text(
            text = restaurant.name,
            style = restaurantName(),
            color = cardContentColor(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        Text(
            text = restaurant.cuisine,
            style = restaurantCuisine(),
            color = textSecondaryColor(),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RestaurantMetadata(
    restaurant: Restaurant
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RatingSection(
            rating = restaurant.rating,
            reviewCount = restaurant.reviewCount
        )

        Spacer(modifier = Modifier.width(AllRestaurantsDimensions.metadataSpacing))

        DeliveryInfoSection(
            deliveryTime = restaurant.deliveryTime,
            distance = restaurant.distance
        )
    }
}

@Composable
private fun RatingSection(
    rating: Float,
    reviewCount: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = stringResource(R.string.cd_rating_star),
            modifier = Modifier.size(AllRestaurantsDimensions.ratingIconSize),
            tint = starRatingColor()
        )

        Spacer(modifier = Modifier.width(Spacing.xs))

        Text(
            text = "$rating ($reviewCount)",
            style = restaurantRating(),
            color = cardContentColor()
        )
    }
}

@Composable
private fun DeliveryInfoSection(
    deliveryTime: String,
    distance: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = deliveryTime,
            style = restaurantDeliveryTime(),
            color = textSecondaryColor()
        )

        Spacer(modifier = Modifier.width(AllRestaurantsDimensions.separatorSpacing))

        Text(
            text = "•",
            style = restaurantDeliveryTime(),
            color = dividerColor()
        )

        Spacer(modifier = Modifier.width(AllRestaurantsDimensions.separatorSpacing))

        Text(
            text = distance,
            style = restaurantDeliveryTime(),
            color = textSecondaryColor()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AllRestaurantsScreenPreview() {
    MaterialTheme {
        AllRestaurantsScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun RestaurantListItemCardPreview() {
    MaterialTheme {
        RestaurantListItemCard(
            restaurant = Restaurant(
                id = "1",
                name = "Pizza Palace",
                cuisine = "Italian, Pizza, Fast Food",
                rating = 4.5f,
                reviewCount = 150,
                deliveryTime = "25-35 min",
                distance = "1.2 km",
                imageUrl = "",
                badge = "POPULAR"
            ),
            onClick = {}
        )
    }
}