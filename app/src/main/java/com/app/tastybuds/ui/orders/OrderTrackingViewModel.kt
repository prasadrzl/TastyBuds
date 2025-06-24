package com.app.tastybuds.ui.orders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.data.model.Order
import com.app.tastybuds.data.model.OrderStatus
import com.app.tastybuds.domain.OrderUseCase
import com.app.tastybuds.util.LocationUtils.calculateDistance
import com.app.tastybuds.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class OrderTrackingViewModel @Inject constructor(
    private val orderUseCase: OrderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderTrackingUiState())
    val uiState: StateFlow<OrderTrackingUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "OrderTrackingViewModel"
    }

    fun loadOrderDetails(orderId: String) {
        Log.d(TAG, "Loading order details for ID: $orderId")

        viewModelScope.launch {
            try {
                orderUseCase.getOrderById(orderId).collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _uiState.update { it.copy(isLoading = true, error = null) }
                        }

                        is Result.Success -> {
                            val order = result.data
                            processOrderData(order)
                            Log.d(TAG, "Order details loaded successfully")
                        }

                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                            Log.e(TAG, "Error loading order: ${result.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load order details: ${e.message}"
                    )
                }
                Log.e(TAG, "Exception loading order: ${e.message}")
            }
        }
    }

    private fun processOrderData(order: Order) {
        val deliveryLat = order.deliveryLat ?: order.deliveryAddress.latitude
        val deliveryLng = order.deliveryLng ?: order.deliveryAddress.longitude
        val deliveryLocation = if (deliveryLat != null && deliveryLng != null) {
            Pair(deliveryLat, deliveryLng)
        } else {
            null
        }

        val restaurantLocation = orderUseCase.getRestaurantLocation(order.restaurantId)

        val distance = if (deliveryLocation != null) {
            val distanceKm = calculateDistance(
                restaurantLocation.first, restaurantLocation.second,
                deliveryLocation.first, deliveryLocation.second
            )
            String.format(Locale.getDefault(), "%.1f km", distanceKm)
        } else {
            "Unknown"
        }

        val deliveryTime = if (deliveryLocation != null) {
            val distanceKm = calculateDistance(
                restaurantLocation.first, restaurantLocation.second,
                deliveryLocation.first, deliveryLocation.second
            )
            val timeMinutes = orderUseCase.calculateDeliveryTimeFromDistance(distanceKm)
            "$timeMinutes mins"
        } else {
            "${order.estimatedDeliveryTime} mins"
        }

        val address = buildString {
            append(order.deliveryAddress.addressLine)
            order.deliveryAddress.street?.let {
                if (it.isNotBlank()) append(", $it")
            }
            order.deliveryAddress.city?.let {
                if (it.isNotBlank()) append(", $it")
            }
            order.deliveryAddress.postalCode?.let {
                if (it.isNotBlank()) append(" $it")
            }
        }

        val progressSteps = generateProgressSteps(order.status)

        _uiState.update {
            it.copy(
                isLoading = false,
                error = null,
                order = order,
                customerName = extractCustomerName(order),
                deliveryAddress = address,
                restaurantLocation = restaurantLocation,
                deliveryLocation = deliveryLocation,
                distance = distance,
                estimatedDeliveryTime = deliveryTime,
                actualDeliveryTime = order.actualDeliveryTime ?: "",
                progressSteps = progressSteps
            )
        }
    }

    private fun extractCustomerName(order: Order): String {
        return "Customer #${order.userId.take(6)}"
    }

    private fun generateProgressSteps(currentStatus: OrderStatus): List<OrderProgressStep> {
        val allSteps = listOf(
            OrderProgressStep("Order Placed", "Your order has been confirmed", false),
            OrderProgressStep("Confirmed", "Restaurant confirmed your order", false),
            OrderProgressStep("Preparing", "Restaurant is preparing your order", false),
            OrderProgressStep("Ready", "Your order is ready for pickup", false),
            OrderProgressStep("Out for Delivery", "Driver is on the way", false),
            OrderProgressStep("Delivered", "Enjoy your meal!", false)
        )

        val statusOrder = listOf(
            OrderStatus.PENDING,
            OrderStatus.CONFIRMED,
            OrderStatus.PREPARING,
            OrderStatus.READY,
            OrderStatus.OUT_FOR_DELIVERY,
            OrderStatus.DELIVERED
        )

        val currentIndex = statusOrder.indexOf(currentStatus)

        return allSteps.mapIndexed { index, step ->
            when {
                index < currentIndex -> step.copy(isCompleted = true)
                index == currentIndex -> step.copy(isActive = true)
                else -> step
            }
        }
    }

    fun retry(orderId: String) {
        loadOrderDetails(orderId)
    }
}