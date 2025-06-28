package com.app.tastybuds.ui.resturants

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tastybuds.R
import com.app.tastybuds.domain.model.CategoryMenuItem
import com.app.tastybuds.domain.model.CategoryRestaurant
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.ui.resturants.state.RestaurantUiState
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.badgeTextColor
import com.app.tastybuds.ui.theme.cardBackgroundColor
import com.app.tastybuds.ui.theme.cardContentColor
import com.app.tastybuds.ui.theme.chipSelectedContentColor
import com.app.tastybuds.ui.theme.chipUnselectedBackgroundColor
import com.app.tastybuds.ui.theme.chipUnselectedContentColor
import com.app.tastybuds.ui.theme.freeshippingBadgeColor
import com.app.tastybuds.ui.theme.loadingIndicatorColor
import com.app.tastybuds.ui.theme.onBackgroundColor
import com.app.tastybuds.ui.theme.onPrimaryColor
import com.app.tastybuds.ui.theme.popularBadgeColor
import com.app.tastybuds.ui.theme.priceTextColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.starRatingColor
import com.app.tastybuds.ui.theme.successColor
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.app.tastybuds.util.ui.AppTopBar
import com.app.tastybuds.util.ui.ErrorScreen
import com.app.tastybuds.util.ui.LoadingScreen
import com.app.tastybuds.util.ui.SeeAllButton
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import kotlinx.coroutines.delay

@Composable
fun CategoryDetailsScreen(
    categoryName: String = "Fast Food",
    categoryId: String = "",
    onBackClick: () -> Unit = {},
    onRestaurantClick: (String) -> Unit = {},
    onMenuItemClick: (String) -> Unit = {},
    onSeeAllClick: (String, String) -> Unit = { _, _ -> },
    viewModel: RestaurantViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(categoryId) {
        if (categoryId.isNotBlank()) {
            viewModel.loadCategoryDetails(categoryId, categoryName)
        }
    }

    val availableFilters = remember(uiState.categoryDetails) {
        uiState.categoryDetails?.let { categoryDetails ->
            val allBadges = mutableSetOf<String>()

            categoryDetails.topRestaurants.forEach { restaurant ->
                restaurant.badges.forEach { badge ->
                    allBadges.add(badge.lowercase().replace(" ", "_"))
                }
            }

            categoryDetails.recommendedRestaurants.forEach { restaurant ->
                restaurant.badges.forEach { badge ->
                    allBadges.add(badge.lowercase().replace(" ", "_"))
                }
            }
            allBadges.toList().sorted()
        } ?: emptyList()
    }

    var selectedFilters by remember { mutableStateOf(setOf<String>()) }

    val filteredCategoryDetails = remember(uiState.categoryDetails, selectedFilters) {
        uiState.categoryDetails?.let { categoryDetails ->
            if (selectedFilters.isEmpty()) {
                categoryDetails
            } else {
                categoryDetails.copy(
                    topRestaurants = filterRestaurants(
                        categoryDetails.topRestaurants,
                        selectedFilters
                    ),
                    recommendedRestaurants = filterRestaurants(
                        categoryDetails.recommendedRestaurants,
                        selectedFilters
                    )
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor())
    ) {
        AppTopBar(
            title = uiState.categoryName.ifBlank { categoryName },
            onBackClick = onBackClick
        )

        if (availableFilters.isNotEmpty()) {
            FilterSection(
                availableFilters = availableFilters,
                selectedFilters = selectedFilters,
                onFilterClick = { filterId ->
                    selectedFilters = if (selectedFilters.contains(filterId)) {
                        selectedFilters - filterId
                    } else {
                        selectedFilters + filterId
                    }
                },
                onClearAllFilters = { selectedFilters = emptySet() }
            )
        }

        when {
            uiState.isLoading -> {
                LoadingScreen()
            }

            uiState.error != null -> {
                ErrorScreen(
                    title = uiState.error ?: stringResource(R.string.unknown_error),
                    onRetryClick = { viewModel.retry() }
                )
            }

            uiState.isEmpty -> {
                EmptyContent(categoryName = categoryName)
            }

            else -> {
                CategoryContent(
                    uiState = uiState.copy(categoryDetails = filteredCategoryDetails),
                    onRestaurantClick = onRestaurantClick,
                    onSeeAllClick = onSeeAllClick,
                    categoryName = categoryName,
                    onMenuItemClick = onMenuItemClick
                )
            }
        }
    }
}

private fun filterRestaurants(
    restaurants: List<CategoryRestaurant>,
    selectedFilters: Set<String>
): List<CategoryRestaurant> {
    if (selectedFilters.isEmpty()) {
        return restaurants
    }

    return restaurants.filter { restaurant ->
        selectedFilters.all { badgeId ->
            when (badgeId) {
                BadgeType.FREESHIP.value -> {
                    restaurant.badges.any { it.lowercase().contains(BadgeType.FREESHIP.value) } ||
                            restaurant.deliveryFee <= 0.0 ||
                            restaurant.isFreeship
                }

                BadgeType.FAVORITE.value -> {
                    restaurant.isFavorite
                }

                BadgeType.NEAR_YOU.value -> {
                    restaurant.distance.contains("km") &&
                            restaurant.distance.replace("[^0-9.]".toRegex(), "").toFloatOrNull()
                                ?.let { it <= 5.0 } ?: false
                }

                BadgeType.PARTNER.value -> {
                    restaurant.badges.any { badge ->
                        val lowerBadge = badge.lowercase()
                        lowerBadge.contains(BadgeType.PARTNER.value) || lowerBadge.contains(
                            BadgeType.VERIFIED.value
                        )
                    }
                }

                else -> {
                    restaurant.badges.any {
                        it.lowercase().replace(" ", "_") == badgeId.lowercase()
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = loadingIndicatorColor(),
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_help),
            contentDescription = stringResource(R.string.error),
            tint = primaryColor(),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.oops_something_went_wrong),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = onBackgroundColor()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error,
            fontSize = 14.sp,
            color = textSecondaryColor(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor()),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = stringResource(R.string.try_again),
                color = onPrimaryColor(),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun EmptyContent(categoryName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = stringResource(R.string.empty),
            tint = textSecondaryColor(),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.no_restaurants_found),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = onBackgroundColor()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(
                R.string.no_restaurants_available_in_category_right_now,
                categoryName
            ),
            fontSize = 14.sp,
            color = textSecondaryColor(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun CategoryContent(
    uiState: RestaurantUiState,
    onRestaurantClick: (String) -> Unit,
    onSeeAllClick: (String, String) -> Unit,
    categoryName: String,
    onMenuItemClick: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        uiState.categoryDetails?.let { categoryDetails ->
            if (categoryDetails.topRestaurants.isNotEmpty()) {
                val displayedRestaurants = categoryDetails.topRestaurants.take(3)

                items(displayedRestaurants) { restaurant ->
                    CategoryRestaurantCard(
                        restaurant = restaurant,
                        onClick = { onRestaurantClick(restaurant.id) }
                    )
                }
                item {
                    if (categoryDetails.topRestaurants.size > 3) {
                        SeeAllButton(
                            onClick = {
                                onSeeAllClick(
                                    "Top $categoryName Restaurants",
                                    "top_restaurants"
                                )
                            }
                        )
                    }
                }
            }
        }

        if (uiState.categoryDetails == null && uiState.restaurants.isNotEmpty()) {
            items(uiState.restaurants) { restaurant ->
                RegularRestaurantCard(
                    restaurant = restaurant,
                    onClick = { onRestaurantClick(restaurant.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        uiState.categoryDetails?.let { categoryDetails ->
            if (categoryDetails.menuItems.isNotEmpty()) {
                item {
                    FeaturedBannerSection(
                        menuItems = categoryDetails.menuItems,
                        onMenuItemClick = { menuItem ->
                            if (menuItem.restaurantId.isNotEmpty()) {
                                onMenuItemClick(menuItem.id)
                            }
                        }
                    )
                }
            }
            if (categoryDetails.recommendedRestaurants.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.recommended_for_you),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = onBackgroundColor(),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(categoryDetails.recommendedRestaurants) { restaurant ->
                    CategoryRestaurantCard(
                        restaurant = restaurant,
                        onClick = { onRestaurantClick(restaurant.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterSection(
    availableFilters: List<String>,
    selectedFilters: Set<String>,
    onFilterClick: (String) -> Unit,
    onClearAllFilters: () -> Unit
) {
    Column {
        if (selectedFilters.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onClearAllFilters,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = primaryColor()
                    )
                ) {
                    Text(
                        text = stringResource(R.string.clear_all),
                        fontSize = 12.sp
                    )
                }
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableFilters) { filter ->
                val isSelected = selectedFilters.contains(filter)
                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterClick(filter) },
                    label = {
                        Text(
                            text = filter.replace("_", " ").replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase() else it.toString()
                            },
                            fontSize = 12.sp,
                            color = if (isSelected) chipSelectedContentColor() else chipUnselectedContentColor()
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = primaryColor(),
                        selectedLabelColor = onPrimaryColor(),
                        containerColor = chipUnselectedBackgroundColor(),
                        labelColor = chipUnselectedContentColor()
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CategoryRestaurantCard(
    restaurant: CategoryRestaurant,
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
                model = restaurant.imageUrl,
                contentDescription = restaurant.name,
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
                    text = restaurant.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = cardContentColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = restaurant.cuisine,
                    fontSize = 14.sp,
                    color = textSecondaryColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = restaurant.deliveryTime,
                        fontSize = 12.sp,
                        color = cardContentColor()
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(id = R.string.rating),
                        modifier = Modifier.size(14.dp),
                        tint = starRatingColor()
                    )

                    Spacer(modifier = Modifier.width(2.dp))

                    Text(
                        text = "${restaurant.rating} (${restaurant.reviewCount})",
                        fontSize = 12.sp,
                        color = cardContentColor()
                    )
                }

                if (restaurant.badges.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(restaurant.badges) { badge ->
                            BadgeChip(text = badge)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MenuItemCard(
    menuItem: CategoryMenuItem,
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
                model = menuItem.imageUrl,
                contentDescription = menuItem.name,
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
                    text = menuItem.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = cardContentColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (menuItem.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = menuItem.description,
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
                    Text(
                        text = "$${menuItem.price.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = priceTextColor()
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(id = R.string.rating),
                        modifier = Modifier.size(12.dp),
                        tint = starRatingColor()
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "${menuItem.rating} (${menuItem.reviewCount})",
                        fontSize = 12.sp,
                        color = textSecondaryColor()
                    )
                }

                if (menuItem.restaurantName.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "From ${menuItem.restaurantName}",
                        fontSize = 12.sp,
                        color = primaryColor(),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RegularRestaurantCard(
    restaurant: Restaurant,
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
                model = restaurant.imageUrl,
                contentDescription = restaurant.name,
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
                    text = restaurant.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = cardContentColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = restaurant.cuisine,
                    fontSize = 14.sp,
                    color = textSecondaryColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = restaurant.deliveryTime,
                        fontSize = 12.sp,
                        color = cardContentColor()
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(id = R.string.rating),
                        modifier = Modifier.size(14.dp),
                        tint = starRatingColor()
                    )

                    Spacer(modifier = Modifier.width(2.dp))

                    Text(
                        text = "${restaurant.rating} (${restaurant.reviewCount})",
                        fontSize = 12.sp,
                        color = cardContentColor()
                    )
                }

                restaurant.badge?.let { badge ->
                    Spacer(modifier = Modifier.height(8.dp))
                    BadgeChip(text = badge)
                }
            }
        }
    }
}

@Composable
fun BadgeChip(text: String) {
    when (text.lowercase()) {
        BadgeType.FREESHIP.value -> freeshippingBadgeColor()
        BadgeType.NEAR_YOU.value.replace("_", " ") -> primaryColor()
        BadgeType.POPULAR.value -> popularBadgeColor()
        else -> successColor()
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor())
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            color = badgeTextColor(),
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeaturedBannerSection(
    menuItems: List<CategoryMenuItem>,
    onMenuItemClick: (CategoryMenuItem) -> Unit
) {
    if (menuItems.isEmpty()) return

    val banners = remember { menuItems }
    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            FeaturedBannerCard(
                banner = banners[page],
                onClick = { onMenuItemClick(banners[page]) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(banners.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) primaryColor() else textSecondaryColor().copy(alpha = 0.5f)
                        )
                )
                if (index < banners.size - 1) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FeaturedBannerCard(
    banner: CategoryMenuItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            GlideImage(
                model = banner.imageUrl,
                contentDescription = banner.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                failure = placeholder(R.drawable.default_food),
                loading = placeholder(R.drawable.default_food)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = banner.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = banner.description,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

enum class BadgeType(val value: String) {
    FREESHIP("freeship"),
    FAVORITE("favorite"),
    NEAR_YOU("near_you"),
    PARTNER("partner"),
    POPULAR("popular"),
    VERIFIED("verified")
}

@Preview(showBackground = true)
@Composable
fun CategoryDetailsScreenPreview() {
    CategoryDetailsScreen()
}