package com.app.tastybuds.ui.resturants.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.app.tastybuds.ui.theme.bodySmall
import com.app.tastybuds.ui.theme.bottomSheetBackgroundColor
import com.app.tastybuds.ui.theme.bottomSheetContentColor
import com.app.tastybuds.ui.theme.buttonText
import com.app.tastybuds.ui.theme.cardBackgroundColor
import com.app.tastybuds.ui.theme.cardContentColor
import com.app.tastybuds.ui.theme.dialogTitle
import com.app.tastybuds.ui.theme.errorColor
import com.app.tastybuds.ui.theme.errorText
import com.app.tastybuds.ui.theme.foodItemName
import com.app.tastybuds.ui.theme.foodItemPrice
import com.app.tastybuds.ui.theme.infoColor
import com.app.tastybuds.ui.theme.inputHint
import com.app.tastybuds.ui.theme.inputText
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
import java.util.Locale

data class SortOption(
    val id: String,
    val displayTextRes: Int
)

object SortOptionIds {
    const val RELEVANCE = "relevance"
    const val RATING = "rating"
    const val DISTANCE = "distance"
    const val DELIVERY_TIME = "delivery_time"
    const val PRICE_LOW_TO_HIGH = "price_low_to_high"
}

object SearchDimensions {
    val headerPadding = Spacing.medium
    val backButtonSize = ComponentSizes.iconSmall
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

@Composable
fun SearchResultsScreen(
    initialSearchTerm: String = "",
    onBackClick: () -> Unit = {},
    onResultClick: (String, SearchResultType) -> Unit = { _, _ -> },
    viewModel: SearchResultsViewModel = hiltViewModel()
) {
    var searchText by remember { mutableStateOf(initialSearchTerm) }
    var showSortBottomSheet by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    val sortOptions = remember {
        listOf(
            SortOption(SortOptionIds.RELEVANCE, R.string.sort_relevance),
            SortOption(SortOptionIds.RATING, R.string.rating_high_to_low),
            SortOption(SortOptionIds.DISTANCE, R.string.sort_distance),
            SortOption(SortOptionIds.DELIVERY_TIME, R.string.delivery_time_asc),
            SortOption(SortOptionIds.PRICE_LOW_TO_HIGH, R.string.price_low_to_high),
        )
    }

    val availableFilters = remember(uiState.searchResults) {
        viewModel.getAvailableFilters()
    }

    val resultsCount = remember(uiState.filteredAndSortedResults) {
        viewModel.getMenuItemsCount()
    }

    val currentSortText = remember(uiState.selectedSortId) {
        viewModel.getCurrentSortDisplayText(sortOptions)
    }

    LaunchedEffect(initialSearchTerm) {
        viewModel.searchMenuItems(initialSearchTerm)
    }

    LaunchedEffect(searchText) {
        viewModel.searchMenuItems(searchText)
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
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
            onClearClick = {
                searchText = ""
                viewModel.resetSearch()
            },
            focusRequester = focusRequester
        )

        ResultsInfoRow(
            resultsCount = resultsCount,
            onClearAllFilters = { viewModel.clearAllFilters() },
            hasActiveFilters = uiState.selectedFilters.isNotEmpty()
        )

        FilterAndSortRow(
            availableFilters = availableFilters,
            selectedFilters = uiState.selectedFilters,
            currentSortText = currentSortText,
            onFilterSelected = { filter ->
                viewModel.toggleFilter(filter)
            },
            onSortClick = { showSortBottomSheet = true }
        )

        SearchResultsContent(
            searchText = searchText,
            uiState = uiState,
            searchResult = uiState.filteredAndSortedResults,
            onResultClick = onResultClick
        )
    }

    if (showSortBottomSheet) {
        SortBottomSheet(
            showBottomSheet = true,
            sortOptions = sortOptions,
            selectedSortId = uiState.selectedSortId,
            onSortSelected = { sortId ->
                viewModel.updateSortOption(sortId)
                showSortBottomSheet = false
            },
            onDismiss = { showSortBottomSheet = false }
        )
    }
}

@Composable
private fun SearchHeader(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    focusRequester: FocusRequester
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = backgroundColor()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SearchDimensions.headerPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_arrow),
                    contentDescription = stringResource(R.string.back),
                    tint = onSurfaceColor(),
                    modifier = Modifier.size(SearchDimensions.backButtonSize)
                )
            }

            Spacer(modifier = Modifier.width(Spacing.small))

            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_for_food_restaurants),
                        style = inputHint(),
                        color = onSurfaceVariantColor()
                    )
                },
                textStyle = inputText(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor(),
                    unfocusedBorderColor = onSurfaceVariantColor(),
                    focusedTextColor = onSurfaceColor(),
                    unfocusedTextColor = onSurfaceColor()
                ),
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = onClearClick) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear),
                                tint = onSurfaceVariantColor()
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
            )
        }
    }
}

@Composable
private fun ResultsInfoRow(
    resultsCount: Int,
    onClearAllFilters: () -> Unit,
    hasActiveFilters: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium, vertical = Spacing.small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.items_found, resultsCount),
            style = bodySmall(),
            color = onSurfaceVariantColor()
        )

        if (hasActiveFilters) {
            TextButton(
                onClick = onClearAllFilters,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = primaryColor()
                )
            ) {
                Text(
                    text = stringResource(R.string.clear_all),
                    style = buttonText()
                )
            }
        }
    }
}

@Composable
private fun FilterAndSortRow(
    availableFilters: List<String>,
    selectedFilters: Set<String>,
    currentSortText: Int,
    onFilterSelected: (String) -> Unit,
    onSortClick: () -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        contentPadding = PaddingValues(vertical = Spacing.small)
    ) {
        item {
            OutlinedButton(
                onClick = onSortClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = primaryColor()
                ),
                border = BorderStroke(1.dp, primaryColor()),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = stringResource(currentSortText),
                    style = buttonText(),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.sort_dropdown),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        items(availableFilters) { filter ->
            FilterChip(
                enabled = true,
                selected = selectedFilters.contains(filter),
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter.replace("_", " ").replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase() else it.toString()
                        },
                        style = bodySmall(),
                        fontSize = 12.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = primaryColor(),
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = onSurfaceColor()
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(36.dp)
            )
        }
    }
}

@Composable
private fun SearchResultsContent(
    searchText: String,
    uiState: SearchUiState,
    searchResult: List<SearchResult>,
    onResultClick: (String, SearchResultType) -> Unit
) {
    when {
        uiState.isLoading -> {
            LoadingContent()
        }

        uiState.error != null -> {
            ErrorContent(
                error = uiState.error,
                onRetry = { }
            )
        }

        searchResult.isEmpty() && searchText.isNotEmpty() -> {
            EmptyResultsContent(searchText = searchText)
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                items(searchResult) { result ->
                    SearchResultCard(
                        searchResult = result,
                        onResultClick = onResultClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    searchResult: SearchResult,
    onResultClick: (String, SearchResultType) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                searchResult.restaurant?.let { restaurant ->
                    onResultClick(restaurant.id, SearchResultType.RESTAURANT)
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor()
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = SearchDimensions.cardElevation
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.medium)
        ) {
            searchResult.restaurant?.let { restaurant ->
                RestaurantHeader(
                    restaurant = restaurant,
                    onRestaurantClick = {
                        onResultClick(
                            restaurant.id,
                            SearchResultType.RESTAURANT
                        )
                    }
                )
            }

            if (searchResult.menuItemList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.medium))

                searchResult.menuItemList.forEach { menuItem ->
                    MenuItemRow(
                        menuItem = menuItem,
                        onMenuItemClick = { onResultClick(menuItem.id, SearchResultType.FOOD_ITEM) }
                    )

                    if (menuItem != searchResult.menuItemList.last()) {
                        Spacer(modifier = Modifier.height(Spacing.small))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun RestaurantHeader(
    restaurant: SearchRestaurant,
    onRestaurantClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRestaurantClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = restaurant.imageUrl,
            contentDescription = stringResource(R.string.restaurant_image),
            modifier = Modifier
                .size(SearchDimensions.restaurantImageSize)
                .clip(RoundedCornerShape(SearchDimensions.restaurantImageCornerRadius)),
            contentScale = ContentScale.Crop,
            failure = placeholder(R.drawable.default_food)
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
                text = restaurant.cuisine.joinToString(", "),
                style = restaurantCuisine(),
                color = textSecondaryColor(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.star),
                        tint = ratingColor(),
                        modifier = Modifier.size(SearchDimensions.ratingIconSize)
                    )
                    Text(
                        text = restaurant.rating.toString(),
                        style = restaurantRating(),
                        color = cardContentColor()
                    )
                }

                Text(
                    text = restaurant.deliveryTime,
                    style = restaurantDeliveryTime(),
                    color = textSecondaryColor()
                )

                Text(
                    text = restaurant.distance,
                    style = restaurantDeliveryTime(),
                    color = textSecondaryColor()
                )
            }

            if (restaurant.badges.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.xs))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(SearchDimensions.badgeChipSpacing)
                ) {
                    items(restaurant.badges.take(3)) { badge ->
                        BadgeChip(badge = badge)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun MenuItemRow(
    menuItem: MenuItem,
    onMenuItemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMenuItemClick() }
            .padding(start = SearchDimensions.menuItemIndentStart),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                text = menuItem.description,
                style = bodySmall(),
                color = textSecondaryColor(),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            Text(
                text = "$${String.format(Locale.getDefault(), "%.2f", menuItem.price)}",
                style = foodItemPrice(),
                color = primaryColor()
            )
        }

        Spacer(modifier = Modifier.width(Spacing.medium))

        GlideImage(
            model = menuItem.imageUrl,
            contentDescription = menuItem.name,
            modifier = Modifier
                .size(SearchDimensions.menuItemImageSize)
                .clip(RoundedCornerShape(SearchDimensions.restaurantImageCornerRadius)),
            contentScale = ContentScale.Crop,
            failure = placeholder(R.drawable.default_food)
        )
    }
}

@Composable
private fun BadgeChip(badge: String) {
    Surface(
        modifier = Modifier
            .padding(
                horizontal = SearchDimensions.badgeChipPaddingHorizontal,
                vertical = SearchDimensions.badgeChipPaddingVertical
            ),
        color = when (badge.lowercase()) {
            "freeship" -> successColor()
            "popular" -> warningColor()
            "new" -> infoColor()
            else -> surfaceVariantColor()
        },
        shape = RoundedCornerShape(SearchDimensions.badgeChipCornerRadius)
    ) {
        Text(
            text = badge,
            style = badgeText(),
            color = when (badge.lowercase()) {
                "freeship" -> Color.White
                "popular" -> Color.Black
                "new" -> Color.White
                else -> onSurfaceVariantColor()
            },
            modifier = Modifier.padding(
                horizontal = SearchDimensions.badgeChipPaddingHorizontal,
                vertical = SearchDimensions.badgeChipPaddingVertical
            )
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = primaryColor()
        )
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            Text(
                text = stringResource(R.string.error_place_holder, error),
                style = errorText(),
                color = errorColor()
            )

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor()
                )
            ) {
                Text(
                    text = stringResource(R.string.try_again),
                    style = buttonText(),
                    color = onPrimaryColor()
                )
            }
        }
    }
}

@Composable
private fun EmptyResultsContent(searchText: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.medium),
            modifier = Modifier.padding(Spacing.large)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.no_results_found),
                tint = onSurfaceVariantColor(),
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = stringResource(R.string.no_results_found_for, searchText),
                style = subTitle(),
                color = onSurfaceColor()
            )

            Text(
                text = stringResource(R.string.delicious_food_delivered_fast),
                style = bodyMedium(),
                color = textSecondaryColor()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortBottomSheet(
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