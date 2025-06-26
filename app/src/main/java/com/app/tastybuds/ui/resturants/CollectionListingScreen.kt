package com.app.tastybuds.ui.resturants

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.R
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.cardBackgroundColor
import com.app.tastybuds.ui.theme.cardContentColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.starRatingColor
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@Composable
fun CollectionListingScreen(
    collectionTitle: String,
    restaurantIds: String,
    onBackClick: () -> Unit = {},
    onRestaurantClick: (String) -> Unit = {},
    viewModel: CollectionListingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(restaurantIds) {
        viewModel.loadCollectionRestaurants(restaurantIds)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor())
    ) {
        TopBar(
            title = collectionTitle,
            onBackClick = onBackClick
        )

        when {
            uiState.isLoading -> {
                LoadingContent()
            }

            uiState.error != null -> {
                ErrorContent(
                    error = uiState.error ?: stringResource(R.string.unknown_error),
                    onRetry = {
                        viewModel.retry(restaurantIds)
                    }
                )
            }

            uiState.restaurants.isEmpty() -> {
                EmptyContent()
            }

            else -> {
                RestaurantsList(
                    restaurants = uiState.restaurants,
                    onRestaurantClick = onRestaurantClick
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    title: String,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No restaurants found",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Try again later",
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun RestaurantsList(
    restaurants: List<Restaurant>,
    onRestaurantClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "${restaurants.size} restaurants found",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(
            items = restaurants,
            key = { restaurant -> restaurant.id }
        ) { restaurant ->
            RestaurantCard(
                restaurant = restaurant,
                onClick = { onRestaurantClick(restaurant.id) }
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor()
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(80.dp)
            ) {
                GlideImage(
                    model = restaurant.imageUrl,
                    contentDescription = stringResource(R.string.cd_restaurant_image),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    failure = placeholder(R.drawable.default_food),
                    loading = placeholder(R.drawable.default_food)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.cd_rating_star),
                        modifier = Modifier.size(14.dp),
                        tint = starRatingColor()
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "${restaurant.rating}",
                        style = MaterialTheme.typography.bodySmall,
                        color = cardContentColor()
                    )

                    Text(
                        text = " (${restaurant.reviewCount})",
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondaryColor()
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = restaurant.deliveryTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondaryColor()
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = restaurant.distance,
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondaryColor()
                    )

                    restaurant.badge?.let { badge ->
                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = primaryColor().copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = badge,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = primaryColor()
                            )
                        }
                    }
                }
            }
        }
    }
}