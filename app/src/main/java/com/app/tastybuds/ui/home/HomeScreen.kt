// HomeScreen.kt - Replace your existing HomeScreen
package com.app.tastybuds.ui.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.PrimaryColor
import kotlinx.coroutines.delay

// Data Models
data class DealBannerItem(
    val id: String,
    val title: String,
    val price: String,
    val description: String,
    val imageRes: Int,
    val backgroundColor: Color = PrimaryColor
)

data class DealItem(
    val id: String,
    val title: String,
    val price: String,
    val originalPrice: String? = null,
    val imageRes: Int,
    val badgeText: String? = null
)

data class CategoryItem(
    val id: String,
    val name: String,
    val iconRes: Int,
    val backgroundColor: Color
)

data class CollectionItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageRes: Int,
    val badgeText: String? = null
)

data class RestaurantItem(
    val id: String,
    val name: String,
    val cuisine: String,
    val rating: Float,
    val deliveryTime: String,
    val imageRes: Int,
    val badgeText: String? = null,
    val isFavorite: Boolean = false
)

@Composable
fun HomeScreen(
    onCategoryClick: (String, String) -> Unit = { _, _ -> },
    onProfileClick: () -> Unit = {},
    onSearchClick: (String) -> Unit = {},
    onRestaurantClick: (String) -> Unit = {} // ADD THIS LINE
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Deal Banner Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            DealBannerSection()
        }

        // Categories Section
        item {
            CategoriesSection(onCategoryClick = onCategoryClick)
        }

        // Voucher Section
        item {
            VoucherSection()
        }

        // Collections Section
        item {
            CollectionsSection()
        }

        // Recommended Section
        item {
            RecommendedSection(onRestaurantClick = onRestaurantClick)
        }

        // Sale Section
        item {
            SaleSection()
        }
    }
}

@Composable
fun DealBannerSection() {
    val dealBanners = remember {
        getDealBanners()
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { dealBanners.size }
    )

    // Auto-scroll effect
    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000) // 3 seconds delay
            val nextPage = (pagerState.currentPage + 1) % dealBanners.size
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
            DealBannerCard(
                dealBanner = dealBanners[page],
                onClick = { /* Handle banner click */ }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Page indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(dealBanners.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) PrimaryColor else Color.Gray.copy(alpha = 0.5f)
                        )
                )
                if (index < dealBanners.size - 1) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}

@Composable
fun DealBannerCard(
    dealBanner: DealBannerItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = dealBanner.backgroundColor)
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
                    text = dealBanner.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dealBanner.price,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dealBanner.description,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            Image(
                painter = painterResource(id = dealBanner.imageRes),
                contentDescription = dealBanner.title,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun CategoriesSection(onCategoryClick: (String, String) -> Unit = { _, _ -> }) {
    val categories = remember {
        listOf(
            CategoryItem("1", "Rice", R.drawable.ic_bn_home, Color(0xFFFFF3E0)),
            CategoryItem("2", "Healthy", R.drawable.ic_bn_favorite, Color(0xFFE8F5E8)),
            CategoryItem("3", "Drink", R.drawable.ic_bn_inbox, Color(0xFFE3F2FD)),
            CategoryItem("4", "Fastfood", R.drawable.ic_bn_order, Color(0xFFFFF3E0)),
            CategoryItem("5", "Pizza", R.drawable.ic_offer_percentage, Color(0xFFFFEBEE)),
            CategoryItem("6", "Burger", R.drawable.ic_ewallet, Color(0xFFF3E5F5)),
            CategoryItem("7", "Dessert", R.drawable.ic_help, Color(0xFFE8F5E8)),
            CategoryItem("8", "Salad", R.drawable.ic_settings, Color(0xFFE1F5FE)),
            CategoryItem("9", "Coffee", R.drawable.ic_search, Color(0xFFFFF8E1)),
            CategoryItem("10", "Soup", R.drawable.ic_user_location, Color(0xFFEDE7F6))
        )
    }

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

@Composable
fun CategoryCard(
    category: CategoryItem,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .width(64.dp) // Fixed width for consistent spacing
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(category.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = category.iconRes),
                contentDescription = category.name,
                modifier = Modifier.size(32.dp),
                tint = PrimaryColor
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
fun VoucherSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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
                text = "You have 5 voucher here",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "View all",
                fontSize = 12.sp,
                color = PrimaryColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CollectionsSection() {
    val collections = getDummyCollections()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Collections",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "View all",
                fontSize = 14.sp,
                color = PrimaryColor,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Static 2x2 Grid using Column and Rows
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // First Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StaticCollectionCard(
                    collection = collections[0],
                    onClick = { /* Handle collection click */ },
                    modifier = Modifier.weight(1f)
                )
                StaticCollectionCard(
                    collection = collections[1],
                    onClick = { /* Handle collection click */ },
                    modifier = Modifier.weight(1f)
                )
            }

            // Second Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StaticCollectionCard(
                    collection = collections[2],
                    onClick = { /* Handle collection click */ },
                    modifier = Modifier.weight(1f)
                )
                StaticCollectionCard(
                    collection = collections[3],
                    onClick = { /* Handle collection click */ },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StaticCollectionCard(
    collection: CollectionItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp) // Reduced height to match design
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)) // Border for each item
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side - Small square image
            Box {
                Image(
                    painter = painterResource(id = collection.imageRes),
                    contentDescription = collection.title,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                // Badge on top-left of image
                collection.badgeText?.let { badge ->
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

            // Right side - Text content
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
fun RecommendedSection(onRestaurantClick: (String) -> Unit = {}) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recommended for you",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "View all",
                fontSize = 14.sp,
                color = PrimaryColor,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.padding(start = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(getDummyRestaurants()) { restaurant ->
                RestaurantCard(
                    restaurant = restaurant,
                    onClick = { onRestaurantClick(restaurant.id) } // CHANGE THIS LINE
                )
            }
        }
    }
}

@Composable
fun RestaurantCard(
    restaurant: RestaurantItem,
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
                Image(
                    painter = painterResource(id = restaurant.imageRes),
                    contentDescription = restaurant.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentScale = ContentScale.Crop
                )

                restaurant.badgeText?.let { badge ->
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
fun SaleSection() {
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
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.padding(start = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(getDummySaleItems()) { deal ->
                SaleCard(
                    deal = deal,
                    onClick = { /* Handle deal click */ }
                )
            }
        }
    }
}

@Composable
fun SaleCard(
    deal: DealItem,
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
                Image(
                    painter = painterResource(id = deal.imageRes),
                    contentDescription = deal.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentScale = ContentScale.Crop
                )

                deal.badgeText?.let { badge ->
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
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    }
                }
            }
        }
    }
}

// Dummy Data Functions
private fun getDealBanners(): List<DealBannerItem> {
    return listOf(
        DealBannerItem(
            id = "1",
            title = "Join Party",
            price = "$1",
            description = "off $20",
            imageRes = R.drawable.profile_img,
            backgroundColor = PrimaryColor
        ),
        DealBannerItem(
            id = "2",
            title = "Super Deal",
            price = "$5",
            description = "off $50",
            imageRes = R.drawable.profile_img,
            backgroundColor = Color(0xFF4CAF50)
        ),
        DealBannerItem(
            id = "3",
            title = "Flash Sale",
            price = "50%",
            description = "off selected items",
            imageRes = R.drawable.profile_img,
            backgroundColor = Color(0xFF9C27B0)
        ),
        DealBannerItem(
            id = "4",
            title = "Free Delivery",
            price = "$0",
            description = "delivery fee",
            imageRes = R.drawable.profile_img,
            backgroundColor = Color(0xFF2196F3)
        )
    )
}

private fun getDummyCollections(): List<CollectionItem> {
    return listOf(
        CollectionItem("1", "FREESHIP", "18 Places • 4.8", R.drawable.profile_img, "Free"),
        CollectionItem("2", "DEAL $1", "8 Places • 4.3", R.drawable.profile_img, "Deal $1"),
        CollectionItem("3", "NEAR YOU", "12 Places • 4.2", R.drawable.profile_img),
        CollectionItem("4", "POPULAR", "25 Places • 4.5", R.drawable.profile_img, "Popular")
    )
}

private fun getDummyRestaurants(): List<RestaurantItem> {
    return listOf(
        RestaurantItem("1", "Bomua Restaurant", "Chinese, Spicy • Desserts", 4.5f, "25 mins • 4.1 km", R.drawable.profile_img, "Popular"),
        RestaurantItem("2", "PT Wen Coffee", "Coffee, Drinks", 4.2f, "15 mins • 2.8 km", R.drawable.profile_img),
        RestaurantItem("3", "Green Salad", "Healthy, Vegetarian", 4.8f, "20 mins • 3.2 km", R.drawable.profile_img, "Healthy")
    )
}

private fun getDummySaleItems(): List<DealItem> {
    return listOf(
        DealItem("1", "Green Salad", "Deal $5", "$8", R.drawable.profile_img, "Deal $5"),
        DealItem("2", "Little Milk", "Deal $3", "$6", R.drawable.profile_img, "Popular"),
        DealItem("3", "Potato Chips", "Deal $1", "$4", R.drawable.profile_img, "Deal $1")
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}