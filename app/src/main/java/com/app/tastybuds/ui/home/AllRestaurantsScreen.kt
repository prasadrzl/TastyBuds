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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.R
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.ui.theme.*
import com.app.tastybuds.util.ui.AppTopBar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

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
                    LoadingContent()
                }

                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error ?: stringResource(R.string.unknown_error),
                        onRetry = { viewModel.retry() }
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
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { contentDescription = "Loading restaurants" },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = loadingIndicatorColor(),
            modifier = Modifier.size(48.dp)
        )
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.no_restaurants_available),
                style = MaterialTheme.typography.headlineSmall,
                color = captionTextColor(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.check_back_later_for_new_restaurants),
                style = MaterialTheme.typography.bodyMedium,
                color = textSecondaryColor(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun RestaurantListItemCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() }
            .semantics {
                contentDescription = "Restaurant: ${restaurant.name}"
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor()
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RestaurantImage(
                restaurant = restaurant,
                modifier = Modifier.size(108.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

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
            contentDescription = null, // Decorative image
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            failure = placeholder(R.drawable.default_food),
            loading = placeholder(R.drawable.default_food)
        )

        restaurant.badge?.let { badge ->
            RestaurantBadge(
                text = badge,
                modifier = Modifier
                    .padding(6.dp)
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
        shape = RoundedCornerShape(6.dp),
        color = popularBadgeColor()
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            ),
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

        Spacer(modifier = Modifier.height(12.dp))

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
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = cardContentColor(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = restaurant.cuisine,
            style = MaterialTheme.typography.bodyMedium,
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

        Spacer(modifier = Modifier.width(16.dp))

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
            contentDescription = stringResource(R.string.rating),
            modifier = Modifier.size(16.dp),
            tint = starRatingColor()
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "$rating ($reviewCount)",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium
            ),
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
            style = MaterialTheme.typography.labelMedium,
            color = textSecondaryColor()
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "•",
            style = MaterialTheme.typography.labelMedium,
            color = dividerColor()
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = distance,
            style = MaterialTheme.typography.labelMedium,
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