package com.app.tastybuds.ui.orders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.data.model.*
import com.app.tastybuds.data.repo.AuthRepository
import com.app.tastybuds.domain.OrderUseCase
import com.app.tastybuds.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderReviewViewModel @Inject constructor(
    private val orderUseCase: OrderUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderReviewUiState())
    val uiState: StateFlow<OrderReviewUiState> = _uiState.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    private val _userId = MutableStateFlow("")

    companion object {
        private const val TAG = "OrderReviewViewModel"
    }

    init {
        viewModelScope.launch {
            try {
                authRepository.getUserId().first()?.let { userId ->
                    _userId.value = userId
                    Log.d(TAG, "User ID loaded: $userId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user ID: ${e.message}")
                _uiState.update { it.copy(error = "Failed to load user information") }
            }
        }
    }

    fun loadOrderReviewData(cartItems: List<CartItem>) {
        Log.d(TAG, "Loading order review data for ${cartItems.size} items")

        _cartItems.value = cartItems
        _uiState.update {
            it.copy(
                cartItems = cartItems,
                isLoading = true,
                error = null
            ).calculateTotals()
        }

        if (_userId.value.isEmpty()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "User not logged in"
                )
            }
            return
        }

        viewModelScope.launch {
            try {
                loadUserAddresses()
                loadAvailableVouchers()

                _uiState.update { it.copy(isLoading = false) }
                Log.d(TAG, "Order review data loaded successfully")

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load order review data: ${e.message}"
                    )
                }
                Log.e(TAG, "Exception in loadOrderReviewData: ${e.message}")
            }
        }
    }

    private fun loadUserAddresses() {
        if (_userId.value.isEmpty()) {
            Log.w(TAG, "Cannot load addresses: User ID is empty")
            setDefaultDemoAddress()
            return
        }

        viewModelScope.launch {
            try {
                orderUseCase.getUserAddresses(_userId.value).catch { e ->
                    Log.e(TAG, "Error loading addresses: ${e.message}")
                    setDefaultDemoAddress()
                }.collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(availableAddresses = result.data)
                            }

                            if (result.data.isNotEmpty()) {
                                val defaultAddress = result.data.find { it.isDefault }
                                    ?: result.data.first() // Use first address if no default

                                _uiState.update {
                                    it.copy(userAddress = defaultAddress)
                                }
                                Log.d(TAG, "Set default address: ${defaultAddress.addressLine}")
                            } else {
                                setDefaultDemoAddress()
                            }

                            Log.d(TAG, "Loaded ${result.data.size} user addresses")
                        }

                        is Result.Error -> {
                            Log.e(TAG, "Error loading addresses: ${result.message}")
                            setDefaultDemoAddress()
                        }

                        is Result.Loading -> {}
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading addresses: ${e.message}")
                setDefaultDemoAddress()
            }
        }
    }

    private fun setDefaultDemoAddress() {
        val demoAddress = UserAddress(
            id = "demo_address_1",
            userId = _userId.value.ifEmpty { "demo_user" },
            addressLine = "123 Sengkang Street 45",
            street = "Sengkang Street 45",
            city = "Sengkang",
            state = "Singapore",
            postalCode = "542123",
            country = "Singapore",
            latitude = 1.3914,
            longitude = 103.8946,
            addressType = "Home",
            addressLabel = "Home",
            isDefault = true,
            deliveryInstructions = "Leave at door if no answer",
            createdAt = "2024-01-01T00:00:00Z"
        )

        _uiState.update {
            it.copy(
                userAddress = demoAddress,
                availableAddresses = listOf(demoAddress)
            )
        }

        Log.d(TAG, "Set demo address: ${demoAddress.addressLine}")
    }

    private suspend fun loadAvailableVouchers() {
        try {
            orderUseCase.getAvailableVouchers(_userId.value).catch { e ->
                Log.e(TAG, "Error loading vouchers: ${e.message}")
            }.collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(availableVouchers = result.data)
                        }
                        Log.d(TAG, "Vouchers loaded: ${result.data.size}")
                    }

                    is Result.Error -> {
                        Log.e(TAG, "Error loading vouchers: ${result.message}")
                    }

                    is Result.Loading -> {}
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception loading vouchers: ${e.message}")
        }
    }

    fun updateItemQuantity(cartItem: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItem(cartItem)
            return
        }

        val updatedCartItems = _cartItems.value.map { item ->
            if (item.menuItemId == cartItem.menuItemId &&
                item.selectedSize?.id == cartItem.selectedSize?.id &&
                item.selectedToppings == cartItem.selectedToppings &&
                item.selectedSpiceLevel?.id == cartItem.selectedSpiceLevel?.id
            ) {
                item.copy(quantity = newQuantity)
            } else {
                item
            }
        }

        _cartItems.value = updatedCartItems
        _uiState.update {
            it.copy(cartItems = updatedCartItems).calculateTotals()
        }
        Log.d(TAG, "Updated item quantity to $newQuantity")
    }

    fun removeItem(cartItem: CartItem) {
        val updatedCartItems = _cartItems.value.filter { item ->
            !(item.menuItemId == cartItem.menuItemId &&
                    item.selectedSize?.id == cartItem.selectedSize?.id &&
                    item.selectedToppings == cartItem.selectedToppings &&
                    item.selectedSpiceLevel?.id == cartItem.selectedSpiceLevel?.id)
        }

        _cartItems.value = updatedCartItems
        _uiState.update {
            it.copy(cartItems = updatedCartItems).calculateTotals()
        }
        Log.d(TAG, "Removed item from cart")
    }

    fun selectVoucher(voucher: Voucher?) {
        val currentSubtotal = _uiState.value.subtotal

        if (voucher != null) {
            when (val validationResult =
                orderUseCase.validateVoucherForOrder(voucher, currentSubtotal)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            selectedVoucher = voucher,
                            error = null
                        ).calculateTotals()
                    }
                    Log.d(TAG, "Voucher selected: ${voucher.title}")
                }

                is Result.Error -> {
                    _uiState.update { it.copy(error = validationResult.message) }
                    Log.e(TAG, "Voucher validation failed: ${validationResult.message}")
                }

                is Result.Loading -> {}
            }
        } else {
            _uiState.update {
                it.copy(selectedVoucher = null).calculateTotals()
            }
            Log.d(TAG, "Voucher removed")
        }
    }

    fun changeDeliveryAddress(address: UserAddress) {
        _uiState.update {
            it.copy(userAddress = address)
        }
        Log.d(TAG, "Delivery address changed to: ${address.addressLine}")
    }

    fun createOrder(specialNotes: String? = null) {
        val currentState = _uiState.value

        if (currentState.cartItems.isEmpty()) {
            _uiState.update { it.copy(error = "Cart is empty") }
            return
        }

        if (currentState.userAddress == null) {
            _uiState.update { it.copy(error = "Please select a delivery address") }
            return
        }

        _uiState.update { it.copy(isCreatingOrder = true, error = null) }

        viewModelScope.launch {
            try {
                orderUseCase.createOrder(
                    userId = _userId.value,
                    cartItems = currentState.cartItems,
                    deliveryAddress = currentState.userAddress,
                    selectedVoucher = currentState.selectedVoucher,
                    specialNotes = specialNotes
                ).collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            // Already set isCreatingOrder = true
                        }

                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isCreatingOrder = false,
                                    orderCreated = true,
                                    createdOrderId = result.data.id,
                                    error = null
                                )
                            }
                            Log.d(TAG, "Order created successfully: ${result.data.id}")
                        }

                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isCreatingOrder = false,
                                    error = result.message
                                )
                            }
                            Log.e(TAG, "Error creating order: ${result.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCreatingOrder = false,
                        error = "Failed to create order: ${e.message}"
                    )
                }
                Log.e(TAG, "Exception in createOrder: ${e.message}")
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetOrderCreatedState() {
        _uiState.update {
            it.copy(
                orderCreated = false,
                createdOrderId = null
            )
        }
    }

    fun retry() {
        loadOrderReviewData(_cartItems.value)
    }

    fun getEstimatedDeliveryTime(): String {
        val address = _uiState.value.userAddress
        return if (address != null) {
            when (address.city?.lowercase()) {
                "marina bay", "raffles place", "tanjong pagar" -> "15 mins"
                "sengkang", "punggol", "hougang" -> "20 mins"
                "tampines", "bedok", "pasir ris" -> "25 mins"
                "jurong west", "clementi" -> "30 mins"
                "woodlands", "yishun" -> "35 mins"
                else -> "20 mins"
            }
        } else {
            "20 mins"
        }
    }
}