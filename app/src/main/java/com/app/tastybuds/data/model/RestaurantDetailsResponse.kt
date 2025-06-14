package com.app.tastybuds.data.model

import com.app.tastybuds.domain.model.*

fun RestaurantDetailsResponse.toDomain() = RestaurantDetails(
    id = id,
    name = name,
    description = description,
    cuisine = cuisine.joinToString(", "),
    rating = rating,
    reviewCount = reviewCount,
    deliveryTime = deliveryTime,
    distance = distance,
    priceRange = priceRange,
    imageUrl = imageUrl,
    isOpen = isOpen,
    deliveryFee = deliveryFee,
    minimumOrder = minimumOrder,
    phone = phone,
    address = address
)

fun MenuItemResponse.toDomain() = RestaurantMenuItem(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    rating = rating,
    reviewCount = reviewCount,
    category = category,
    isPopular = isPopular,
    isSpicy = isSpicy,
    prepTime = prepTime
)

fun RestaurantReviewResponse.toDomain() = RestaurantReview(
    id = id,
    restaurantId = restaurantId,
    userId = userId,
    userName = userName,
    userAvatar = userAvatar,
    rating = rating,
    comment = comment,
    helpfulCount = helpfulCount,
    timeAgo = timeAgo
)

fun RestaurantVoucherResponse.toDomain() = RestaurantVoucher(
    id = id,
    title = title,
    description = description,
    value = value,
    discountType = discountType.toVoucherDiscountType(),
    userId = userId,
    restaurantId = restaurantId,
    restaurantName = restaurantName,
    minimumOrderAmount = minimumOrderAmount,
    endDate = endDate
)

fun ComboResponse.toDomain() = RestaurantCombo(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    rating = rating,
    reviewCount = reviewCount
)

private fun String.toVoucherDiscountType(): VoucherDiscountType = when (lowercase()) {
    "percentage" -> VoucherDiscountType.PERCENTAGE
    "fixed" -> VoucherDiscountType.FIXED
    "free_delivery" -> VoucherDiscountType.FREE_DELIVERY
    else -> VoucherDiscountType.PERCENTAGE
}