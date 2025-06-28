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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tastybuds.R
import com.app.tastybuds.data.model.FavoriteMenuItemUi
import com.app.tastybuds.data.model.FavoriteRestaurantUi
import com.app.tastybuds.ui.login.LoginViewModel
import com.app.tastybuds.ui.theme.Spacing
import com.app.tastybuds.ui.theme.badgeText
import com.app.tastybuds.ui.theme.bodyMedium
import com.app.tastybuds.ui.theme.cardBackgroundColor
import com.app.tastybuds.ui.theme.emptyStateDescription
import com.app.tastybuds.ui.theme.emptyStateTitle
import com.app.tastybuds.ui.theme.favoriteColor
import com.app.tastybuds.ui.theme.foodItemName
import com.app.tastybuds.ui.theme.foodItemPrice
import com.app.tastybuds.ui.theme.onPrimaryColor
import com.app.tastybuds.ui.theme.onSurfaceColor
import com.app.tastybuds.ui.theme.onSurfaceVariantColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.ratingColor
import com.app.tastybuds.ui.theme.restaurantCuisine
import com.app.tastybuds.ui.theme.restaurantDeliveryTime
import com.app.tastybuds.ui.theme.restaurantName
import com.app.tastybuds.ui.theme.restaurantRating
import com.app.tastybuds.ui.theme.subTitle
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.app.tastybuds.util.ui.LoadingScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

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

        Spacer(modifier = Modifier.height(Spacing.medium))

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
                modifier = Modifier.padding(Spacing.medium)
            ) {
                Text(
                    text = stringResource(id = R.string.favorite_item_tab),
                    style = if (selectedTabIndex == 0) subTitle() else bodyMedium(),
                    color = if (selectedTabIndex == 0) primaryColor() else textSecondaryColor()
                )
                if (favoriteItemsCount > 0) {
                    Spacer(modifier = Modifier.width(Spacing.small))
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (selectedTabIndex == 0) primaryColor() else onSurfaceVariantColor(),
                                shape = CircleShape
                            )
                            .padding(
                                horizontal = FavoritesDimensions.badgePaddingHorizontal,
                                vertical = FavoritesDimensions.badgePaddingVertical
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = favoriteItemsCount.toString(),
                            style = badgeText(),
                            color = if (selectedTabIndex == 0) onPrimaryColor() else onSurfaceColor()
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
                modifier = Modifier.padding(Spacing.medium)
            ) {
                Text(
                    text = stringResource(id = R.string.restaurants),
                    style = if (selectedTabIndex == 1) subTitle() else bodyMedium(),
                    color = if (selectedTabIndex == 1) primaryColor() else textSecondaryColor()
                )
                if (favoriteRestaurantsCount > 0) {
                    Spacer(modifier = Modifier.width(Spacing.small))
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (selectedTabIndex == 1) primaryColor() else onSurfaceVariantColor(),
                                shape = CircleShape
                            )
                            .padding(
                                horizontal = FavoritesDimensions.badgePaddingHorizontal,
                                vertical = FavoritesDimensions.badgePaddingVertical
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = favoriteRestaurantsCount.toString(),
                            style = badgeText(),
                            color = if (selectedTabIndex == 1) onPrimaryColor() else onSurfaceColor()
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
            LoadingScreen()
        }

        favoriteItems.isEmpty() -> {
            EmptyFavoritesState(
                title = stringResource(R.string.no_favorite_items_yet),
                description = stringResource(R.string.start_adding_your_favorite_dishes_to_see_them_here)
            )
        }

        else -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(FavoritesDimensions.cardSpacing),
                contentPadding = PaddingValues(
                    horizontal = Spacing.medium,
                    vertical = Spacing.small
                )
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
        isLoading -> LoadingScreen()

        favoriteRestaurants.isEmpty() -> {
            EmptyFavoritesState(
                title = stringResource(R.string.no_favorite_restaurants_yet),
                description = stringResource(R.string.start_adding_your_favorite_restaurants_to_see_them_here)
            )
        }

        else -> {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(FavoritesDimensions.cardSpacing),
                contentPadding = PaddingValues(
                    horizontal = Spacing.medium,
                    vertical = Spacing.small
                )
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
        shape = RoundedCornerShape(FavoritesDimensions.cardCornerRadius),
        elevation = CardDefaults.cardElevation(
            defaultElevation = FavoritesDimensions.cardElevation
        ),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(FavoritesDimensions.cardContentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                model = if (favorite.hasValidImage) favorite.imageUrl else null,
                contentDescription = stringResource(R.string.cd_restaurant_image),
                modifier = Modifier
                    .size(FavoritesDimensions.cardImageSize)
                    .clip(RoundedCornerShape(FavoritesDimensions.cardImageCornerRadius)),
                contentScale = ContentScale.Crop,
                failure = placeholder(R.drawable.default_food),
                loading = placeholder(R.drawable.default_food)
            )

            Spacer(modifier = Modifier.width(FavoritesDimensions.cardSpacing))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = favorite.name,
                    style = restaurantName(),
                    color = onSurfaceColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                Text(
                    text = favorite.cuisine,
                    style = restaurantCuisine(),
                    color = textSecondaryColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (favorite.rating > 0) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(id = R.string.cd_rating_star),
                            modifier = Modifier.size(FavoritesDimensions.ratingIconSize),
                            tint = ratingColor()
                        )

                        Spacer(modifier = Modifier.width(Spacing.xs))

                        Text(
                            text = favorite.ratingText,
                            style = restaurantRating(),
                            color = textSecondaryColor()
                        )

                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Text(
                        text = favorite.deliveryInfo,
                        style = restaurantDeliveryTime(),
                        color = textSecondaryColor()
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                Text(
                    text = favorite.priceRange,
                    style = foodItemPrice(),
                    color = primaryColor()
                )
            }

            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier.size(FavoritesDimensions.favoriteButtonSize)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = stringResource(R.string.remove_from_favorites),
                    tint = favoriteColor(),
                    modifier = Modifier.size(FavoritesDimensions.favoriteIconSize)
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
        shape = RoundedCornerShape(FavoritesDimensions.cardCornerRadius),
        elevation = CardDefaults.cardElevation(
            defaultElevation = FavoritesDimensions.cardElevation
        ),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(FavoritesDimensions.cardContentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                model = if (favorite.hasValidImage) favorite.imageUrl else null,
                contentDescription = stringResource(R.string.cd_food_image),
                modifier = Modifier
                    .size(FavoritesDimensions.cardImageSize)
                    .clip(RoundedCornerShape(FavoritesDimensions.cardImageCornerRadius)),
                contentScale = ContentScale.Crop,
                failure = placeholder(R.drawable.default_food),
                loading = placeholder(R.drawable.default_food)
            )

            Spacer(modifier = Modifier.width(FavoritesDimensions.cardSpacing))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = favorite.name,
                    style = foodItemName(),
                    color = onSurfaceColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                Text(
                    text = favorite.restaurantName,
                    style = restaurantCuisine(),
                    color = textSecondaryColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (favorite.rating > 0) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(R.string.cd_rating_star),
                            modifier = Modifier.size(FavoritesDimensions.ratingIconSize),
                            tint = ratingColor()
                        )

                        Spacer(modifier = Modifier.width(Spacing.xs))

                        Text(
                            text = favorite.ratingText,
                            style = restaurantRating(),
                            color = textSecondaryColor()
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = favorite.priceText,
                        style = foodItemPrice(),
                        color = primaryColor()
                    )
                }
            }

            IconButton(
                onClick = onRemoveClick,
                modifier = Modifier.size(FavoritesDimensions.favoriteButtonSize)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = stringResource(R.string.remove_from_favorites),
                    tint = favoriteColor(),
                    modifier = Modifier.size(FavoritesDimensions.favoriteIconSize)
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
            modifier = Modifier.padding(Spacing.xl)
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = stringResource(id = R.string.no_favorites),
                modifier = Modifier.size(FavoritesDimensions.emptyStateIconSize),
                tint = onSurfaceVariantColor()
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            Text(
                text = title,
                style = emptyStateTitle(),
                color = onSurfaceColor()
            )

            Spacer(modifier = Modifier.height(Spacing.small))

            Text(
                text = description,
                style = emptyStateDescription(),
                color = textSecondaryColor(),
                textAlign = TextAlign.Center
            )
        }
    }
}