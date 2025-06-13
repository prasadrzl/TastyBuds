package com.app.tastybuds.ui.resturants

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.ArrowBack
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
import com.app.tastybuds.R
import com.app.tastybuds.data.SearchResultType
import com.app.tastybuds.ui.resturants.search.SearchResultsViewModel
import com.app.tastybuds.ui.theme.PrimaryColor
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

// Data Models
data class SearchFilterOption(
    val id: String,
    val name: String,
    val isSelected: Boolean = false
)

@Composable
fun SearchResultsScreen(
    initialSearchTerm: String = "",
    onBackClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onResultClick: (String, SearchResultType) -> Unit = { _, _ -> },
    viewModel: SearchResultsViewModel = hiltViewModel()
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
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Mock data for demonstration - you can replace with viewModel data
    val searchResults = remember(searchText) {
        getSearchResults(searchText)
    }

    val resultsCount = searchResults.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ActiveSearchHeader(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onBackClick = onBackClick,
            onCloseClick = onCloseClick,
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (searchText.isNotBlank()) {
                item {
                    Text(
                        text = "$resultsCount results for \"$searchText\"",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            if (searchResults.isEmpty() && searchText.isNotBlank()) {
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
            } else {
                items(searchResults) { result ->
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
}

@Composable
fun ActiveSearchHeader(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
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
                    imageVector = Icons.Default.ArrowBack,
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
                    painter = painterResource(id = R.drawable.ic_free_shiping),
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

@OptIn(ExperimentalGlideComposeApi::class)
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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                if (result.imageUrl.isNotEmpty()) {
                    GlideImage(
                        model = result.imageUrl,
                        contentDescription = result.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        failure = placeholder(R.drawable.default_food)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.default_food),
                        contentDescription = result.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = result.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = result.subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = result.deliveryTime,
                        fontSize = 12.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )

                    if (result.rating > 0) {
                        Text(
                            text = " • ",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            modifier = Modifier.size(12.dp),
                            tint = Color(0xFFFFB400)
                        )

                        Text(
                            text = result.rating.toString(),
                            fontSize = 12.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }

                if (result.badges.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(result.badges) { badge ->
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
fun FoodItemResultCard(
    result: SearchResultItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            if (result.imageUrl.isNotEmpty()) {
                GlideImage(
                    model = result.imageUrl,
                    contentDescription = result.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    failure = placeholder(R.drawable.default_food)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.default_food),
                    contentDescription = result.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = result.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (result.price.isNotEmpty()) {
                Text(
                    text = result.price,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

// Mock data function - replace with actual data from ViewModel
fun getSearchResults(searchTerm: String): List<SearchResultItem> {
    val allResults = listOf(
        SearchResultItem(
            id = "1",
            type = SearchResultType.RESTAURANT,
            name = "Hana Chicken",
            subtitle = "Fried Chicken",
            deliveryTime = "15 mins",
            rating = 4.8f,
            badges = listOf("Freeship", "Near you")
        ),
        SearchResultItem(
            id = "2",
            type = SearchResultType.FOOD_ITEM,
            name = "Fried Chicken",
            price = "$10"
        ),
        SearchResultItem(
            id = "3",
            type = SearchResultType.FOOD_ITEM,
            name = "Fried Chicken & Potatos",
            price = "$26"
        ),
        SearchResultItem(
            id = "4",
            type = SearchResultType.RESTAURANT,
            name = "Bamsu Restaurant",
            subtitle = "Chicken Salad & Sandwich",
            deliveryTime = "35 mins",
            rating = 4.1f,
            badges = listOf("Freeship", "Near you")
        ),
        SearchResultItem(
            id = "5",
            type = SearchResultType.FOOD_ITEM,
            name = "Chicken Sandwich",
            price = "$26"
        ),
        SearchResultItem(
            id = "6",
            type = SearchResultType.FOOD_ITEM,
            name = "Crunchy Fried Chicken Balls",
            price = "$30"
        )
    )

    return if (searchTerm.isBlank()) {
        allResults
    } else {
        allResults.filter { result ->
            result.name.contains(searchTerm, ignoreCase = true) ||
                    result.subtitle.contains(searchTerm, ignoreCase = true)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchResultsScreenPreview() {
    SearchResultsScreen(
        initialSearchTerm = "Fried Chicken"
    )
}