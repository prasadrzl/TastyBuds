// SearchResultsScreen.kt - Food Search Results
package com.app.tastybuds.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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

// Data Models
data class SearchFilterOption(
    val id: String,
    val name: String,
    val isSelected: Boolean = false
)

data class SearchResultItem(
    val id: String,
    val type: SearchResultType,
    val name: String,
    val subtitle: String = "",
    val price: String = "",
    val deliveryTime: String = "",
    val rating: Float = 0f,
    val imageRes: Int,
    val badges: List<String> = emptyList()
)

enum class SearchResultType {
    RESTAURANT,
    FOOD_ITEM
}

@Composable
fun SearchResultsScreen(
    searchTerm: String = "Fried Chicken",
    resultsCount: Int = 359,
    onBackClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onResultClick: (String, SearchResultType) -> Unit = { _, _ -> }
) {
    var selectedSortBy by remember { mutableStateOf("Sort by") }
    val filterOptions = remember {
        listOf(
            SearchFilterOption("freeship", "Freeship"),
            SearchFilterOption("favorite", "Favorite"),
            SearchFilterOption("near_you", "Near you"),
            SearchFilterOption("partner", "Partner")
        )
    }
    var selectedFilters by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Search Header
        SearchHeader(
            searchTerm = searchTerm,
            onBackClick = onBackClick,
            onCloseClick = onCloseClick,
            onFilterClick = onFilterClick
        )
        
        // Filter Section
        SearchFilterSection(
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
        
        // Results Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Results count
            item {
                Text(
                    text = "$resultsCount results for \"$searchTerm\"",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            // Search results
            items(getSearchResults()) { result ->
                when (result.type) {
                    SearchResultType.RESTAURANT -> {
                        RestaurantResultCard(
                            result = result,
                            onClick = { onResultClick(result.id, result.type) }
                        )
                    }
                    SearchResultType.FOOD_ITEM -> {
                        FoodItemResultCard(
                            result = result,
                            onClick = { onResultClick(result.id, result.type) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchHeader(
    searchTerm: String,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bn_home), // Replace with back arrow
                contentDescription = "Back",
                tint = Color.Black
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Search term
        Text(
            text = searchTerm,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        
        // Close button
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.Gray
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Filter button
        IconButton(
            onClick = onFilterClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search), // Replace with filter icon
                contentDescription = "Filter",
                tint = PrimaryColor
            )
        }
    }
    
    // Divider
    HorizontalDivider(
        thickness = 1.dp,
        color = Color(0xFFE0E0E0)
    )
}

@Composable
fun SearchFilterSection(
    selectedSortBy: String,
    onSortByClick: (String) -> Unit,
    filterOptions: List<SearchFilterOption>,
    selectedFilters: Set<String>,
    onFilterClick: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Sort by dropdown
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
                    selected = true,
                    enabled = true,
                    borderColor = PrimaryColor,
                    selectedBorderColor = PrimaryColor
                ),
                shape = RoundedCornerShape(20.dp)
            )
        }
        
        // Filter options
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
fun RestaurantResultCard(
    result: SearchResultItem,
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
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Main restaurant info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Restaurant image
                Image(
                    painter = painterResource(id = result.imageRes),
                    contentDescription = result.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Restaurant details
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = result.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = result.subtitle,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Rating and delivery time
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = result.deliveryTime,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                        
                        if (result.rating > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                modifier = Modifier.size(14.dp),
                                tint = Color(0xFFFFC107)
                            )
                            
                            Spacer(modifier = Modifier.width(2.dp))
                            
                            Text(
                                text = result.rating.toString(),
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Badges
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(result.badges) { badge ->
                            SearchBadgeChip(text = badge)
                        }
                    }
                }
            }
            
            // Food items from this restaurant
            if (result.name == "Hana Chicken") {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    FoodMenuItem(
                        name = "Fried Chicken",
                        price = "$10",
                        imageRes = R.drawable.profile_img
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    FoodMenuItem(
                        name = "Fried Chicken & Potatos",
                        price = "$26",
                        imageRes = R.drawable.profile_img
                    )
                }
            }
            
            if (result.name == "Bamsu Restaurant") {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    FoodMenuItem(
                        name = "Chicken Sandwich",
                        price = "$26",
                        imageRes = R.drawable.profile_img
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    FoodMenuItem(
                        name = "Crunchy Fried Chicken Balls",
                        price = "$30",
                        imageRes = R.drawable.profile_img
                    )
                }
            }
        }
    }
}

@Composable
fun FoodMenuItem(
    name: String,
    price: String,
    imageRes: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = name,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = price,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun FoodItemResultCard(
    result: SearchResultItem,
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
                painter = painterResource(id = result.imageRes),
                contentDescription = result.name,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = result.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                if (result.price.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = result.price,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBadgeChip(text: String) {
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

// Dummy Data
private fun getSearchResults(): List<SearchResultItem> {
    return listOf(
        SearchResultItem(
            id = "1",
            type = SearchResultType.RESTAURANT,
            name = "Hana Chicken",
            subtitle = "Fried Chicken",
            deliveryTime = "15 mins",
            rating = 4.8f,
            imageRes = R.drawable.profile_img,
            badges = listOf("Freeship", "Near you")
        ),
        SearchResultItem(
            id = "2",
            type = SearchResultType.RESTAURANT,
            name = "Bamsu Restaurant",
            subtitle = "Chicken Salad & Sandwich",
            deliveryTime = "35 mins",
            rating = 4.1f,
            imageRes = R.drawable.profile_img,
            badges = listOf("Freeship", "Near you")
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SearchResultsScreenPreview() {
    SearchResultsScreen()
}