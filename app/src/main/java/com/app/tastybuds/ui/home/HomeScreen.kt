package com.app.tastybuds.ui.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.R
import com.app.tastybuds.domain.model.Banner
import com.app.tastybuds.domain.model.Category
import com.app.tastybuds.domain.model.Collection
import com.app.tastybuds.domain.model.Deal
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.ui.home.HomeViewModel.Companion.HOME_ITEM_LIMIT
import com.app.tastybuds.ui.home.state.HomeUiState
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.borderColor
import com.app.tastybuds.ui.theme.cardBackgroundColor
import com.app.tastybuds.ui.theme.cardContentColor
import com.app.tastybuds.ui.theme.discountTextColor
import com.app.tastybuds.ui.theme.linkTextColor
import com.app.tastybuds.ui.theme.newBadgeColor
import com.app.tastybuds.ui.theme.offerBackgroundColor
import com.app.tastybuds.ui.theme.offerTextColor
import com.app.tastybuds.ui.theme.onBackgroundColor
import com.app.tastybuds.ui.theme.onErrorColor
import com.app.tastybuds.ui.theme.onPrimaryColor
import com.app.tastybuds.ui.theme.originalPriceTextColor
import com.app.tastybuds.ui.theme.outlineVariantColor
import com.app.tastybuds.ui.theme.popularBadgeColor
import com.app.tastybuds.ui.theme.priceTextColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.sectionTitle
import com.app.tastybuds.ui.theme.starRatingColor
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.app.tastybuds.util.ui.ErrorScreen
import com.app.tastybuds.util.ui.LoadingScreen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import kotlinx.coroutines.delay
import java.util.Locale
import com.app.tastybuds.domain.model.Collection as FoodCollection

@Composable
fun HomeScreen(
    onCategoryClick: (String, String) -> Unit = { _, _ -> },
    onRestaurantClick: (String) -> Unit = {},
    onViewAllRestaurants: () -> Unit = {},
    onViewAllDeals: () -> Unit = {},
    onViewAllVouchers: () -> Unit = {},
    onBannerClick: (String) -> Unit = {},
    onCollectionClick: (Collection) -> Unit = {},
    onDealClick: (String, String) -> Unit = { _, _ -> },
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor()
    ) {
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

            else -> {
                HomeContent(
                    uiState = uiState,
                    onCategoryClick = onCategoryClick,
                    onRestaurantClick = onRestaurantClick,
                    onViewAllRestaurants = onViewAllRestaurants,
                    onViewAllDeals = onViewAllDeals,
                    onViewAllVouchers = onViewAllVouchers,
                    onBannerClick = onBannerClick,
                    onCollectionClick = onCollectionClick,
                    onDealClick = onDealClick
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onCategoryClick: (String, String) -> Unit,
    onRestaurantClick: (String) -> Unit,
    onViewAllRestaurants: () -> Unit,
    onViewAllDeals: () -> Unit,
    onViewAllVouchers: () -> Unit,
    onBannerClick: (String) -> Unit,
    onCollectionClick: (Collection) -> Unit,
    onDealClick: (String, String) -> Unit = { _, _ -> }
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (uiState.banners.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                DealBannerSection(
                    banners = uiState.banners,
                    onBannerClick = onBannerClick
                )
            }
        }

        if (uiState.categories.isNotEmpty()) {
            item {
                CategoriesSection(
                    categories = uiState.categories,
                    onCategoryClick = onCategoryClick
                )
            }
        }

        item {
            VoucherSection(
                voucherCount = uiState.voucherCount,
                onViewAllClick = onViewAllVouchers
            )
        }

        if (uiState.collections.isNotEmpty()) {
            item {
                CollectionsSection(
                    collections = uiState.collections,
                    onCollectionClick = onCollectionClick
                )
            }
        }

        if (uiState.recommendedRestaurants.isNotEmpty()) {
            item {
                RecommendedSection(
                    restaurants = uiState.recommendedRestaurants,
                    onRestaurantClick = onRestaurantClick,
                    onViewAllClick = onViewAllRestaurants
                )
            }
        }

        if (uiState.deals.isNotEmpty()) {
            item {
                SaleSection(
                    deals = uiState.deals,
                    onViewAllDeals = onViewAllDeals,
                    onDealClick = onDealClick
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun VoucherSection(
    voucherCount: Int,
    onViewAllClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clickable { onViewAllClick() }
            .semantics { contentDescription = "Voucher section" },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = offerBackgroundColor()
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_offer_percentage),
                contentDescription = stringResource(R.string.vouchers),
                modifier = Modifier.size(24.dp),
                tint = primaryColor()
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(R.string.you_have_voucher_here, voucherCount),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = offerTextColor(),
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_right_arrow),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = primaryColor()
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DealBannerSection(
    banners: List<Banner>,
    onBannerClick: (String) -> Unit = {}
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { banners.size }
    )

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000)
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
            val banner = banners[page]
            DealBannerCard(
                banner = banner,
                onClick = { onBannerClick(banner.id) }
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
                            if (isSelected) primaryColor()
                            else outlineVariantColor()
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
private fun DealBannerCard(
    banner: Banner,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(144.dp)
            .clickable { onClick() }
            .semantics { contentDescription = "Banner: ${banner.title}" },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = banner.backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = banner.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = banner.price,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = banner.description,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            GlideImage(
                model = banner.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                failure = placeholder(R.drawable.default_food),
                loading = placeholder(R.drawable.default_food)
            )
        }
    }
}

@Composable
private fun CategoriesSection(
    categories: List<Category>,
    onCategoryClick: (String, String) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(
            items = categories,
            key = { category -> category.id }
        ) { category ->
            CategoryCard(
                category = category,
                onClick = { onCategoryClick(category.id, category.name) }
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun CategoryCard(
    category: Category,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .width(72.dp)
            .semantics { contentDescription = "Category: ${category.name}" }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(category.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            GlideImage(
                model = category.imageUrl,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                failure = placeholder(R.drawable.ic_rice),
                loading = placeholder(R.drawable.ic_rice)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = category.name,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = cardContentColor(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CollectionsSection(
    collections: List<Collection>,
    onCollectionClick: (Collection) -> Unit = {}
) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        SectionHeader(
            title = stringResource(R.string.collections),
            showViewAll = false
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (collections.isNotEmpty()) {
                    StaticCollectionCard(
                        collection = collections[0],
                        onClick = { onCollectionClick(collections[0]) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (collections.size > 1) {
                    StaticCollectionCard(
                        collection = collections[1],
                        onClick = { onCollectionClick(collections[1]) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (collections.size > 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StaticCollectionCard(
                        collection = collections[2],
                        onClick = { onCollectionClick(collections[2]) },
                        modifier = Modifier.weight(1f)
                    )
                    if (collections.size > 3) {
                        StaticCollectionCard(
                            collection = collections[3],
                            onClick = { onCollectionClick(collections[3]) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun StaticCollectionCard(
    collection: FoodCollection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() }
            .semantics { contentDescription = "Collection: ${collection.title}" },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor()
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, borderColor())
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                GlideImage(
                    model = collection.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    failure = placeholder(R.drawable.default_food),
                    loading = placeholder(R.drawable.default_food)
                )

                collection.badge?.let { badge ->
                    Surface(
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.TopStart),
                        shape = RoundedCornerShape(4.dp),
                        color = newBadgeColor()
                    ) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = onErrorColor()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = collection.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = cardContentColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = collection.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecondaryColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    showViewAll: Boolean = true,
    onViewAllClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = sectionTitle(),
            color = onBackgroundColor()
        )

        if (showViewAll) {
            Text(
                text = stringResource(R.string.view_all),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = linkTextColor(),
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }
    }
}

@Composable
private fun RecommendedSection(
    restaurants: List<Restaurant>,
    onRestaurantClick: (String) -> Unit,
    onViewAllClick: () -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        SectionHeader(
            title = stringResource(R.string.recommended_for_you),
            showViewAll = true,
            onViewAllClick = onViewAllClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.padding(start = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = restaurants.take(HOME_ITEM_LIMIT),
                key = { restaurant -> restaurant.id }
            ) { restaurant ->
                RestaurantCard(
                    restaurant = restaurant,
                    onClick = { onRestaurantClick(restaurant.id) }
                )
            }
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
            .width(160.dp)
            .clickable { onClick() }
            .semantics { contentDescription = "Restaurant: ${restaurant.name}" },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor()
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                GlideImage(
                    model = restaurant.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentScale = ContentScale.Crop,
                    failure = placeholder(R.drawable.default_food),
                    loading = placeholder(R.drawable.default_food)
                )

                restaurant.badge?.let { badge ->
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart),
                        shape = RoundedCornerShape(6.dp),
                        color = popularBadgeColor()
                    ) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = onPrimaryColor()
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = cardContentColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = restaurant.cuisine,
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecondaryColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.material_starpurple500_sharp),
                        contentDescription = stringResource(R.string.rating),
                        modifier = Modifier.size(12.dp),
                        tint = starRatingColor()
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = restaurant.rating.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = cardContentColor()
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = restaurant.deliveryTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = textSecondaryColor()
                    )
                }
            }
        }
    }
}

@Composable
private fun SaleSection(
    deals: List<Deal>,
    onViewAllDeals: () -> Unit,
    onDealClick: (String, String) -> Unit = { _, _ -> }
) {
    Column {
        SectionHeader(
            title = stringResource(R.string.sale_up_to_50),
            showViewAll = true,
            onViewAllClick = onViewAllDeals
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.padding(start = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = deals.take(HOME_ITEM_LIMIT),
                key = { deal -> deal.id }
            ) { deal ->
                SaleCard(
                    deal = deal,
                    onClick = { onDealClick(deal.id, deal.menuItemId) }
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun SaleCard(
    deal: Deal,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
            .semantics { contentDescription = "Deal: ${deal.title}" },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor()
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                GlideImage(
                    model = deal.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentScale = ContentScale.Crop,
                    failure = placeholder(R.drawable.default_food),
                    loading = placeholder(R.drawable.default_food)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    deal.badges.firstOrNull()?.let { badge ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = try {
                                Color(badge.backgroundColor.toColorInt())
                            } catch (e: Exception) {
                                newBadgeColor()
                            }
                        ) {
                            Text(
                                text = badge.text,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color.White
                            )
                        }
                    }

                    if (deal.discountPercentage > 0) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = discountTextColor()
                        ) {
                            Text(
                                text = "-${deal.discountPercentage}%",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = onErrorColor()
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = deal.restaurantName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = cardContentColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = deal.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecondaryColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        modifier = Modifier.size(12.dp),
                        tint = starRatingColor()
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = deal.rating.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = cardContentColor(),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondaryColor()
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = deal.deliveryTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondaryColor()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    Text(
                        text = deal.originalPrice,
                        style = MaterialTheme.typography.bodySmall,
                        color = originalPriceTextColor(),
                        textDecoration = TextDecoration.LineThrough
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = deal.salePrice,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = priceTextColor()
                        )

                        val originalPriceValue =
                            deal.originalPrice.replace("$", "").toFloatOrNull() ?: 0f
                        val salePriceValue = deal.salePrice.replace("$", "").toFloatOrNull() ?: 0f
                        val savings = originalPriceValue - salePriceValue

                        if (savings > 0) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = primaryColor().copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "Save $${
                                        String.format(
                                            Locale.getDefault(),
                                            "%.2f",
                                            savings
                                        )
                                    }",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = primaryColor(),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}