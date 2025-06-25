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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tastybuds.R
import com.app.tastybuds.domain.model.MenuItem
import com.app.tastybuds.domain.model.SearchRestaurant
import com.app.tastybuds.domain.model.SearchResult
import com.app.tastybuds.domain.model.SearchResultType
import com.app.tastybuds.ui.resturants.state.SearchUiState
import com.app.tastybuds.ui.theme.ComponentSizes
import com.app.tastybuds.ui.theme.Spacing
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.badgeText
import com.app.tastybuds.ui.theme.bodyMedium
import com.app.tastybuds.ui.theme.bottomSheetBackgroundColor
import com.app.tastybuds.ui.theme.bottomSheetContentColor
import com.app.tastybuds.ui.theme.buttonText
import com.app.tastybuds.ui.theme.cardBackgroundColor
import com.app.tastybuds.ui.theme.cardContentColor
import com.app.tastybuds.ui.theme.dialogTitle
import com.app.tastybuds.ui.theme.errorColor
import com.app.tastybuds.ui.theme.errorText
import com.app.tastybuds.ui.theme.favoriteColor
import com.app.tastybuds.ui.theme.foodItemName
import com.app.tastybuds.ui.theme.foodItemPrice
import com.app.tastybuds.ui.theme.infoColor
import com.app.tastybuds.ui.theme.inputHint
import com.app.tastybuds.ui.theme.inputText
import com.app.tastybuds.ui.theme.onBackgroundColor
import com.app.tastybuds.ui.theme.onPrimaryColor
import com.app.tastybuds.ui.theme.onSurfaceColor
import com.app.tastybuds.ui.theme.onSurfaceVariantColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.ratingColor
import com.app.tastybuds.ui.theme.restaurantCuisine
import com.app.tastybuds.ui.theme.restaurantDeliveryTime
import com.app.tastybuds.ui.theme.restaurantName
import com.app.tastybuds.ui.theme.restaurantRating
import com.app.tastybuds.ui.theme.subTitle
import com.app.tastybuds.ui.theme.successColor
import com.app.tastybuds.ui.theme.surfaceVariantColor
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.app.tastybuds.ui.theme.warningColor
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder

data class SortOption(
    val id: String,
    val displayTextRes: Int // String resource ID
)

object SortOptionIds {
    const val RELEVANCE = "relevance"
    const val RATING = "rating"
    const val DISTANCE = "distance"
    const val DELIVERY_TIME = "delivery_time"
    const val PRICE_LOW_TO_HIGH = "price_low_to_high"
    const val PRICE_HIGH_TO_LOW = "price_high_to_low"
}

object SearchDimensions {
    val headerPadding = Spacing.medium
    val backButtonSize = ComponentSizes.iconSmall
    val searchFieldCornerRadius = ComponentSizes.cornerRadius
    val filterIconSize = ComponentSizes.iconMedium
    val filterChipCornerRadius = ComponentSizes.cornerRadius
    val filterChipPaddingHorizontal = Spacing.medium
    val filterChipPaddingVertical = Spacing.small
    val filterChipSpacing = Spacing.small
    val sortIconSize = ComponentSizes.iconSmall
    val restaurantImageSize = 80.dp
    val restaurantImageCornerRadius = Spacing.small
    val menuItemImageSize = 60.dp
    val menuItemIndentStart = 104.dp
    val badgeChipCornerRadius = Spacing.xs
    val badgeChipPaddingHorizontal = Spacing.xs
    val badgeChipPaddingVertical = 2.dp
    val badgeChipSpacing = Spacing.xs
    val ratingIconSize = 14.dp
    val cardElevation = 0.dp
    val bottomSheetDragHandleWidth = 32.dp
    val bottomSheetDragHandleHeight = 4.dp
    val bottomSheetCornerRadius = Spacing.medium
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    showBottomSheet: Boolean,
    sortOptions: List<SortOption>,
    selectedSortId: String,
    onSortSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = bottomSheetState,
            containerColor = bottomSheetBackgroundColor(),
            contentColor = bottomSheetContentColor(),
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(vertical = Spacing.small),
                    color = onSurfaceColor().copy(alpha = 0.4f),
                    shape = RoundedCornerShape(SearchDimensions.bottomSheetCornerRadius)
                ) {
                    Box(
                        modifier = Modifier.size(
                            width = SearchDimensions.bottomSheetDragHandleWidth,
                            height = SearchDimensions.bottomSheetDragHandleHeight
                        )
                    )
                }
            }
        ) {
            Column(
                modifier = Modifier.padding(Spacing.medium)
            ) {
                Text(
                    text = stringResource(R.string.sort_by),
                    style = dialogTitle(),
                    color = onSurfaceColor(),
                    modifier = Modifier.padding(bottom = Spacing.medium)
                )

                sortOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSortSelected(option.id)
                                onDismiss()
                            }
                            .padding(vertical = Spacing.small),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option.id == selectedSortId,
                            onClick = {
                                onSortSelected(option.id)
                                onDismiss()
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = primaryColor(),
                                unselectedColor = onSurfaceColor().copy(alpha = 0.6f)
                            )
                        )

                        Spacer(modifier = Modifier.width(Spacing.medium))

                        Text(
                            text = stringResource(option.displayTextRes),
                            style = bodyMedium(),
                            color = onSurfaceColor()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.medium))
            }
        }
    }
}

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
    var selectedSortId by remember { mutableStateOf(SortOptionIds.RELEVANCE) }
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    val sortOptions = remember {
        listOf(
            SortOption(SortOptionIds.RELEVANCE, R.string.sort_relevance),
            SortOption(SortOptionIds.RATING, R.string.rating_high_to_low),
            SortOption(SortOptionIds.DISTANCE, R.string.sort_distance),
            SortOption(SortOptionIds.DELIVERY_TIME, R.string.delivery_time_asc),
            SortOption(SortOptionIds.PRICE_LOW_TO_HIGH, R.string.price_low_to_high),
            SortOption(SortOptionIds.PRICE_HIGH_TO_LOW, R.string.price_high_to_low)
        )
    }

    val filteredResults by remember {
        derivedStateOf {
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
    }

    val sortedAndFilteredResults = remember(filteredResults, selectedSortId) {
        when (selectedSortId) {
            SortOptionIds.RATING -> {
                filteredResults.sortedWith(compareByDescending { it.restaurant?.rating ?: 0f })
            }

            SortOptionIds.DISTANCE -> {
                filteredResults.sortedWith(compareBy {
                    val distanceStr =
                        it.restaurant?.distance?.replace("[^0-9.]".toRegex(), "") ?: "999999"
                    distanceStr.toFloatOrNull() ?: Float.MAX_VALUE
                })
            }

            SortOptionIds.DELIVERY_TIME -> {
                filteredResults.sortedWith(compareBy {
                    val timeStr =
                        it.restaurant?.deliveryTime?.replace("[^0-9]".toRegex(), "") ?: "999999"
                    timeStr.toIntOrNull() ?: Int.MAX_VALUE
                })
            }

            SortOptionIds.PRICE_LOW_TO_HIGH -> {
                filteredResults.sortedWith(compareBy {
                    if (it.menuItemList.isNotEmpty()) {
                        it.menuItemList.minByOrNull { menuItem -> menuItem.price }?.price
                            ?: Float.MAX_VALUE
                    } else {
                        Float.MAX_VALUE
                    }
                })
            }

            SortOptionIds.PRICE_HIGH_TO_LOW -> {
                filteredResults.sortedWith(compareByDescending {
                    if (it.menuItemList.isNotEmpty()) {
                        it.menuItemList.maxByOrNull { menuItem -> menuItem.price }?.price ?: 0f
                    } else {
                        0f
                    }
                })
            }

            SortOptionIds.RELEVANCE -> filteredResults

            else -> filteredResults
        }
    }

    LaunchedEffect(initialSearchTerm) {
        viewModel.searchMenuItems(initialSearchTerm)
    }

    LaunchedEffect(searchText) {
        viewModel.searchMenuItems(searchText)
    }

    LaunchedEffect(Unit) {
        if (initialSearchTerm.isEmpty()) {
            viewModel.searchMenuItems("")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor())
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
            selectedSortId = selectedSortId,
            sortOptions = sortOptions,
            onFilterSelected = { filter ->
                selectedFilters = if (selectedFilters.contains(filter)) {
                    selectedFilters - filter
                } else {
                    selectedFilters + filter
                }
            },
            onSortBySelected = { sortId ->
                selectedSortId = sortId
            }
        )

        SearchResultsContent(
            searchText = searchText,
            uiState = uiState,
            searchResult = sortedAndFilteredResults,
            selectedFilters = selectedFilters,
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
            .padding(SearchDimensions.headerPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(SearchDimensions.backButtonSize)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.cd_back_button),
                tint = onBackgroundColor()
            )
        }

        Spacer(modifier = Modifier.width(Spacing.medium))

        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            placeholder = {
                Text(
                    text = stringResource(R.string.search_for_food_restaurants),
                    style = inputHint(),
                    color = textSecondaryColor()
                )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = onClearClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.clear),
                            tint = textSecondaryColor()
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = surfaceVariantColor(),
                unfocusedContainerColor = surfaceVariantColor(),
                focusedTextColor = onSurfaceColor(),
                unfocusedTextColor = onSurfaceColor()
            ),
            shape = RoundedCornerShape(SearchDimensions.searchFieldCornerRadius),
            textStyle = inputText(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        )

        Spacer(modifier = Modifier.width(Spacing.medium))

        IconButton(onClick = onFilterClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter),
                contentDescription = stringResource(R.string.cd_filter),
                tint = primaryColor(),
                modifier = Modifier.size(SearchDimensions.filterIconSize)
            )
        }
    }
}

@Composable
fun FilterRow(
    searchResults: List<SearchResult>,
    selectedFilters: Set<String>,
    selectedSortId: String,
    sortOptions: List<SortOption>,
    onFilterSelected: (String) -> Unit,
    onSortBySelected: (String) -> Unit
) {
    var showSortDialog by remember { mutableStateOf(false) }

    val availableFilters = remember(searchResults) {
        searchResults
            .mapNotNull { it.restaurant }
            .flatMap { it.badges }
            .distinct()
            .sorted()
    }

    val selectedSortDisplayText = remember(selectedSortId) {
        sortOptions.find { it.id == selectedSortId }?.displayTextRes ?: R.string.sort_by
    }

    if (availableFilters.isNotEmpty()) {
        Column {
            LazyRow(
                modifier = Modifier.padding(horizontal = Spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(SearchDimensions.filterChipSpacing)
            ) {
                item {
                    FilterChip(
                        text = stringResource(selectedSortDisplayText),
                        isSelected = selectedSortId != SortOptionIds.RELEVANCE,
                        onClick = { showSortDialog = true },
                        showArrow = true
                    )
                }

                items(availableFilters) { filter ->
                    FilterChip(
                        text = filter,
                        isSelected = selectedFilters.contains(filter),
                        onClick = { onFilterSelected(filter) },
                        showArrow = false
                    )
                }
            }
        }

        SortBottomSheet(
            showBottomSheet = showSortDialog,
            sortOptions = sortOptions,
            selectedSortId = selectedSortId,
            onSortSelected = onSortBySelected,
            onDismiss = { showSortDialog = false }
        )
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    showArrow: Boolean = false
) {
    val backgroundColor = if (isSelected) primaryColor() else surfaceVariantColor()
    val textColor = if (isSelected) onPrimaryColor() else onSurfaceVariantColor()

    Row(
        modifier = Modifier
            .background(
                backgroundColor,
                RoundedCornerShape(SearchDimensions.filterChipCornerRadius)
            )
            .clickable { onClick() }
            .padding(
                horizontal = SearchDimensions.filterChipPaddingHorizontal,
                vertical = SearchDimensions.filterChipPaddingVertical
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = buttonText(),
            color = textColor
        )

        if (showArrow) {
            Spacer(modifier = Modifier.width(Spacing.xs))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.sort_dropdown),
                tint = textColor,
                modifier = Modifier.size(SearchDimensions.sortIconSize)
            )
        }
    }
}

@Composable
fun SearchResultsContent(
    searchText: String,
    uiState: SearchUiState,
    searchResult: List<SearchResult>,
    selectedFilters: Set<String>,
    onResultClick: (String, SearchResultType) -> Unit
) {
    Column(
        modifier = Modifier.padding(
            horizontal = Spacing.medium,
            vertical = Spacing.small
        )
    ) {
        if (searchText.isNotBlank() || selectedFilters.isNotEmpty()) {
            val restaurantCount = searchResult.size
            val menuItemsCount = searchResult.sumOf { it.menuItemList.size }
            val totalCount = restaurantCount + menuItemsCount

            val resultsText = if (searchText.isNotBlank()) {
                "$totalCount results for \"$searchText\""
            } else if (selectedFilters.isNotEmpty()) {
                "$totalCount results filtered by: ${selectedFilters.joinToString(", ")}"
            } else {
                "$totalCount results"
            }

            Text(
                text = resultsText,
                style = subTitle(),
                color = onBackgroundColor(),
                modifier = Modifier.padding(vertical = Spacing.small)
            )
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryColor())
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.error_place_holder, uiState.error),
                        style = errorText(),
                        color = errorColor()
                    )
                }
            }

            searchResult.isEmpty() && searchText.isNotBlank() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_results_found_for, searchText),
                        style = bodyMedium(),
                        color = textSecondaryColor()
                    )
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    searchResult.forEach { searchResult ->
                        // Show restaurant card first
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
                                onClick = {
                                    onResultClick(menuItem.id, SearchResultType.FOOD_ITEM)
                                }
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
    restaurant: SearchRestaurant,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = SearchDimensions.cardElevation)
    ) {
        Row(
            modifier = Modifier.padding(Spacing.medium)
        ) {
            GlideImage(
                model = restaurant.imageUrl,
                contentDescription = stringResource(R.string.cd_restaurant_image),
                modifier = Modifier
                    .size(SearchDimensions.restaurantImageSize)
                    .clip(RoundedCornerShape(SearchDimensions.restaurantImageCornerRadius)),
                contentScale = ContentScale.Crop,
                failure = placeholder(R.drawable.default_food),
                loading = placeholder(R.drawable.default_food)
            )

            Spacer(modifier = Modifier.width(Spacing.medium))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = restaurant.name,
                    style = restaurantName(),
                    color = cardContentColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = restaurant.description,
                    style = restaurantCuisine(),
                    color = textSecondaryColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Row(
                    modifier = Modifier.padding(top = Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = restaurant.deliveryTime,
                        style = restaurantDeliveryTime(),
                        color = textSecondaryColor()
                    )

                    Spacer(modifier = Modifier.width(Spacing.small))

                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.cd_rating_star),
                        tint = ratingColor(),
                        modifier = Modifier.size(SearchDimensions.ratingIconSize)
                    )

                    Text(
                        text = restaurant.rating.toString(),
                        style = restaurantRating(),
                        color = textSecondaryColor(),
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }

                if (restaurant.badges.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(SearchDimensions.badgeChipSpacing),
                        modifier = Modifier.padding(top = Spacing.small)
                    ) {
                        items(restaurant.badges.take(2)) { badge ->
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
fun MenuItemCard(
    menuItem: MenuItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                start = SearchDimensions.menuItemIndentStart,
                end = Spacing.medium,
                top = Spacing.xs,
                bottom = Spacing.xs
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = menuItem.imageUrl,
            contentDescription = stringResource(R.string.cd_food_image),
            modifier = Modifier
                .size(SearchDimensions.menuItemImageSize)
                .clip(RoundedCornerShape(SearchDimensions.restaurantImageCornerRadius)),
            contentScale = ContentScale.Crop,
            failure = placeholder(R.drawable.default_food),
            loading = placeholder(R.drawable.default_food)
        )

        Spacer(modifier = Modifier.width(Spacing.medium))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = menuItem.name,
                style = foodItemName(),
                color = cardContentColor(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${stringResource(R.string.currency_symbol)}${menuItem.price.toInt()}",
                style = foodItemPrice(),
                color = primaryColor(),
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun BadgeChip(text: String) {
    val backgroundColor = when (text.lowercase()) {
        "freeship" -> successColor()
        "near you" -> primaryColor()
        "favorite" -> favoriteColor()
        "partner" -> infoColor()
        "popular" -> warningColor()
        else -> textSecondaryColor()
    }

    Text(
        text = text,
        style = badgeText(),
        color = onPrimaryColor(),
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(SearchDimensions.badgeChipCornerRadius))
            .padding(
                horizontal = SearchDimensions.badgeChipPaddingHorizontal,
                vertical = SearchDimensions.badgeChipPaddingVertical
            )
    )
}