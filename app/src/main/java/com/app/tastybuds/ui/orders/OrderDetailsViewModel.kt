package com.app.tastybuds.ui.orders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.data.model.Order
import com.app.tastybuds.domain.OrderUseCase
import com.app.tastybuds.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val orderUseCase: OrderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderDetailsUiState())
    val uiState: StateFlow<OrderDetailsUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "OrderDetailsViewModel"
    }

    fun loadOrderDetails(orderId: String) {
        Log.d(TAG, "Loading order details for ID: $orderId")

        viewModelScope.launch {
            try {
                orderUseCase.getOrderById(orderId).collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = true,
                                    error = null
                                )
                            }
                            Log.d(TAG, "Loading order details...")
                        }

                        is Result.Success -> {
                            val order = result.data
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    order = order,
                                    error = null
                                )
                            }
                            Log.d(TAG, "Successfully loaded order details: ${order.id}")
                        }

                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                            Log.e(TAG, "Error loading order details: ${result.message}")
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
                Log.e(TAG, "Exception loading order details: ${e.message}")
            }
        }
    }
}

data class OrderDetailsUiState(
    val isLoading: Boolean = false,
    val order: Order? = null,
    val error: String? = null
)