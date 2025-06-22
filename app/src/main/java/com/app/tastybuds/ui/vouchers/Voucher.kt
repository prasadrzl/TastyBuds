package com.app.tastybuds.ui.vouchers

import androidx.compose.ui.graphics.Color

data class Voucher(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val value: Double = 0.0,
    val isUsed: Boolean = false,
    val expiryDate: String = "",
    val createdAt: String = "",
    val restaurantId: String? = null,
    val restaurantName: String? = null,
    val voucherType: VoucherType = VoucherType.RESTAURANT_SPECIFIC,
    val applicableCategoryIds: List<String>? = null,
    val minimumOrderAmount: Double = 0.0,
    val usageLimit: Int = 1,
    val usedCount: Int = 0,
    val isActive: Boolean = true,
    val startDate: String = "",
    val endDate: String = "",
    val description: String = "",
    val discountType: DiscountType = DiscountType.PERCENTAGE,
    val backgroundColor: Color = Color(0xFFFFF3E0),
    val isExpired: Boolean = false,
    val canBeUsed: Boolean = true,
    val remainingUses: Int = 1,
    val expiryText: String = "",
    val discountText: String = "",
    val validityText: String = "",
    val buttonText: String = "USE NOW",
    val buttonEnabled: Boolean = true,
    val iconText: String = "",
    val iconColor: Color = Color.White,
    val minimumOrderText: String? = null
)

enum class VoucherType {
    RESTAURANT_SPECIFIC,
    GLOBAL,
    CATEGORY_SPECIFIC;

    companion object {
        fun fromString(value: String): VoucherType {
            return when (value.lowercase()) {
                "restaurant_specific" -> RESTAURANT_SPECIFIC
                "global" -> GLOBAL
                "category_specific" -> CATEGORY_SPECIFIC
                else -> RESTAURANT_SPECIFIC
            }
        }
    }
}

enum class DiscountType {
    PERCENTAGE,
    FIXED_AMOUNT,
    FREE_DELIVERY,
    BUY_ONE_GET_ONE;

    companion object {
        fun fromString(value: String): DiscountType {
            return when (value.lowercase()) {
                "percentage" -> PERCENTAGE
                "fixed_amount" -> FIXED_AMOUNT
                "free_delivery" -> FREE_DELIVERY
                "buy_one_get_one" -> BUY_ONE_GET_ONE
                else -> PERCENTAGE
            }
        }
    }
}