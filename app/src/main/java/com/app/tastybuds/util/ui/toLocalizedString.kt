package com.app.tastybuds.util.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.app.tastybuds.R
import com.app.tastybuds.ui.favorites.FavoriteError

@Composable
fun FavoriteError.toLocalizedString(details: String? = null): String {
    val context = LocalContext.current
    
    val baseMessage = when (this) {
        FavoriteError.REMOVE_RESTAURANT_FAILED -> context.getString(R.string.error_remove_restaurant_failed)
        FavoriteError.REMOVE_MENU_ITEM_FAILED -> context.getString(R.string.error_remove_menu_item_failed)
        FavoriteError.LOAD_FAVORITES_FAILED -> context.getString(R.string.error_load_favorites_failed)
        FavoriteError.FAVORITE_NOT_FOUND -> context.getString(R.string.error_favorite_not_found)
        FavoriteError.NETWORK_ERROR -> context.getString(R.string.error_network)
        FavoriteError.UNKNOWN_ERROR -> context.getString(R.string.error_unknown)
    }
    
    return if (details != null) {
        context.getString(R.string.error_with_details, baseMessage, details)
    } else {
        baseMessage
    }
}