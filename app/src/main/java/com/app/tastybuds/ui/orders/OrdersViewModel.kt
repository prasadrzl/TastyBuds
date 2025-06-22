package com.app.tastybuds.ui.orders

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
                    _uiState.update { it.copy(error = "Failed to load user data") }
                }.onLoading {
                    _uiState.update { it.copy(isLoading = true) }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to load user data") }
            }
        }
    }

    fun loadUserOrders() {
        val userId = _userId.value
        if (userId.isEmpty()) {
            return
        }
        loadUserOrdersInternal()
    }

    private fun loadUserOrdersInternal() {
        val userId = _userId.value
        if (userId.isEmpty()) {
            return
        }

        viewModelScope.launch {
            try {
                orderUseCase.getUserOrders(userId).collect { result ->
                    result.onSuccess {
                        val sortedOrders = it.sortedByDescending { order ->
                            try {
                                order.createdAt
                            } catch (e: Exception) {
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
                    }.onError {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = it.error
                            )
                        }
                    }
                        .onLoading {
                            _uiState.update { it.copy(isLoading = true, error = null) }
                        }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load orders: ${e.message}"
                    )
                }
            }
        }
    }

    fun refreshOrders() {
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