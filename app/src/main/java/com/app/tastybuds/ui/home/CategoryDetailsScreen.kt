package com.app.tastybuds.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.PrimaryColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import kotlinx.coroutines.delay

data class FilterOption(
    val id: String,
    val name: String,
    val isSelected: Boolean = false
)

data class RestaurantItem(
    val id: String,
    val name: String,
    val cuisine: String,
    val deliveryTime: String,
    val rating: Float,
    val imageRes: Int,
    val badges: List<String> = emptyList()
)

@Composable
fun FoodListingScreen(
    categoryName: String = "Fast Food",
    onBackClick: () -> Unit = {},
    onRestaurantClick: (String) -> Unit = {}
) {
    var selectedSortBy by remember { mutableStateOf("Sort by") }
    val filterOptions = remember {
        listOf(
            FilterOption("freeship", "Freeship"),
            FilterOption("favorite", "Favorite"),
            FilterOption("near_you", "Near you"),
            FilterOption("partner", "Partner")
        )
    }
    var selectedFilters by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CategoryHeader(
            categoryName = categoryName,
            onBackClick = onBackClick
        )

        FilterSection(
            selectedSortBy = selectedSortBy,
            onSortByClick = { selectedSortBy = it },
            filterOptions = filterOptions,
            selectedFilters = selectedFilters,
            onFilterClick = { filterId ->
                selectedFilters = if (selectedFilters.contains(filterId)) {
                    selectedFilters - filterId
                } else {
                    selectedFilters + filterId
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Restaurant List
            items(getRestaurantList()) { restaurant ->
                RestaurantCard(
                    restaurant = restaurant,
                    onClick = { onRestaurantClick(restaurant.id) }
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = { /* Load more */ }
                    ) {
                        Text(
                            text = "See all",
                            color = PrimaryColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                FeaturedBannerSection()
            }

            item {
                RecommendedSection(onRestaurantClick = onRestaurantClick)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CategoryHeader(
    categoryName: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = categoryName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun FilterSection(
    selectedSortBy: String,
    onSortByClick: (String) -> Unit,
    filterOptions: List<FilterOption>,
    selectedFilters: Set<String>,
    onFilterClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = false,
                onClick = { /* Handle sort dropdown */ },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedSortBy,
                            fontSize = 14.sp,
                            color = PrimaryColor
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Sort dropdown",
                            modifier = Modifier.size(16.dp),
                            tint = PrimaryColor
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    labelColor = PrimaryColor
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = true,
                    borderColor = PrimaryColor,
                    selectedBorderColor = PrimaryColor
                ),
                shape = RoundedCornerShape(20.dp)
            )
        }

        items(filterOptions) { filter ->
            val isSelected = selectedFilters.contains(filter.id)
            FilterChip(
                selected = isSelected,
                onClick = { onFilterClick(filter.id) },
                label = {
                    Text(
                        text = filter.name,
                        fontSize = 14.sp,
                        color = if (isSelected) Color.White else Color.Gray
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFFF5F5F5),
                    selectedContainerColor = PrimaryColor,
                    labelColor = Color.Gray,
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp)
            )
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
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = restaurant.imageRes),
                contentDescription = restaurant.name,
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
                    text = restaurant.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = restaurant.cuisine,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Delivery time and rating
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = restaurant.deliveryTime,
                        fontSize = 12.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFFFFC107)
                    )

                    Spacer(modifier = Modifier.width(2.dp))

                    Text(
                        text = restaurant.rating.toString(),
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Badges Row
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

@Composable
fun BadgeChip(text: String) {
    val backgroundColor = when (text.lowercase()) {
        "freeship" -> Color(0xFF4CAF50)
        "near you" -> PrimaryColor
        "popular" -> Color(0xFF9C27B0)
        else -> Color(0xFF4CAF50)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeaturedBannerSection() {
    val banners = remember { getFeaturedBanners() }
    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000) // 4 seconds delay
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
            FeaturedBannerCard(banner = banners[page])
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

@Composable
fun FeaturedBannerCard(banner: FeaturedBanner) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = banner.imageRes),
                contentDescription = banner.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
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
                    text = banner.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = banner.subtitle,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun RecommendedSection(onRestaurantClick: (String) -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val recommendedRestaurants = getRecommendedRestaurants()

        recommendedRestaurants.forEach { restaurant ->
            RestaurantCard(
                restaurant = restaurant,
                onClick = { onRestaurantClick(restaurant.id) }
            )

            if (restaurant != recommendedRestaurants.last()) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

data class FeaturedBanner(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageRes: Int
)

// Dummy Data Functions
private fun getRestaurantList(): List<RestaurantItem> {
    return listOf(
        RestaurantItem(
            id = "1",
            name = "Hana Chicken",
            cuisine = "Fried Chicken",
            deliveryTime = "15 mins",
            rating = 4.8f,
            imageRes = R.drawable.default_food,
            badges = listOf("Freeship", "Near you")
        ),
        RestaurantItem(
            id = "2",
            name = "Bamsu Restaurant",
            cuisine = "Chicken Salad, Sandwich & Desserts",
            deliveryTime = "35 mins",
            rating = 4.1f,
            imageRes = R.drawable.default_food,
            badges = listOf("Freeship")
        ),
        RestaurantItem(
            id = "3",
            name = "Neighbor Milk",
            cuisine = "Dairy Drinks & Smoothies",
            deliveryTime = "35 mins",
            rating = 4.1f,
            imageRes = R.drawable.default_food,
            badges = listOf("Freeship")
        )
    )
}

private fun getFeaturedBanners(): List<FeaturedBanner> {
    return listOf(
        FeaturedBanner(
            id = "1",
            title = "Tasty",
            subtitle = "dishes",
            imageRes = R.drawable.default_food
        ),
        FeaturedBanner(
            id = "2",
            title = "Special",
            subtitle = "offers",
            imageRes = R.drawable.default_food
        ),
        FeaturedBanner(
            id = "3",
            title = "Fresh",
            subtitle = "ingredients",
            imageRes = R.drawable.default_food
        ),
        FeaturedBanner(
            id = "4",
            title = "Best",
            subtitle = "quality",
            imageRes = R.drawable.default_food
        )
    )
}

private fun getRecommendedRestaurants(): List<RestaurantItem> {
    return listOf(
        RestaurantItem(
            id = "rec_1",
            name = "Mr. John Tapas",
            cuisine = "Best Tapas in Town",
            deliveryTime = "35 mins",
            rating = 4.1f,
            imageRes = R.drawable.default_food,
            badges = listOf("Freeship")
        ),
        RestaurantItem(
            id = "rec_2",
            name = "Pasta Paradise",
            cuisine = "Italian Cuisine & Pizza",
            deliveryTime = "25 mins",
            rating = 4.5f,
            imageRes = R.drawable.default_food,
            badges = listOf("Freeship", "Popular")
        )
    )
}

@Preview(showBackground = true)
@Composable
fun FoodListingScreenPreview() {
    FoodListingScreen()
}