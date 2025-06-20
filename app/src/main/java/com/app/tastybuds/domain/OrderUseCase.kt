package com.app.tastybuds.domain

import com.app.tastybuds.data.model.CartItem
import com.app.tastybuds.data.model.CreateOrderRequest
import com.app.tastybuds.data.model.DeliveryAddress
import com.app.tastybuds.data.model.Order
import com.app.tastybuds.data.model.UserAddress
import com.app.tastybuds.data.model.Voucher
import com.app.tastybuds.data.repo.OrderRepository
import com.app.tastybuds.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderUseCase @Inject constructor(
    private val repository: OrderRepository
) {

    suspend fun getUserAddresses(userId: String): Flow<Result<List<UserAddress>>> {
        return repository.getUserAddresses(userId)
    }

    fun createOrder(
        userId: String,
        cartItems: List<CartItem>,
        deliveryAddress: UserAddress,
        selectedVoucher: Voucher? = null,
        specialNotes: String? = null
    ): Flow<Result<Order>> = flow {
        try {
            emit(Result.Loading)

            if (cartItems.isEmpty()) {
                emit(Result.Error("Cart is empty"))
                return@flow
            }

            val subtotal = cartItems.sumOf { it.calculateItemTotal() }
            val deliveryFee = 2.0
            val promotionDiscount = selectedVoucher?.calculateDiscount(subtotal) ?: 0.0
            val totalAmount = subtotal + deliveryFee - promotionDiscount

            selectedVoucher?.let { voucher ->
                if (voucher.minimumOrder != null && subtotal < voucher.minimumOrder) {
                    emit(Result.Error("Minimum order of $${voucher.minimumOrder} required for this voucher"))
                    return@flow
                }
            }

            val restaurantId = cartItems.firstOrNull()?.restaurantId

            val orderRequest = CreateOrderRequest(
                userId = userId,
                restaurantId = restaurantId,
                orderItems = cartItems.map { it.toOrderItemRequest() },
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                promotionDiscount = promotionDiscount,
                totalAmount = totalAmount,
                paymentMethod = "e-wallet",
                deliveryAddress = DeliveryAddress(
                    addressLine = deliveryAddress.addressLine,
                    street = deliveryAddress.street,
                    city = deliveryAddress.city,
                    state = deliveryAddress.state,
                    postalCode = deliveryAddress.postalCode,
                    country = deliveryAddress.country ?: "Singapore",
                    latitude = deliveryAddress.latitude,
                    longitude = deliveryAddress.longitude,
                    deliveryInstructions = deliveryAddress.deliveryInstructions
                ),
                deliveryLat = deliveryAddress.latitude,
                deliveryLng = deliveryAddress.longitude,
                estimatedDeliveryTime = calculateEstimatedDeliveryTime(deliveryAddress),
                specialNotes = specialNotes
            )

            repository.createOrder(orderRequest).collect { result ->
                emit(result)
            }

        } catch (e: Exception) {
            emit(Result.Error("Failed to create order: ${e.message}"))
        }
    }

    suspend fun getAvailableVouchers(userId: String): Flow<Result<List<Voucher>>> {
        return repository.getAvailableVouchers(userId)
    }

    fun validateVoucherForOrder(voucher: Voucher, subtotal: Double): Result<Double> {
        return try {
            if (voucher.isUsed) {
                return Result.Error("This voucher has already been used")
            }

            if (voucher.minimumOrder != null && subtotal < voucher.minimumOrder) {
                return Result.Error("Minimum order of $${voucher.minimumOrder} required for this voucher")
            }

            val discount = voucher.calculateDiscount(subtotal)
            Result.Success(discount)
        } catch (e: Exception) {
            Result.Error("Error validating voucher: ${e.message}")
        }
    }


    private fun calculateEstimatedDeliveryTime(address: UserAddress): Int {
        return when (address.city?.lowercase()) {
            "marina bay", "raffles place", "tanjong pagar" -> 15
            "sengkang", "punggol", "hougang" -> 20
            "tampines", "bedok", "pasir ris" -> 25
            "jurong west", "clementi" -> 30
            "woodlands", "yishun" -> 35
            else -> 20
        }
    }
}