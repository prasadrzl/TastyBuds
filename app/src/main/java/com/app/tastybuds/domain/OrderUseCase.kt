package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.CartItem
import com.app.tastybuds.data.model.CreateOrderRequest
import com.app.tastybuds.data.model.DeliveryAddress
import com.app.tastybuds.data.model.Order
import com.app.tastybuds.data.model.UserAddress
import com.app.tastybuds.data.model.Voucher
import com.app.tastybuds.data.repo.OrderRepository
import com.app.tastybuds.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

    fun calculateDeliveryTimeFromDistance(distanceKm: Double): Int {
        return when {
            distanceKm <= 2.0 -> 15
            distanceKm <= 5.0 -> 20
            distanceKm <= 10.0 -> 30
            distanceKm <= 15.0 -> 40
            else -> 50
        }
    }

    fun getRestaurantLocation(restaurantId: String?): Pair<Double, Double> {
        return when (restaurantId) {
            "rest_001" -> Pair(1.3966, 103.9072)
            "rest_002" -> Pair(1.4043, 103.9010)
            "rest_003" -> Pair(1.3914, 103.8946)
            else -> Pair(1.3966, 103.9072)
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


    suspend fun getUserOrders(userId: String): Flow<Result<List<Order>>> {
        return repository.getUserOrders(userId)
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        val processedOrders = result.data.map { order ->
                            order.copy(
                                restaurantName = order.restaurantName ?: "Unknown Restaurant"
                            )
                        }
                        Result.Success(processedOrders)
                    }

                    is Result.Error -> result
                    is Result.Loading -> result
                }
            }
    }

    suspend fun getOrderById(orderId: String): Flow<Result<Order>> {
        return repository.getOrderById(orderId)
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        val processedOrder = result.data.copy(
                            restaurantName = result.data.restaurantName ?: "Unknown Restaurant"
                        )
                        Result.Success(processedOrder)
                    }

                    is Result.Error -> result
                    is Result.Loading -> result
                }
            }
    }
}