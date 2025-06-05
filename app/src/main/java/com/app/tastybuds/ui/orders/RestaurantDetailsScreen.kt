// RestaurantDetailsScreen.kt - Restaurant Details Food Listing
package com.app.tastybuds.ui.restaurant

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.PrimaryColor

// Data Models
data class RestaurantInfo(
    val id: String,
    val name: String,
    val timing: String,
    val distance: String,
    val priceRange: String,
    val rating: Float,
    val reviewCount: Int,
    val imageRes: Int,
    val badges: List<RestaurantBadge>
)

data class RestaurantBadge(
    val text: String,
    val backgroundColor: Color
)

data class FoodMenuItem(
    val id: String,
    val name: String,
    val description: String = "",
    val price: Int,
    val rating: Float,
    val reviewCount: Int,
    val imageRes: Int
)

data class ReviewItem(
    val id: String,
    val userName: String,
    val userImage: Int,
    val timeAgo: String,
    val rating: Float,
    val comment: String
)

data class ComboItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val rating: Float,
    val reviewCount: Int,
    val imageRes: Int
)

@Composable
fun RestaurantDetailsScreen(
    restaurantId: String = "1",
    onBackClick: () -> Unit = {},
    onFoodItemClick: (String) -> Unit = {},
    onComboClick: (String) -> Unit = {}
) {
    val restaurant = getRestaurantInfo()
    val forYouItems = getForYouItems()
    val menuItems = getMenuItems()
    val reviews = getReviews()
    val combos = getCombos()

    var isFavorite by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                // Restaurant Image Header with Overlapping Card
                RestaurantImageHeader(
                    restaurant = restaurant,
                    onBackClick = onBackClick,
                    isFavorite = isFavorite,
                    onFavoriteClick = { isFavorite = !isFavorite }
                )
            }

            item {
                // Info Rows (Reviews, Vouchers, Delivery)
                RestaurantInfoRows()
            }

            item {
                // For You Section
                ForYouSection(
                    items = forYouItems,
                    onItemClick = onFoodItemClick
                )
            }

            item {
                // Menu Section
                MenuSection(
                    items = menuItems,
                    onItemClick = onFoodItemClick
                )
            }

            item {
                // Reviews Section
                ReviewsSection(reviews = reviews)
            }

            item {
                // Combo Section
                ComboSection(
                    combos = combos,
                    onComboClick = onComboClick
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Space for bottom navigation
            }
        }

    }
}

@Composable
fun RestaurantImageHeader(
    restaurant: RestaurantInfo,
    onBackClick: () -> Unit,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp) // Increased height to accommodate overlapping card
    ) {
        // Restaurant Image
        Image(
            painter = painterResource(id = restaurant.imageRes),
            contentDescription = "Restaurant Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp), // Image takes less height to allow card overlap
            contentScale = ContentScale.Crop
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

        // Overlapping Restaurant Info Card
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
                // Badges centered at top of card
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    restaurant.badges.forEach { badge ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = badge.backgroundColor)
                        ) {
                            Text(
                                text = badge.text,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

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
                        icon = R.drawable.ic_bn_home, // Clock icon
                        text = restaurant.timing,
                        color = PrimaryColor
                    )

                    InfoItem(
                        icon = R.drawable.ic_user_location, // Location icon
                        text = restaurant.distance,
                        color = PrimaryColor
                    )

                    InfoItem(
                        icon = R.drawable.ic_offer_percentage, // Money icon
                        text = restaurant.priceRange,
                        color = PrimaryColor
                    )
                }
            }
        }
    }
}

// Remove the separate RestaurantInfoCard composable since it's now integrated

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
fun RestaurantInfoRows() {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        // Rating Row
        InfoRow(
            icon = R.drawable.material_starpurple500_sharp,
            title = "4.5 (289 reviews)",
            iconColor = Color(0xFFFFC107),
            showArrow = true,
            onClick = { /* Navigate to reviews */ }
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFE0E0E0),
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // Voucher Row
        InfoRow(
            icon = R.drawable.ic_offer_percentage,
            title = "2 discount voucher for restaurant",
            iconColor = PrimaryColor,
            showArrow = true,
            onClick = { /* Navigate to vouchers */ }
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFE0E0E0),
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // Delivery Row
        InfoRow(
            icon = R.drawable.ic_help, // Delivery icon
            title = "Delivery on 20 mins",
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
    items: List<FoodMenuItem>,
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

            TextButton(onClick = { /* View all */ }) {
                Text(
                    text = "View all",
                    fontSize = 14.sp,
                    color = PrimaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2x2 Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.height(320.dp) // Fixed height for 2 rows
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

@Composable
fun ForYouItemCard(
    item: FoodMenuItem,
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
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
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
                        text = "$${item.price}",
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
    items: List<FoodMenuItem>,
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

        items.forEach { item ->
            MenuItemCard(
                item = item,
                onClick = { onItemClick(item.id) }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

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

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun MenuItemCard(
    item: FoodMenuItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.name,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
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
                    text = "$${item.price}",
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
fun ReviewsSection(reviews: List<ReviewItem>) {
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

            TextButton(onClick = { /* View all */ }) {
                Text(
                    text = "View all",
                    fontSize = 14.sp,
                    color = PrimaryColor
                )
            }
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

@Composable
fun ReviewCard(review: ReviewItem) {
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
                Image(
                    painter = painterResource(id = review.userImage),
                    contentDescription = "User Image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
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
    combos: List<ComboItem>,
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

@Composable
fun ComboItemCard(
    combo: ComboItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = combo.imageRes),
            contentDescription = combo.name,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
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
                    text = "$${combo.price}",
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

// Dummy Data Functions
private fun getRestaurantInfo(): RestaurantInfo {
    return RestaurantInfo(
        id = "1",
        name = "Hana Chicken",
        timing = "6am - 9pm",
        distance = "2 km",
        priceRange = "$5 - $50",
        rating = 4.5f,
        reviewCount = 289,
        imageRes = R.drawable.default_food,
        badges = listOf(
            RestaurantBadge("Deal $1", Color(0xFF4CAF50)),
            RestaurantBadge("Near you", PrimaryColor)
        )
    )
}

private fun getForYouItems(): List<FoodMenuItem> {
    return listOf(
        FoodMenuItem("1", "Fried Chicken", "", 15, 4.5f, 99, R.drawable.default_food),
        FoodMenuItem("2", "Chicken Salad", "", 15, 4.5f, 99, R.drawable.default_food),
        FoodMenuItem("3", "Spicy Chicken", "", 15, 4.5f, 99, R.drawable.default_food),
        FoodMenuItem("4", "Fried Potatos", "", 15, 4.5f, 99, R.drawable.default_food)
    )
}

private fun getMenuItems(): List<FoodMenuItem> {
    return listOf(
        FoodMenuItem("5", "Sauté Chicken Rice", "Sauté chicken, Rice", 15, 4.5f, 99, R.drawable.default_food),
        FoodMenuItem("6", "Chicken Burger", "Fried chicken, Cheese & Burger", 15, 4.5f, 99, R.drawable.default_food)
    )
}

private fun getReviews(): List<ReviewItem> {
    return listOf(
        ReviewItem("1", "Jinny Oslin", R.drawable.default_food, "A day ago", 5.0f, "Quick delivery, good dishes. I love the chicken burger."),
        ReviewItem("2", "John Doe", R.drawable.default_food, "2 days ago", 4.0f, "Fresh ingredients and great taste!")
    )
}

private fun getCombos(): List<ComboItem> {
    return listOf(
        ComboItem("1", "Combo B", "Fried Chicken, Chicken Rice & Salad", 25, 4.5f, 60, R.drawable.default_food),
        ComboItem("2", "Combo B", "Fried Chicken (Small) & Potatos", 19, 4.6f, 76, R.drawable.default_food)
    )
}

@Preview(showBackground = true)
@Composable
fun RestaurantDetailsScreenPreview() {
    RestaurantDetailsScreen()
}