package com.app.tastybuds.ui.favorites

enum class FavoriteError(val code: String) {
    REMOVE_RESTAURANT_FAILED("error_remove_restaurant_failed"),
    REMOVE_MENU_ITEM_FAILED("error_remove_menu_item_failed"), 
    LOAD_FAVORITES_FAILED("error_load_favorites_failed"),
    FAVORITE_NOT_FOUND("error_favorite_not_found"),
    NETWORK_ERROR("error_network"),
    UNKNOWN_ERROR("error_unknown")
}