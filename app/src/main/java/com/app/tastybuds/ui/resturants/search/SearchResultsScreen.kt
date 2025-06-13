package com.app.tastybuds.ui.resturants.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.app.tastybuds.R
import com.app.tastybuds.data.SearchResult
import com.app.tastybuds.data.SearchResultType
import com.app.tastybuds.ui.resturants.state.SearchUiState
import com.app.tastybuds.ui.theme.PrimaryColor

data class SearchFilterOption(
    val id: String,
    val name: String,
    val isSelected: Boolean = false
)

@Composable
fun SearchResultsScreen(
    initialSearchTerm: String = "",
    viewModel: SearchViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onResultClick: (String, SearchResultType) -> Unit = { _, _ -> }
) {
    var searchText by remember { mutableStateOf(initialSearchTerm) }
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

    val focusRequester = remember { FocusRequester() }

    // Get UI state from ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // Trigger search when searchText changes
    LaunchedEffect(searchText) {
        if (searchText.isNotBlank()) {
            viewModel.searchMenuItems(searchText)
        }
    }

    // Auto-focus on search field
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        // Search initial term if provided
        if (initialSearchTerm.isNotBlank()) {
            viewModel.searchMenuItems(initialSearchTerm)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ActiveSearchHeader(
            searchText = searchText,
            onSearchTextChange = {
                searchText = it
                // Clear results if search is empty
                if (it.isBlank()) {
                    viewModel.clearResults()
                }
            },
            onBackClick = onBackClick,
            onFilterClick = onFilterClick,
            focusRequester = focusRequester
        )

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

        // Updated content based on UI state
        SearchResultsContent(
            uiState = uiState,
            searchText = searchText,
            onResultClick = onResultClick
        )
    }
}

@Composable
private fun SearchResultsContent(
    uiState: SearchUiState,
    searchText: String,
    onResultClick: (String, SearchResultType) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        when {
            uiState.isLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryColor,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            uiState.isEmpty && searchText.isNotBlank() -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No results found",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Text(
                                text = "Try searching for something else",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            uiState.searchResults.isNotEmpty() -> {
                // Result count
                if (searchText.isNotBlank()) {
                    item {
                        val menuItemsCount = uiState.searchResults.count { it.type == SearchResultType.FOOD_ITEM }
                        Text(
                            text = "$menuItemsCount results for \"$searchText\"",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                // Group results by restaurant
                val groupedResults = groupSearchResults(uiState.searchResults)

                items(groupedResults) { group ->
                    SearchResultGroup(
                        restaurantResult = group.restaurant,
                        menuItems = group.menuItems,
                        onResultClick = onResultClick
                    )
                }
            }
        }
    }
}

// Helper function to group results
private fun groupSearchResults(results: List<SearchResult>): List<SearchResultGroup> {
    val groups = mutableListOf<SearchResultGroup>()
    var currentRestaurant: SearchResult? = null
    var currentMenuItems = mutableListOf<SearchResult>()

    results.forEach { result ->
        when (result.type) {
            SearchResultType.RESTAURANT -> {
                // Save previous group if exists
                currentRestaurant?.let { restaurant ->
                    groups.add(SearchResultGroup(restaurant, currentMenuItems.toList()))
                }
                // Start new group
                currentRestaurant = result
                currentMenuItems = mutableListOf()
            }
            SearchResultType.FOOD_ITEM -> {
                currentMenuItems.add(result)
            }
        }
    }

    // Add last group
    currentRestaurant?.let { restaurant ->
        groups.add(SearchResultGroup(restaurant, currentMenuItems.toList()))
    }

    return groups
}

data class SearchResultGroup(
    val restaurant: SearchResult,
    val menuItems: List<SearchResult>
)

@Composable
private fun SearchResultGroup(
    restaurantResult: SearchResult,
    menuItems: List<SearchResult>,
    onResultClick: (String, SearchResultType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Restaurant Header
            RestaurantHeader(
                restaurant = restaurantResult,
                onClick = { onResultClick(restaurantResult.id, restaurantResult.type) }
            )

            // Menu Items under this restaurant
            if (menuItems.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    menuItems.forEachIndexed { index, menuItem ->
                        if (index > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        MenuItemRow(
                            menuItem = menuItem,
                            onClick = { onResultClick(menuItem.id, menuItem.type) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun RestaurantHeader(
    restaurant: SearchResult,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = restaurant.imageUrl.ifEmpty { R.drawable.default_food },
            contentDescription = restaurant.name,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        ) {
            it.placeholder(R.drawable.default_food)
                .error(R.drawable.default_food)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = restaurant.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            if (restaurant.subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = restaurant.subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (restaurant.deliveryTime.isNotEmpty()) {
                    Text(
                        text = restaurant.deliveryTime,
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }

                if (restaurant.rating > 0) {
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
            }

            if (restaurant.badges.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(restaurant.badges) { badge ->
                        SearchBadgeChip(text = badge)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun MenuItemRow(
    menuItem: SearchResult,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = menuItem.imageUrl.ifEmpty { R.drawable.default_food },
            contentDescription = menuItem.name,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        ) {
            it.placeholder(R.drawable.default_food)
                .error(R.drawable.default_food)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = menuItem.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            if (menuItem.price.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = menuItem.price,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun ActiveSearchHeader(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit,
    focusRequester: FocusRequester
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column {
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

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(
                        text = "Search for food, restaurants...",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchTextChange("") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFF8F8F8),
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    cursorColor = PrimaryColor
                ),
                shape = RoundedCornerShape(25.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                    }
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onFilterClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search), // Keep your existing icon or add filter icon
                    contentDescription = "Filter",
                    tint = PrimaryColor
                )
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFE0E0E0)
        )
    }
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
        item {
            FilterChip(
                selected = false,
                onClick = { },
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

@Preview(showBackground = true)
@Composable
fun SearchResultsScreenPreview() {
    SearchResultsScreen(initialSearchTerm = "Chicken")
}