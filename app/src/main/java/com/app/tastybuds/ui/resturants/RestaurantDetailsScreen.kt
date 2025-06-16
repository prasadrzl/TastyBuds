package com.app.tastybuds.ui.resturants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.app.tastybuds.ui.theme.PrimaryColor
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
                LoadingContent()
            }

            uiState.error != null -> {
                ErrorContent(
                    error = uiState.error!!,
                    onRetry = { viewModel.retry() },
                    onBackClick = onBackClick
                )
            }

            uiState.restaurantData != null -> {
                RestaurantDetailsContent(
                    restaurantData = uiState.restaurantData!!,
                    isFavorite = uiState.isFavorite,
                    voucherCount = uiState.voucherCount,
                    onBackClick = onBackClick,
                    onFavoriteClick = { viewModel.toggleFavorite() },
                    onFoodItemClick = onFoodItemClick,
                    onComboClick = onComboClick
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_help),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(onClick = onBackClick) {
                    Text("Go Back")
                }

                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
private fun RestaurantDetailsContent(
    restaurantData: RestaurantDetailsData,
    isFavorite: Boolean,
    voucherCount: Int,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onFoodItemClick: (String) -> Unit,
    onComboClick: (String) -> Unit
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
                    items = restaurantData.forYouItems,
                    onItemClick = onFoodItemClick
                )
            }
        }

        if (restaurantData.menuItems.isNotEmpty()) {
            item {
                MenuSection(
                    items = restaurantData.menuItems,
                    onItemClick = onFoodItemClick
                )
            }
        }

        if (restaurantData.reviews.isNotEmpty()) {
            item {
                ReviewsSection(reviews = restaurantData.reviews)
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
            contentDescription = "Restaurant Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop,
            failure = placeholder(R.drawable.default_food),
            loading = placeholder(R.drawable.default_food)
        )

        // Top Controls
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
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Restaurant name centered
                Text(
                    text = restaurant.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Info items centered
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoItem(
                        icon = R.drawable.ic_bn_home,
                        text = restaurant.deliveryTime,
                        color = PrimaryColor
                    )

                    InfoItem(
                        icon = R.drawable.ic_user_location,
                        text = restaurant.distance,
                        color = PrimaryColor
                    )

                    InfoItem(
                        icon = R.drawable.ic_offer_percentage,
                        text = restaurant.priceRange,
                        color = PrimaryColor
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
            color = Color.Gray
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
            icon = R.drawable.material_starpurple500_sharp,
            title = "${restaurant.rating} (${restaurant.reviewCount} reviews)",
            iconColor = Color(0xFFFFC107),
            showArrow = true,
            onClick = { /* Navigate to reviews */ }
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFE0E0E0),
            modifier = Modifier.padding(vertical = 12.dp)
        )

        InfoRow(
            icon = R.drawable.ic_offer_percentage,
            title = "$voucherCount discount voucher for restaurant",
            iconColor = PrimaryColor,
            showArrow = true,
            onClick = { /* Navigate to vouchers */ }
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFE0E0E0),
            modifier = Modifier.padding(vertical = 12.dp)
        )

        InfoRow(
            icon = R.drawable.ic_help,
            title = "Delivery on ${restaurant.deliveryTime}",
            iconColor = PrimaryColor,
            showArrow = true,
            onClick = { /* Navigate to delivery info */ }
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
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Arrow",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ForYouSection(
    items: List<RestaurantMenuItem>,
    onItemClick: (String) -> Unit
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
                text = "For you",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            TextButton(onClick = { }) {
                Text(
                    text = "View all",
                    fontSize = 14.sp,
                    color = PrimaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(320.dp)
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    color = Color.Black,
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
                        tint = Color(0xFFFFC107)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "${item.rating} (${item.reviewCount})",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "$${item.price.toInt()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun MenuSection(
    items: List<RestaurantMenuItem>,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Menu",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
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
            TextButton(
                onClick = { /* See all */ },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "See all",
                    fontSize = 14.sp,
                    color = PrimaryColor
                )
            }
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
                color = Color.Black
            )

            if (item.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${item.price.toInt()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    modifier = Modifier.size(12.dp),
                    tint = Color(0xFFFFC107)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "${item.rating} (${item.reviewCount})",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ReviewsSection(reviews: List<RestaurantReview>) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reviews",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            SeeAllButton(
                text = "See all",
                onClick = { }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(reviews) { review ->
                ReviewCard(review = review)
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    contentDescription = "User Image",
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
                        color = Color.Black
                    )

                    Text(
                        text = review.timeAgo,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            modifier = Modifier.size(12.dp),
                            tint = if (index < review.rating) Color(0xFFFFC107) else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = review.comment,
                fontSize = 14.sp,
                color = Color.Black,
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
            text = "Combo",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
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
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = combo.description,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${combo.price.toInt()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    modifier = Modifier.size(12.dp),
                    tint = Color(0xFFFFC107)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "${combo.rating} (${combo.reviewCount})",
                    fontSize = 12.sp,
                    color = Color.Gray
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