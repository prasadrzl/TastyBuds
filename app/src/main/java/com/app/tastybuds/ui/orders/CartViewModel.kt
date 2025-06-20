package com.app.tastybuds.ui.orders

import androidx.lifecycle.ViewModel
import com.app.tastybuds.data.model.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor() : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _editingItem = MutableStateFlow<CartItem?>(null)
    val editingItem: StateFlow<CartItem?> = _editingItem.asStateFlow()

    fun addToCart(item: CartItem) {
        val currentItems = _cartItems.value.toMutableList()

        val existingItemIndex = currentItems.indexOfFirst { cartItem ->
            cartItem.menuItemId == item.menuItemId &&
                    cartItem.selectedSize?.id == item.selectedSize?.id &&
                    cartItem.selectedToppings == item.selectedToppings &&
                    cartItem.selectedSpiceLevel?.id == item.selectedSpiceLevel?.id
        }

        if (existingItemIndex != -1) {
            val existingItem = currentItems[existingItemIndex]
            currentItems[existingItemIndex] = existingItem.copy(
                quantity = existingItem.quantity + item.quantity
            )
        } else {
            currentItems.add(item)
        }

        _cartItems.value = currentItems
    }

    fun setEditingItem(item: CartItem) {
        _editingItem.value = item
    }

    private fun clearEditingItem() {
        _editingItem.value = null
    }

    fun updateCartItem(oldItem: CartItem, newItem: CartItem) {
        val currentItems = _cartItems.value.toMutableList()

        val index = currentItems.indexOfFirst {
            it.menuItemId == oldItem.menuItemId &&
                    it.selectedSize?.id == oldItem.selectedSize?.id &&
                    it.selectedToppings == oldItem.selectedToppings &&
                    it.selectedSpiceLevel?.id == oldItem.selectedSpiceLevel?.id
        }

        if (index != -1) {
            currentItems[index] = newItem
            _cartItems.value = currentItems
        }

        clearEditingItem()
    }

    private fun removeFromCart(item: CartItem) {
        val currentItems = _cartItems.value.toMutableList()
        currentItems.removeAll { cartItem ->
            cartItem.menuItemId == item.menuItemId &&
                    cartItem.selectedSize?.id == item.selectedSize?.id &&
                    cartItem.selectedToppings == item.selectedToppings &&
                    cartItem.selectedSpiceLevel?.id == item.selectedSpiceLevel?.id
        }
        _cartItems.value = currentItems
    }

    fun updateItemQuantity(item: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(item)
            return
        }

        val currentItems = _cartItems.value.toMutableList()
        val itemIndex = currentItems.indexOfFirst { cartItem ->
            cartItem.menuItemId == item.menuItemId &&
                    cartItem.selectedSize?.id == item.selectedSize?.id &&
                    cartItem.selectedToppings == item.selectedToppings &&
                    cartItem.selectedSpiceLevel?.id == item.selectedSpiceLevel?.id
        }

        if (itemIndex != -1) {
            currentItems[itemIndex] = currentItems[itemIndex].copy(quantity = newQuantity)
            _cartItems.value = currentItems
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        clearEditingItem()
    }

    fun getCartTotal(): Double {
        return _cartItems.value.sumOf { it.calculateItemTotal() }
    }

    fun getCartItemCount(): Int {
        return _cartItems.value.sumOf { it.quantity }
    }
}