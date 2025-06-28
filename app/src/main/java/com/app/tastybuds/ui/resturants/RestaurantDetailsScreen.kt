package com.app.tastybuds.ui.resturants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.R
import com.app.tastybuds.data.model.RestaurantCombo
import com.app.tastybuds.data.model.RestaurantDetails
import com.app.tastybuds.data.model.RestaurantDetailsData
import com.app.tastybuds.data.model.RestaurantMenuItem
import com.app.tastybuds.data.model.RestaurantReview
import com.app.tastybuds.ui.login.LoginViewModel
import com.app.tastybuds.ui.theme.cardBackgroundColor
import com.app.tastybuds.ui.theme.cardContentColor
import com.app.tastybuds.ui.theme.dividerColor
import com.app.tastybuds.ui.theme.emptyStarColor
import com.app.tastybuds.ui.theme.heartFavoriteColor
import com.app.tastybuds.ui.theme.onBackgroundColor
import com.app.tastybuds.ui.theme.priceTextColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.starRatingColor
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.app.tastybuds.util.ui.ErrorScreen
import com.app.tastybuds.util.ui.LoadingScreen
import com.app.tastybuds.util.ui.SeeAllButton
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@Composable
fun RestaurantDetailsScreen(
    restaurantId: String = "",
    onBackClick: () -> Unit = {},
    onFoodItemClick: (String) -> Unit = {},
    onComboClick: (String) -> Unit = {},
    onViewAllClick: (String) -> Unit = {},
    onSellAllClick: (String) -> Unit = {},
    onViewAllReviews: (List<RestaurantReview>) -> Unit = {},
    viewModel: RestaurantDetailsViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()

) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userIdFlow by loginViewModel.getUserId().collectAsState(initial = "user_001")

    LaunchedEffect(restaurantId, userIdFlow) {
        userIdFlow?.let { userId ->
            if (userId.isNotEmpty() && restaurantId.isNotEmpty()) {
                viewModel.loadRestaurantDetails(restaurantId, userId)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

            uiState.restaurantData != null -> {
                RestaurantDetailsContent(
                    restaurantData = uiState.restaurantData!!,
                    isFavorite = uiState.restaurantData?.restaurant?.isFavorite ?: false,
                    voucherCount = uiState.voucherCount,
                    onBackClick = onBackClick,
                    onFavoriteClick = { viewModel.toggleFavorite() },
                    onFoodItemClick = onFoodItemClick,
                    onComboClick = onComboClick,
                    onViewAllClick = onViewAllClick,
                    onSellAllClick = onSellAllClick,
                    onViewAllReviews = onViewAllReviews
                )
            }
        }
    }
}

@Composable
private fun RestaurantDetailsContent(
    restaurantData: RestaurantDetailsData,
    voucherCount: Int,
    onBackClick: () -> Unit,
    onFoodItemClick: (String) -> Unit,
    onComboClick: (String) -> Unit,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onViewAllClick: (String) -> Unit = {},
    onSellAllClick: (String) -> Unit = {},
    onViewAllReviews: (List<RestaurantReview>) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            RestaurantImageHeader(
                restaurant = restaurantData.restaurant,
                onBackClick = onBackClick,
                isFavorite = isFavorite,
                onFavoriteClick = onFavoriteClick
            )
        }

        item {
            RestaurantInfoRows(
                restaurant = restaurantData.restaurant,
                voucherCount = voucherCount
            )
        }

        if (restaurantData.forYouItems.isNotEmpty()) {
            item {
                ForYouSection(
                    restaurantId = restaurantData.restaurant.id,
                    items = restaurantData.forYouItems,
                    onItemClick = onFoodItemClick,
                    onViewAllClick = onViewAllClick
                )
            }
        }

        if (restaurantData.menuItems.isNotEmpty()) {
            item {
                MenuSection(
                    restaurantId = restaurantData.restaurant.id,
                    items = restaurantData.menuItems,
                    onItemClick = onFoodItemClick,
                    onSellAllClick = onSellAllClick
                )
            }
        }

        if (restaurantData.reviews.isNotEmpty()) {
            item {
                ReviewsSection(
                    reviews = restaurantData.reviews,
                    onViewAllClick = onViewAllReviews
                )
            }
        }

        if (restaurantData.combos.isNotEmpty()) {
            item {
                ComboSection(
                    combos = restaurantData.combos,
                    onComboClick = onComboClick
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RestaurantImageHeader(
    restaurant: RestaurantDetails,
    onBackClick: () -> Unit,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        GlideImage(
            model = restaurant.imageUrl,
            contentDescription = stringResource(R.string.restaurant_image),
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop,
            failure = placeholder(R.drawable.default_food),
            loading = placeholder(R.drawable.default_food)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) heartFavoriteColor() else Color.White
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackgroundColor()),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = restaurant.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = cardContentColor(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoItem(
                        icon = R.drawable.ic_bn_home,
                        text = restaurant.deliveryTime,
                        color = primaryColor()
                    )

                    InfoItem(
                        icon = R.drawable.ic_user_location,
                        text = restaurant.distance,
                        color = primaryColor()
                    )

                    InfoItem(
                        icon = R.drawable.ic_offer_percentage,
                        text = restaurant.priceRange,
                        color = primaryColor()
                    )
                }
            }
        }
    }
}

@Composable
fun InfoItem(
    icon: Int,
    text: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = color
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            color = textSecondaryColor()
        )
    }
}

@Composable
fun RestaurantInfoRows(
    restaurant: RestaurantDetails,
    voucherCount: Int
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {

        InfoRow(
            icon = R.drawable.ic_offer_percentage,
            title = stringResource(R.string.discount_voucher_for_restaurant, voucherCount),
            iconColor = primaryColor(),
            showArrow = true,
            onClick = { }
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = dividerColor(),
            modifier = Modifier.padding(vertical = 12.dp)
        )

        InfoRow(
            icon = R.drawable.material_starpurple500_sharp,
            title = "${restaurant.rating} (${restaurant.reviewCount} reviews)",
            iconColor = starRatingColor(),
            showArrow = false,
            onClick = { }
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = dividerColor(),
            modifier = Modifier.padding(vertical = 12.dp)
        )

        InfoRow(
            icon = R.drawable.ic_help,
            title = "Delivery on ${restaurant.deliveryTime}",
            iconColor = primaryColor(),
            showArrow = false,
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun InfoRow(
    icon: Int,
    title: String,
    iconColor: Color,
    showArrow: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = iconColor
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            color = onBackgroundColor(),
            modifier = Modifier.weight(1f)
        )

        if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.arrow),
                tint = textSecondaryColor(),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ForYouSection(
    restaurantId: String,
    items: List<RestaurantMenuItem>,
    onItemClick: (String) -> Unit,
    onViewAllClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.for_you),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor()
            )

            TextButton(onClick = {
                onViewAllClick(restaurantId)
            }) {
                Text(
                    text = stringResource(id = R.string.view_all),
                    fontSize = 14.sp,
                    color = primaryColor()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(320.dp),
            userScrollEnabled = false
        ) {
            items(items) { item ->
                ForYouItemCard(
                    item = item,
                    onClick = { onItemClick(item.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ForYouItemCard(
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
        Column {
            GlideImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop,
                failure = placeholder(R.drawable.default_food),
                loading = placeholder(R.drawable.default_food)
            )
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = cardContentColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        modifier = Modifier.size(12.dp),
                        tint = starRatingColor()
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "${item.rating} (${item.reviewCount})",
                        fontSize = 12.sp,
                        color = textSecondaryColor()
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "${item.price.toInt()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = priceTextColor()
                    )
                }
            }
        }
    }
}

@Composable
fun MenuSection(
    restaurantId: String,
    items: List<RestaurantMenuItem>,
    onItemClick: (String) -> Unit,
    onSellAllClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.menu_section),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = onBackgroundColor()
        )

        Spacer(modifier = Modifier.height(16.dp))

        items.take(2).forEach { item ->
            MenuItemCard(
                item = item,
                onClick = { onItemClick(item.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (items.size > 2) {
            SeeAllButton(
                text = stringResource(id = R.string.see_all),
                onClick = { onSellAllClick(restaurantId) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MenuItemCard(
    item: RestaurantMenuItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                color = onBackgroundColor()
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
                Text(
                    text = "${item.price.toInt()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = priceTextColor()
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    modifier = Modifier.size(12.dp),
                    tint = starRatingColor()
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "${item.rating} (${item.reviewCount})",
                    fontSize = 12.sp,
                    color = textSecondaryColor()
                )
            }
        }
    }
}

@Composable
fun ReviewsSection(
    reviews: List<RestaurantReview>,
    onViewAllClick: (List<RestaurantReview>) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.reviews),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = onBackgroundColor()
            )

            // Only show "View All" if there are more than 3 reviews
            if (reviews.size > 3) {
                Text(
                    text = stringResource(id = R.string.view_all),
                    fontSize = 14.sp,
                    color = primaryColor(),
                    modifier = Modifier.clickable {
                        onViewAllClick(reviews)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (reviews.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_reviews_yet),
                    color = textSecondaryColor(),
                    fontSize = 14.sp
                )
            }
        } else {
            // Show only first 3 reviews
            val reviewsToShow = reviews.take(3)

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(reviewsToShow) { review ->
                    ReviewCard(review = review)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ReviewCard(review: RestaurantReview) {
    Card(
        modifier = Modifier.width(250.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlideImage(
                    model = review.userAvatar,
                    contentDescription = stringResource(R.string.user_image),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    failure = placeholder(R.drawable.default_food),
                    loading = placeholder(R.drawable.default_food)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = review.userName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = cardContentColor()
                    )

                    Text(
                        text = review.timeAgo,
                        fontSize = 12.sp,
                        color = textSecondaryColor()
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(R.string.star),
                            modifier = Modifier.size(12.dp),
                            tint = if (index < review.rating) starRatingColor() else emptyStarColor()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = review.comment,
                fontSize = 14.sp,
                color = cardContentColor(),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ComboSection(
    combos: List<RestaurantCombo>,
    onComboClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.combo),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = onBackgroundColor()
        )

        Spacer(modifier = Modifier.height(16.dp))

        combos.forEach { combo ->
            ComboItemCard(
                combo = combo,
                onClick = { onComboClick(combo.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ComboItemCard(
    combo: RestaurantCombo,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = combo.imageUrl,
            contentDescription = combo.name,
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
                text = combo.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = onBackgroundColor()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = combo.description,
                fontSize = 12.sp,
                color = textSecondaryColor(),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${combo.price.toInt()}",
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
                    text = "${combo.rating} (${combo.reviewCount})",
                    fontSize = 12.sp,
                    color = textSecondaryColor()
                )
            }
        }
    }
}

@Preview
@Composable
fun RestaurantDetailsScreenPreView() {
    RestaurantDetailsScreen()
}