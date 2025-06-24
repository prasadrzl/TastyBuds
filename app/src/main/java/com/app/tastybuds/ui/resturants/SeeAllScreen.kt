package com.app.tastybuds.ui.resturants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tastybuds.R
import com.app.tastybuds.domain.model.CategoryMenuItem
import com.app.tastybuds.domain.model.CategoryRestaurant
import com.app.tastybuds.ui.theme.*
import com.app.tastybuds.util.ui.AppTopBar

@Composable
fun SeeAllScreen(
    categoryId: String,
    type: String,
    title: String,
    onBackClick: () -> Unit,
    onRestaurantClick: (String) -> Unit,
    onMenuItemClick: ((String) -> Unit)? = null,
    viewModel: SeeAllViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(categoryId, type) {
        viewModel.loadItems(categoryId, type)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor())
    ) {

        AppTopBar(title = title, onBackClick = onBackClick)

        when {
            uiState.isLoading -> {
                LoadingContent()
            }

            uiState.error != null -> {
                ErrorContent(
                    error = uiState.error ?: "",
                    onRetry = { viewModel.loadItems(categoryId, type) }
                )
            }

            else -> {
                ItemsList(
                    type = type,
                    restaurants = uiState.restaurants,
                    menuItems = uiState.menuItems,
                    onRestaurantClick = onRestaurantClick,
                    onMenuItemClick = onMenuItemClick
                )
            }
        }
    }
}

@Composable
private fun ItemsList(
    type: String,
    restaurants: List<CategoryRestaurant>,
    menuItems: List<CategoryMenuItem>,
    onRestaurantClick: (String) -> Unit,
    onMenuItemClick: ((String) -> Unit)?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (type) {
            "restaurants", "top_restaurants", "recommended_restaurants" -> {
                if (restaurants.isEmpty()) {
                    item {
                        EmptyContent(stringResource(R.string.no_restaurants_found))
                    }
                } else {
                    item {
                        Text(
                            text = "${restaurants.size} restaurants found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textSecondaryColor(),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(restaurants) { restaurant ->
                        CategoryRestaurantCard(
                            restaurant = restaurant,
                            onClick = { onRestaurantClick(restaurant.id) }
                        )
                    }
                }
            }

            "menu_items", "popular_items" -> {
                if (menuItems.isEmpty()) {
                    item {
                        EmptyContent(stringResource(R.string.no_menu_items_found))
                    }
                } else {
                    item {
                        Text(
                            text = stringResource(R.string.items_found, menuItems.size),
                            style = MaterialTheme.typography.bodyMedium,
                            color = textSecondaryColor(),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(menuItems) { menuItem ->
                        MenuItemCard(
                            menuItem = menuItem,
                            onClick = {
                                onMenuItemClick?.invoke(menuItem.id)
                            }
                        )
                    }
                }
            }

            else -> {
                item {
                    EmptyContent(stringResource(R.string.unknown_content_type))
                }
            }
        }
    }
}

data class SeeAllUiState(
    val isLoading: Boolean = false,
    val restaurants: List<CategoryRestaurant> = emptyList(),
    val menuItems: List<CategoryMenuItem> = emptyList(),
    val error: String? = null
)