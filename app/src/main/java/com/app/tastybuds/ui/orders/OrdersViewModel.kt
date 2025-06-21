package com.app.tastybuds.ui.orders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.data.model.Order
import com.app.tastybuds.data.repo.AuthRepository
import com.app.tastybuds.domain.OrderUseCase
import com.app.tastybuds.util.onError
import com.app.tastybuds.util.onLoading
import com.app.tastybuds.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderUseCase: OrderUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    private val _userId = MutableStateFlow("")

    companion object {
        private const val TAG = "OrdersViewModel"
    }

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                authRepository.getCurrentUser().onSuccess {
                    _userId.value = it.id
                    loadUserOrdersInternal()
                }.onError {
                    Log.e(TAG, "Failed to load current user: $it")
                    _uiState.update { it.copy(error = "Failed to load user data") }
                }.onLoading {
                    _uiState.update { it.copy(isLoading = true) }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Exception loading current user: ${e.message}")
                _uiState.update { it.copy(error = "Failed to load user data") }
            }
        }
    }

    fun loadUserOrders() {
        val userId = _userId.value
        if (userId.isEmpty()) {
            Log.w(TAG, "User ID is empty, waiting for user to load first...")
            return
        }
        loadUserOrdersInternal()
    }

    private fun loadUserOrdersInternal() {
        val userId = _userId.value
        if (userId.isEmpty()) {
            Log.w(TAG, "User ID is still empty, cannot load orders")
            return
        }

        Log.d(TAG, "Loading orders for user: $userId")

        viewModelScope.launch {
            try {
                orderUseCase.getUserOrders(userId).collect { result ->
                    result.onSuccess {
                        val sortedOrders = it.sortedByDescending { order ->
                            try {
                                order.createdAt
                            } catch (e: Exception) {
                                Log.w(
                                    TAG,
                                    "Could not parse date for order ${order.id}: ${e.message}"
                                )
                                order.createdAt
                            }
                        }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                orders = sortedOrders,
                                error = null
                            )
                        }
                        Log.d(TAG, "Successfully loaded ${sortedOrders.size} orders")
                    }.onError {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = it.error
                            )
                        }
                        Log.e(TAG, "Error loading orders: ${it}")
                    }
                        .onLoading {
                            _uiState.update { it.copy(isLoading = true, error = null) }
                            Log.d(TAG, "Loading user orders...")
                        }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load orders: ${e.message}"
                    )
                }
                Log.e(TAG, "Exception loading orders: ${e.message}")
            }
        }
    }

    fun refreshOrders() {
        Log.d(TAG, "Refreshing orders...")
        loadUserOrdersInternal()
    }
}

data class OrdersUiState(
    val isLoading: Boolean = false,
    val isLoadingUser: Boolean = false,
    val userLoaded: Boolean = false,
    val orders: List<Order> = emptyList(),
    val filteredOrders: List<Order>? = null,
    val error: String? = null
)