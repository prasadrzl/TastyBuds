package com.app.tastybuds.data.model

data class RestaurantDetails(
    val id: String,
    val name: String,
    val description: String,
    val cuisine: String,
    val rating: Float,
    val reviewCount: Int,
    val deliveryTime: String,
    val distance: String,
    val priceRange: String,
    val imageUrl: String,
    val isOpen: Boolean,
    val deliveryFee: Float,
    val minimumOrder: Float?,
    val phone: String,
    val address: String
)

data class RestaurantMenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Float,
    val imageUrl: String,
    val rating: Float,
    val reviewCount: Int,
    val category: String,
    val isPopular: Boolean,
    val isSpicy: Boolean,
    val prepTime: String
)

data class RestaurantReview(
    val id: String,
    val restaurantId: String,
    val userId: String,
    val userName: String,
    val userAvatar: String,
    val rating: Float,
    val comment: String,
    val helpfulCount: Int,
    val timeAgo: String
)

data class RestaurantVoucher(
    val id: String,
    val title: String,
    val description: String,
    val value: Float,
    val discountType: VoucherDiscountType,
    val userId: String,
    val restaurantId: String?,
    val restaurantName: String?,
    val minimumOrderAmount: Float,
    val endDate: String
)

data class RestaurantCombo(
    val id: String,
    val name: String,
    val description: String,
    val price: Float,
    val imageUrl: String,
    val rating: Float,
    val reviewCount: Int
)

enum class VoucherDiscountType {
    PERCENTAGE,
    FIXED,
    FREE_DELIVERY
}

data class RestaurantDetailsData(
    val restaurant: RestaurantDetails,
    val forYouItems: List<RestaurantMenuItem>,
    val menuItems: List<RestaurantMenuItem>,
    val reviews: List<RestaurantReview>,
    val vouchers: List<RestaurantVoucher>,
    val combos: List<RestaurantCombo>
)