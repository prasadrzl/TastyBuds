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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tastybuds.R
import com.app.tastybuds.data.model.RestaurantMenuItem
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.borderColor
import com.app.tastybuds.ui.theme.cardBackgroundColor
import com.app.tastybuds.ui.theme.cardContentColor
import com.app.tastybuds.ui.theme.categoryChipSelectedColor
import com.app.tastybuds.ui.theme.categoryChipUnselectedColor
import com.app.tastybuds.ui.theme.emptyHeartColor
import com.app.tastybuds.ui.theme.errorColor
import com.app.tastybuds.ui.theme.heartFavoriteColor
import com.app.tastybuds.ui.theme.onPrimaryColor
import com.app.tastybuds.ui.theme.onSurfaceColor
import com.app.tastybuds.ui.theme.popularBadgeColor
import com.app.tastybuds.ui.theme.priceTextColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.starRatingColor
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.app.tastybuds.util.ui.AppTopBar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

enum class MenuViewType {
    GRID, LIST
}

enum class MenuSectionType {
    FOR_YOU, FULL_MENU
}

@Composable
fun MenuListScreen(
    restaurantId: String,
    restaurantName: String? = null,
    section: MenuSectionType = MenuSectionType.FULL_MENU,
    onBackClick: () -> Unit,
    onMenuItemClick: (RestaurantMenuItem) -> Unit,
    viewModel: RestaurantDetailsViewModel = hiltViewModel()
) {
    val viewType by remember { mutableStateOf(if (section == MenuSectionType.FOR_YOU) MenuViewType.GRID else MenuViewType.LIST) }

    val uiState by viewModel.uiState.collectAsState()
    val isLoadingMenu by viewModel.isLoadingMenu.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories by viewModel.categories.collectAsState()

    val menuItems = when (section) {
        MenuSectionType.FOR_YOU -> uiState.restaurantData?.forYouItems ?: emptyList()
        MenuSectionType.FULL_MENU -> {
            val filteredItems by viewModel.filteredMenuItems.collectAsState()
            filteredItems
        }
    }

    LaunchedEffect(restaurantId) {
        if (restaurantId.isNotEmpty()) {
            viewModel.loadRestaurantDetails(restaurantId, "user_001")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor())
    ) {
        AppTopBar(
            title = when (section) {
                MenuSectionType.FOR_YOU -> "${restaurantName ?: "Restaurant"} - For You"
                MenuSectionType.FULL_MENU -> "${restaurantName ?: "Restaurant"} Menu"
            },
            onBackClick = onBackClick
        )

        if (section == MenuSectionType.FULL_MENU && categories.isNotEmpty()) {
            CategoryFilterRow(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.selectCategory(it) }
            )
        }

        when {
            isLoadingMenu -> {
                LoadingContent()
            }

            menuItems.isEmpty() -> {
                EmptyContent(section = section, selectedCategory = selectedCategory)
            }

            else -> {
                MenuContent(
                    items = menuItems,
                    viewType = viewType,
                    onItemClick = onMenuItemClick,
                    onToggleFavorite = { viewModel.toggleMenuItemFavorite(it.id) }
                )
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            CategoryChip(
                text = stringResource(R.string.all),
                isSelected = selectedCategory == "All",
                onClick = { onCategorySelected("All") }
            )
        }

        items(categories) { category ->
            CategoryChip(
                text = category,
                isSelected = selectedCategory == category,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
private fun MenuContent(
    items: List<RestaurantMenuItem>,
    viewType: MenuViewType,
    onItemClick: (RestaurantMenuItem) -> Unit,
    onToggleFavorite: (RestaurantMenuItem) -> Unit
) {
    when (viewType) {
        MenuViewType.GRID -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    MenuItemGridCard(
                        item = item,
                        onClick = { onItemClick(item) },
                        onToggleFavorite = { onToggleFavorite(item) }
                    )
                }
            }
        }

        MenuViewType.LIST -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    MenuItemListCard(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyContent(
    section: MenuSectionType,
    selectedCategory: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (section) {
                    MenuSectionType.FOR_YOU -> stringResource(R.string.no_recommendations_available)
                    MenuSectionType.FULL_MENU -> {
                        if (selectedCategory == "All") {
                            stringResource(R.string.no_menu_items_available)
                        } else {
                            stringResource(R.string.no_items_found_in, selectedCategory)
                        }
                    }
                },
                fontSize = 16.sp,
                color = textSecondaryColor()
            )
        }
    }
}

@Composable
fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        selected = isSelected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = categoryChipSelectedColor(),
            selectedLabelColor = onPrimaryColor(),
            containerColor = categoryChipUnselectedColor(),
            labelColor = onSurfaceColor()
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = if (isSelected) primaryColor() else borderColor(),
            selectedBorderColor = primaryColor(),
            enabled = false,
            selected = true
        )
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MenuItemGridCard(
    item: RestaurantMenuItem,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box {
                GlideImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    failure = placeholder(R.drawable.default_food),
                    loading = placeholder(R.drawable.default_food)
                )

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle favorite",
                        tint = if (item.isFavorite) heartFavoriteColor() else emptyHeartColor(),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = cardContentColor(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (item.rating > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = starRatingColor(),
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(2.dp))

                    Text(
                        text = "${item.rating}",
                        fontSize = 12.sp,
                        color = cardContentColor()
                    )

                    Text(
                        text = " (${item.reviewCount})",
                        fontSize = 12.sp,
                        color = textSecondaryColor()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$${"%.0f".format(item.price)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = priceTextColor()
            )

            if (item.isPopular) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = popularBadgeColor().copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Popular",
                        fontSize = 10.sp,
                        color = popularBadgeColor(),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MenuItemListCard(
    item: RestaurantMenuItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                failure = placeholder(R.drawable.default_food),
                loading = placeholder(R.drawable.default_food)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = cardContentColor()
                )

                if (item.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.description,
                        fontSize = 12.sp,
                        color = textSecondaryColor(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (item.rating > 0) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = starRatingColor(),
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "${item.rating}",
                            fontSize = 12.sp,
                            color = cardContentColor()
                        )

                        Text(
                            text = " (${item.reviewCount})",
                            fontSize = 12.sp,
                            color = textSecondaryColor()
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = item.prepTime,
                        fontSize = 12.sp,
                        color = textSecondaryColor()
                    )
                }

                if (item.isPopular || item.isSpicy) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        if (item.isPopular) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = popularBadgeColor().copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "Popular",
                                    fontSize = 10.sp,
                                    color = popularBadgeColor(),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            if (item.isSpicy) {
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }

                        if (item.isSpicy) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = errorColor().copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "Spicy",
                                    fontSize = 10.sp,
                                    color = errorColor(),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$${"%.0f".format(item.price)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = priceTextColor()
                )
            }
        }
    }
}