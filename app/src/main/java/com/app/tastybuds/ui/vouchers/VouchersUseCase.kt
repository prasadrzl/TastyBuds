package com.app.tastybuds.ui.vouchers

import com.app.tastybuds.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VouchersUseCase @Inject constructor(
    private val repository: VouchersRepository
) {

    fun getAllVouchers(userId: String): Flow<Result<List<Voucher>>> {
        return repository.getUserVouchers(userId)
    }

    fun getActiveVouchers(userId: String): Flow<Result<List<Voucher>>> {
        return repository.getActiveVouchers(userId)
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        // Additional business logic filtering and sorting
                        val filteredVouchers = result.data
                            .filter { voucher -> voucher.canBeUsed && !voucher.isExpired }
                            .sortedWith(
                                compareBy<Voucher> { it.isExpired }
                                    .thenByDescending { it.value }
                                    .thenBy { it.expiryDate }
                            )
                        Result.Success(filteredVouchers)
                    }
                    is Result.Error -> result
                    is Result.Loading -> result
                }
            }
    }

    fun getUsedVouchers(userId: String): Flow<Result<List<Voucher>>> {
        return repository.getUsedVouchers(userId)
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        // Sort used vouchers by creation date (most recent first)
                        val sortedVouchers = result.data
                            .sortedByDescending { it.createdAt }
                        Result.Success(sortedVouchers)
                    }
                    is Result.Error -> result
                    is Result.Loading -> result
                }
            }
    }

    fun getRestaurantVouchers(userId: String, restaurantId: String): Flow<Result<List<Voucher>>> {
        return repository.getRestaurantVouchers(userId, restaurantId)
    }

    suspend fun useVoucher(voucherId: String, orderId: String): Result<Voucher> {
        return repository.markVoucherAsUsed(voucherId, orderId)
    }

    suspend fun refreshVouchers(userId: String): Result<List<Voucher>> {
        return repository.refreshVouchers(userId)
    }

    fun validateVoucherForOrder(voucher: Voucher, orderAmount: Double): Result<Boolean> {
        return try {
            when {
                voucher.isUsed -> Result.Error("Voucher has already been used")
                voucher.isExpired -> Result.Error("Voucher has expired")
                !voucher.canBeUsed -> Result.Error("Voucher cannot be used")
                !voucher.isActive -> Result.Error("Voucher is not active")
                voucher.usedCount >= voucher.usageLimit -> Result.Error("Voucher usage limit reached")
                voucher.minimumOrderAmount > 0 && orderAmount < voucher.minimumOrderAmount -> {
                    Result.Error("Minimum order amount of $${voucher.minimumOrderAmount} required")
                }
                else -> Result.Success(true)
            }
        } catch (e: Exception) {
            Result.Error("Error validating voucher: ${e.localizedMessage}")
        }
    }

    fun calculateDiscountAmount(voucher: Voucher, orderAmount: Double): Double {
        return when (voucher.discountType) {
            DiscountType.PERCENTAGE -> {
                (orderAmount * voucher.value / 100.0)
            }
            DiscountType.FIXED_AMOUNT -> {
                voucher.value
            }
            DiscountType.FREE_DELIVERY -> {
                voucher.value // Delivery fee amount
            }
            DiscountType.BUY_ONE_GET_ONE -> {
                // This would need more complex logic based on cart items
                0.0
            }
        }
    }

    fun getVoucherDisplayText(voucher: Voucher): String {
        return when (voucher.discountType) {
            DiscountType.PERCENTAGE -> "${voucher.value.toInt()}% Off"
            DiscountType.FIXED_AMOUNT -> "$${voucher.value.toInt()} Off"
            DiscountType.FREE_DELIVERY -> "Free Delivery"
            DiscountType.BUY_ONE_GET_ONE -> "Buy 1 Get 1"
        }
    }

    fun getVoucherIconText(voucher: Voucher): String {
        return when (voucher.discountType) {
            DiscountType.PERCENTAGE -> "${voucher.value.toInt()}% OFF"
            DiscountType.FIXED_AMOUNT -> "$${voucher.value.toInt()} OFF"
            DiscountType.FREE_DELIVERY -> "FREE"
            DiscountType.BUY_ONE_GET_ONE -> "BOGO"
        }
    }
}