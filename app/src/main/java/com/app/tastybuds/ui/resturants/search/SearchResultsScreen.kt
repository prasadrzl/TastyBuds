package com.app.tastybuds.ui.resturants.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tastybuds.R
import com.app.tastybuds.data.SearchResult
import com.app.tastybuds.data.SearchResultType
import com.app.tastybuds.ui.resturants.state.SearchUiState
import com.app.tastybuds.ui.theme.PrimaryColor
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

@Composable
fun SearchResultsScreen(
    initialSearchTerm: String = "",
    onBackClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onResultClick: (String, SearchResultType) -> Unit = { _, _ -> },
    viewModel: SearchResultsViewModel = hiltViewModel()
) {
    var searchText by remember { mutableStateOf(initialSearchTerm) }
    var selectedFilters by remember { mutableStateOf(setOf<String>()) }
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    val filteredResults = remember(uiState.searchResults, selectedFilters) {
        if (selectedFilters.isEmpty()) {
            uiState.searchResults
        } else {
            uiState.searchResults.filter { searchResult ->
                searchResult.restaurant?.badges?.any { badge ->
                    selectedFilters.contains(badge)
                } == true
            }
        }
    }

    LaunchedEffect(initialSearchTerm) {
        viewModel.searchMenuItems(initialSearchTerm)
    }

    LaunchedEffect(searchText) {
            viewModel.searchMenuItems(searchText)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SearchHeader(
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onBackClick = onBackClick,
            onClearClick = { searchText = "" },
            onFilterClick = onFilterClick,
            focusRequester = focusRequester
        )

        FilterRow(
            searchResults = uiState.searchResults,
            selectedFilters = selectedFilters,
            onFilterSelected = { filter ->
                selectedFilters = if (selectedFilters.contains(filter)) {
                    selectedFilters - filter
                } else {
                    selectedFilters + filter
                }
            }
        )

        SearchResultsContent(
            searchText = searchText,
            uiState = uiState,
            searchResult = filteredResults,
            onResultClick = onResultClick
        )
    }
}

@Composable
fun SearchHeader(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    onFilterClick: () -> Unit,
    focusRequester: FocusRequester
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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

        Spacer(modifier = Modifier.width(12.dp))

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
                    fontSize = 16.sp
                )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = onClearClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = Color.Gray
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5)
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Icon(
            painter = painterResource(id = R.drawable.ic_filter),
            contentDescription = "Filter",
            tint = PrimaryColor,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun FilterRow(
    searchResults: List<SearchResult>,
    selectedFilters: Set<String>,
    onFilterSelected: (String) -> Unit
) {
    val filters = searchResults
        .mapNotNull { it.restaurant }
        .flatMap { it.badges }
        .distinct()

    val allFilters = listOf("Sort by") + filters

    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(allFilters) { filter ->
            FilterChip(
                text = filter,
                isSelected = if (filter == "Sort by") true else selectedFilters.contains(filter),
                onClick = {
                    if (filter != "Sort by") {
                        onFilterSelected(filter)
                    }
                }
            )
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFFFE7D2) else Color(0xFFF5F5F5)
    val textColor = if (isSelected) PrimaryColor else Color.Gray

    Row(
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        if (text == "Sort by") {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun SearchResultsContent(
    searchText: String,
    uiState: SearchUiState,
    searchResult: List<SearchResult>,
    onResultClick: (String, SearchResultType) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (searchText.isNotBlank()) {
            val menuItemsCount = uiState.searchResults.sumOf { it.menuItemList.size }
            Text(
                text = "$menuItemsCount results for \"$searchText\"",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            }

            uiState.searchResults.isEmpty() && searchText.isNotBlank() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No results found for \"$searchText\"",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.searchResults.forEach { searchResult ->
                        searchResult.restaurant?.let { restaurant ->
                            item {
                                RestaurantCard(
                                    restaurant = restaurant,
                                    onClick = {
                                        onResultClick(
                                            restaurant.id,
                                            SearchResultType.RESTAURANT
                                        )
                                    }
                                )
                            }
                        }

                        items(searchResult.menuItemList) { menuItem ->
                            MenuItemCard(
                                menuItem = menuItem,
                                onClick = { onResultClick(menuItem.id, SearchResultType.FOOD_ITEM) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RestaurantCard(
    restaurant: com.app.tastybuds.data.Restaurant,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            GlideImage(
                model = restaurant.imageUrl,
                contentDescription = restaurant.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                failure = placeholder(R.drawable.default_food)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = restaurant.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = restaurant.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = restaurant.deliveryTime,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(14.dp)
                    )

                    Text(
                        text = restaurant.rating.toString(),
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    items(restaurant.badges.take(2)) { badge ->
                        BadgeChip(text = badge)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MenuItemCard(
    menuItem: com.app.tastybuds.data.MenuItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(start = 104.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = menuItem.imageUrl,
            contentDescription = menuItem.name,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            failure = placeholder(R.drawable.default_food)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = menuItem.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${menuItem.price.toInt()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun BadgeChip(text: String) {
    val backgroundColor = when (text.lowercase()) {
        "freeship" -> Color(0xFF4CAF50)
        "near you" -> PrimaryColor
        "favorite" -> Color(0xFFE91E63)
        "partner" -> Color(0xFF2196F3)
        else -> Color.Gray
    }

    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = Color.White,
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}