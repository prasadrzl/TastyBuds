package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.CartItem
import com.app.tastybuds.data.model.CreateOrderRequest
import com.app.tastybuds.data.model.DeliveryAddress
import com.app.tastybuds.domain.model.AvailableVoucher
import com.app.tastybuds.domain.model.Order
import com.app.tastybuds.domain.model.UserAddress
import com.app.tastybuds.domain.repository.OrderRepository
import com.app.tastybuds.util.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderUseCase @Inject constructor(
    private val repository: OrderRepository
) {

    suspend fun getUserAddresses(userId: String): Result<List<UserAddress>> {
        return repository.getUserAddresses(userId)
    }

    suspend fun createOrder(
        userId: String,
        cartItems: List<CartItem>,
        deliveryAddress: UserAddress,
        selectedVoucher: AvailableVoucher? = null,
        specialNotes: String? = null
    ): Result<Order> {
        return try {
            if (cartItems.isEmpty()) {
                return Result.Error("Cart is empty")
            }

            val subtotal = cartItems.sumOf { it.calculateItemTotal() }
            val deliveryFee = 2.0
            val promotionDiscount = selectedVoucher?.calculateDiscount(subtotal) ?: 0.0
            val totalAmount = subtotal + deliveryFee - promotionDiscount

            selectedVoucher?.let { voucher ->
                if (voucher.minimumOrder != null && subtotal < voucher.minimumOrder) {
                    return Result.Error("Minimum order of $${voucher.minimumOrder} required for this voucher")
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

            repository.createOrder(orderRequest)
        } catch (e: Exception) {
            Result.Error("Failed to create order: ${e.message}")
        }
    }

    suspend fun getAvailableVouchers(userId: String): Result<List<AvailableVoucher>> {
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


    suspend fun getUserOrders(userId: String): Result<List<Order>> {
        return when (val result = repository.getUserOrders(userId)) {
            is Result.Success -> {
                val processedOrders = result.data.map { order ->
                    order.copy(restaurantName = order.restaurantName ?: "Unknown Restaurant")
                }
                Result.Success(processedOrders)
            }

            is Result.Error -> result
            is Result.Loading -> result
        }
    }

    suspend fun getOrderById(orderId: String): Result<Order> {
        return when (val result = repository.getOrderById(orderId)) {
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