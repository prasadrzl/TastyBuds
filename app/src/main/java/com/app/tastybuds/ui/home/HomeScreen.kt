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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tastybuds.R
import com.app.tastybuds.domain.model.Banner
import com.app.tastybuds.domain.model.Category
import com.app.tastybuds.domain.model.Collection
import com.app.tastybuds.domain.model.Deal
import com.app.tastybuds.domain.model.Restaurant
import com.app.tastybuds.ui.home.state.HomeUiState
import com.app.tastybuds.ui.theme.PrimaryColor
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import kotlinx.coroutines.delay
import com.app.tastybuds.domain.model.Collection as FoodCollection

@Composable
fun HomeScreen(
    onCategoryClick: (String, String) -> Unit = { _, _ -> },
    onProfileClick: () -> Unit = {},
    onSearchClick: (String) -> Unit = {},
    onRestaurantClick: (String) -> Unit = {},
    onViewAllCollections: () -> Unit = {},
    onViewAllRestaurants: () -> Unit = {},
    onViewAllDeals: () -> Unit = {},
    onViewAllVouchers: () -> Unit = {},
    onBannerClick: (String) -> Unit = {},
    onCollectionClick: (String) -> Unit = {},
    onDealClick: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            LoadingScreen()
        }

        uiState.error != null -> {
            ErrorScreen(
                error = uiState.error ?: stringResource(id = R.string.unknown_error),
                onRetry = { viewModel.retry() }
            )
        }

        else -> {
            HomeContent(
                uiState = uiState,
                onCategoryClick = onCategoryClick,
                onRestaurantClick = onRestaurantClick,
                onViewAllCollections = onViewAllCollections,
                onViewAllRestaurants = onViewAllRestaurants,
                onViewAllDeals = onViewAllDeals,
                onViewAllVouchers = onViewAllVouchers,
                onBannerClick = onBannerClick,
                onCollectionClick = onCollectionClick,
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = PrimaryColor,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
fun ErrorScreen(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_help),
            contentDescription = "Error",
            tint = PrimaryColor,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.oops_something_went_wrong),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text = "Try Again",
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    onCategoryClick: (String, String) -> Unit,
    onRestaurantClick: (String) -> Unit,
    onViewAllCollections: () -> Unit,
    onViewAllRestaurants: () -> Unit,
    onViewAllDeals: () -> Unit,
    onViewAllVouchers: () -> Unit,
    onBannerClick: (String) -> Unit,
    onCollectionClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
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
                    onViewAllCollections = onViewAllCollections,
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
                SaleSection(deals = uiState.deals, onViewAllDeals)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun VoucherSection(
    voucherCount: Int,
    onViewAllClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onViewAllClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_offer_percentage),
                contentDescription = "Voucher",
                modifier = Modifier.size(24.dp),
                tint = PrimaryColor
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "You have $voucherCount voucher here",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "View all",
                fontSize = 12.sp,
                color = PrimaryColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DealBannerSection(
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
                            if (isSelected) PrimaryColor else Color.Gray.copy(alpha = 0.5f)
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
fun DealBannerCard(
    banner: Banner,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(144.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = banner.backgroundColor)
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = banner.price,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = banner.description,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            GlideImage(
                model = banner.imageUrl,
                contentDescription = banner.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                failure = placeholder(R.drawable.default_food),
                loading = placeholder(R.drawable.default_food)
            )
        }
    }
}

@Composable
fun CategoriesSection(
    categories: List<Category>,
    onCategoryClick: (String, String) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                onClick = { onCategoryClick(category.id, category.name) }
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CategoryCard(
    category: Category,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .width(64.dp)
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
                contentDescription = category.name,
                modifier = Modifier.size(32.dp),
                failure = placeholder(R.drawable.ic_rice),
                loading = placeholder(R.drawable.ic_rice)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = category.name,
            fontSize = 12.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CollectionsSection(
    collections: List<Collection>,
    onViewAllCollections: () -> Unit,
    onCollectionClick: (String) -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.collections),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = stringResource(id = R.string.view_all),
                fontSize = 14.sp,
                color = PrimaryColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onViewAllCollections() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (collections.size > 0) {
                    StaticCollectionCard(
                        collection = collections[0],
                        onClick = { onCollectionClick(collections[0].id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (collections.size > 1) {
                    StaticCollectionCard(
                        collection = collections[1],
                        onClick = { onCollectionClick(collections[1].id) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (collections.size > 2) {
                    StaticCollectionCard(
                        collection = collections[2],
                        onClick = { onCollectionClick(collections[2].id) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (collections.size > 3) {
                    StaticCollectionCard(
                        collection = collections[3],
                        onClick = { onCollectionClick(collections[3].id) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun StaticCollectionCard(
    collection: FoodCollection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
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
                    contentDescription = collection.title,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    failure = placeholder(R.drawable.default_food),
                    loading = placeholder(R.drawable.default_food)
                )

                collection.badge?.let { badge ->
                    Card(
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.TopStart),
                        shape = RoundedCornerShape(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            fontSize = 8.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = collection.subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun RecommendedSection(
    restaurants: List<Restaurant>,
    onRestaurantClick: (String) -> Unit,
    onViewAllClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.recommended_for_you),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = stringResource(id = R.string.view_all),
                fontSize = 14.sp,
                color = PrimaryColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.padding(start = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(restaurants) { restaurant ->
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
fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box {
                GlideImage(
                    model = restaurant.imageUrl,
                    contentDescription = restaurant.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentScale = ContentScale.Crop,
                    failure = placeholder(R.drawable.default_food),
                    loading = placeholder(R.drawable.default_food)
                )

                restaurant.badge?.let { badge ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart),
                        shape = RoundedCornerShape(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = restaurant.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = restaurant.cuisine,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.material_starpurple500_sharp),
                        contentDescription = "Rating",
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFFFFC107)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = restaurant.rating.toString(),
                        fontSize = 12.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = restaurant.deliveryTime,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun SaleSection(
    deals: List<Deal>,
    onViewAllClick: () -> Unit,
    onDealClick: (String) -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sale up to 50%",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "View all",
                fontSize = 14.sp,
                color = PrimaryColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.padding(start = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(deals) { deal ->
                SaleCard(
                    deal = deal,
                    onClick = { onDealClick(deal.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SaleCard(
    deal: Deal,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box {
                GlideImage(
                    model = deal.imageUrl,
                    contentDescription = deal.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentScale = ContentScale.Crop,
                    failure = placeholder(R.drawable.default_food),
                    loading = placeholder(R.drawable.default_food)
                )

                deal.badge?.let { badge ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart),
                        shape = RoundedCornerShape(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                deal.discountPercentage?.let { percentage ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopStart),
                        shape = RoundedCornerShape(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(
                            text = "-$percentage%",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = deal.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = deal.price,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )

                    deal.originalPrice?.let { originalPrice ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = originalPrice,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
            }
        }
    }
}