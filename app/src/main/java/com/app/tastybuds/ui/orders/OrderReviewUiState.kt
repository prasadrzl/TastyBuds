package com.app.tastybuds.ui.orders

import com.app.tastybuds.domain.model.CartItem
import com.app.tastybuds.data.model.RestaurantMenuItem
import com.app.tastybuds.data.model.UserAddress
import com.app.tastybuds.data.model.Voucher

data class OrderReviewUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val cartItems: List<CartItem> = emptyList(),
    val userAddress: UserAddress? = null,
    val availableAddresses: List<UserAddress> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 2.0,
    val promotionDiscount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val selectedVoucher: Voucher? = null,
    val availableVouchers: List<Voucher> = emptyList(),
    val recommendedItems: List<RestaurantMenuItem> = emptyList(),
    val isCreatingOrder: Boolean = false,
    val orderCreated: Boolean = false,
    val createdOrderId: String? = null
) {
    fun calculateTotals(): OrderReviewUiState {
        val calculatedSubtotal = cartItems.sumOf { it.calculateItemTotal() }
        val discount = selectedVoucher?.calculateDiscount(calculatedSubtotal) ?: 0.0
        val total = calculatedSubtotal + deliveryFee - discount

        return copy(
            subtotal = calculatedSubtotal,
            promotionDiscount = discount,
            totalAmount = total
        )
    }
}