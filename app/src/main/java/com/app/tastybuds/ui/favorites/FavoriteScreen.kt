package com.app.tastybuds.ui.favorites

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tastybuds.R
import com.app.tastybuds.data.model.FavoriteMenuItemUi
import com.app.tastybuds.data.model.FavoriteRestaurantUi
import com.app.tastybuds.ui.login.LoginViewModel
import com.app.tastybuds.ui.theme.*
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@Composable
fun FavoriteScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel(),
    onRestaurantClick: (String) -> Unit = {},
    onMenuItemClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val userIdFlow by loginViewModel.getUserId().collectAsState(initial = "")
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val userId = userIdFlow ?: ""

    LaunchedEffect(userIdFlow) {
        if (userId.isNotBlank())
            viewModel.loadUserFavoritesWithDetails(userId)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        FavoritesTabRow(
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it },
            favoriteItemsCount = uiState.favoriteMenuItems.size,
            favoriteRestaurantsCount = uiState.favoriteRestaurants.size
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTabIndex) {
            0 -> FavoriteItemsTab(
                favoriteItems = uiState.favoriteMenuItems,
                isLoading = uiState.isLoading,
                onItemClick = onMenuItemClick,
                onRemoveClick = { favoriteId ->
                    viewModel.removeFavorite(favoriteId, userId)
                }
            )

            1 -> FavoriteRestaurantsTab(
                favoriteRestaurants = uiState.favoriteRestaurants,
                isLoading = uiState.isLoading,
                onRestaurantClick = onRestaurantClick,
                onRemoveClick = { favoriteId ->
                    viewModel.removeFavorite(favoriteId, userId)
                }
            )
        }
    }
}

@Composable
fun FavoritesTabRow(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    favoriteItemsCount: Int,
    favoriteRestaurantsCount: Int
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        indicator = { tabPositions ->
            if (selectedTabIndex < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = primaryColor()
                )
            }
        }
    ) {
        Tab(
            selected = selectedTabIndex == 0,
            onClick = { onTabSelected(0) }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.favorite_item_tab),
                    fontSize = 16.sp,
                    fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTabIndex == 0) primaryColor() else textSecondaryColor()
                )
                if (favoriteItemsCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (selectedTabIndex == 0) primaryColor() else onSurfaceVariantColor(),
                                shape = CircleShape
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = favoriteItemsCount.toString(),
                            color = if (selectedTabIndex == 0) onPrimaryColor() else onSurfaceColor(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Tab(
            selected = selectedTabIndex == 1,
            onClick = { onTabSelected(1) }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.restaurants),
                    fontSize = 16.sp,
                    fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTabIndex == 1) primaryColor() else textSecondaryColor()
                )
                if (favoriteRestaurantsCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (selectedTabIndex == 1) primaryColor() else onSurfaceVariantColor(),
                                shape = CircleShape
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = favoriteRestaurantsCount.toString(),
                            color = if (selectedTabIndex == 1) onPrimaryColor() else onSurfaceColor(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteItemsTab(
    favoriteItems: List<FavoriteMenuItemUi>,
    isLoading: Boolean,
    onItemClick: (String) -> Unit,
    onRemoveClick: (Int) -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryColor())
            }
        }

        favoriteItems.isEmpty() -> {
            EmptyFavoritesState(
                title = stringResource(R.string.no_favorite_items_yet),
                description = stringResource(R.string.start_adding_your_favorite_dishes_to_see_them_here)
            )
        }

        else -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(favoriteItems) { favorite ->
                    FavoriteMenuItemCard(
                        favorite = favorite,
                        onClick = {
                            onItemClick(favorite.menuItemId)
                        },
                        onRemoveClick = { onRemoveClick(favorite.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteRestaurantsTab(
    favoriteRestaurants: List<FavoriteRestaurantUi>,
    isLoading: Boolean,
    onRestaurantClick: (String) -> Unit,
    onRemoveClick: (Int) -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryColor())
            }
        }

        favoriteRestaurants.isEmpty() -> {
            EmptyFavoritesState(
                title = stringResource(R.string.no_favorite_restaurants_yet),
                description = stringResource(R.string.start_adding_your_favorite_restaurants_to_see_them_here)
            )
        }

        else -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(favoriteRestaurants) { favorite ->
                    FavoriteRestaurantCard(
                        favorite = favorite,
                        onClick = {
                            onRestaurantClick(favorite.restaurantId)
                        },
                        onRemoveClick = { onRemoveClick(favorite.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FavoriteRestaurantCard(
    favorite: FavoriteRestaurantUi,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                model = if (favorite.hasValidImage) favorite.imageUrl else null,
                contentDescription = favorite.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                failure = placeholder(R.drawable.default_food),
                loading = placeholder(R.drawable.default_food)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = favorite.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = onSurfaceColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = favorite.cuisine,
                    fontSize = 14.sp,
                    color = textSecondaryColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (favorite.rating > 0) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(id = R.string.rating),
                            modifier = Modifier.size(12.dp),
                            tint = ratingColor()
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = favorite.ratingText,
                            fontSize = 12.sp,
                            color = textSecondaryColor()
                        )

                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Text(
                        text = favorite.deliveryInfo,
                        fontSize = 12.sp,
                        color = textSecondaryColor()
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = favorite.priceRange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor()
                )
            }

            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Remove from favorites",
                    tint = favoriteColor(),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FavoriteMenuItemCard(
    favorite: FavoriteMenuItemUi,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                model = if (favorite.hasValidImage) favorite.imageUrl else null,
                contentDescription = favorite.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                failure = placeholder(R.drawable.default_food),
                loading = placeholder(R.drawable.default_food)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = favorite.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = onSurfaceColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = favorite.restaurantName,
                    fontSize = 14.sp,
                    color = textSecondaryColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (favorite.rating > 0) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            modifier = Modifier.size(12.dp),
                            tint = ratingColor()
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = favorite.ratingText,
                            fontSize = 12.sp,
                            color = textSecondaryColor()
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = favorite.priceText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor()
                    )
                }
            }

            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Remove from favorites",
                    tint = favoriteColor(),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyFavoritesState(
    title: String,
    description: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = stringResource(id = R.string.no_favorites),
                modifier = Modifier.size(64.dp),
                tint = onSurfaceVariantColor()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = onSurfaceColor(),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = textSecondaryColor(),
                textAlign = TextAlign.Center
            )
        }
    }
}