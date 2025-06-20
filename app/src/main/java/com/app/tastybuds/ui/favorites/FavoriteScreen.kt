package com.app.tastybuds.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.app.tastybuds.ui.theme.PrimaryColor
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
                    color = PrimaryColor
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
                    text = "Items",
                    fontSize = 16.sp,
                    fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTabIndex == 0) PrimaryColor else Color.Gray
                )
                if (favoriteItemsCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (selectedTabIndex == 0) PrimaryColor else Color.Gray,
                                shape = CircleShape
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = favoriteItemsCount.toString(),
                            color = Color.White,
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
                    text = "Restaurants",
                    fontSize = 16.sp,
                    fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                    color = if (selectedTabIndex == 1) PrimaryColor else Color.Gray
                )
                if (favoriteRestaurantsCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (selectedTabIndex == 1) PrimaryColor else Color.Gray,
                                shape = CircleShape
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = favoriteRestaurantsCount.toString(),
                            color = Color.White,
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
                CircularProgressIndicator(color = PrimaryColor)
            }
        }

        favoriteItems.isEmpty() -> {
            EmptyFavoritesState(
                title = "No favorite items yet",
                description = "Start adding your favorite dishes to see them here!"
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
                CircularProgressIndicator(color = PrimaryColor)
            }
        }

        favoriteRestaurants.isEmpty() -> {
            EmptyFavoritesState(
                title = "No favorite restaurants yet",
                description = "Start adding your favorite restaurants to see them here!"
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🔧 FIXED: Use actual restaurant image
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
                // 🔧 FIXED: Use actual restaurant name
                Text(
                    text = favorite.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 🔧 FIXED: Use actual cuisine info
                Text(
                    text = favorite.cuisine,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 🔧 FIXED: Show actual rating
                    if (favorite.rating > 0) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            modifier = Modifier.size(12.dp),
                            tint = Color(0xFFFFC107)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = favorite.ratingText,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // 🔧 FIXED: Show actual delivery info
                    Text(
                        text = favorite.deliveryInfo,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 🔧 FIXED: Show actual price range
                Text(
                    text = favorite.priceRange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            }

            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Remove from favorites",
                    tint = Color.Red,
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🔧 FIXED: Use actual menu item image
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
                // 🔧 FIXED: Use actual menu item name
                Text(
                    text = favorite.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 🔧 FIXED: Use actual restaurant name
                Text(
                    text = favorite.restaurantName,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 🔧 FIXED: Show actual rating
                    if (favorite.rating > 0) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            modifier = Modifier.size(12.dp),
                            tint = Color(0xFFFFC107)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = favorite.ratingText,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 🔧 FIXED: Show actual price
                    Text(
                        text = favorite.priceText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
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
                    tint = Color.Red,
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
                contentDescription = "No favorites",
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}


fun formatDate(dateString: String): String {
    // Simple date formatting - you can enhance this
    return if (dateString.isNotBlank()) {
        try {
            dateString.substring(0, 10) // Extract YYYY-MM-DD
        } catch (e: Exception) {
            "recently"
        }
    } else {
        "recently"
    }
}