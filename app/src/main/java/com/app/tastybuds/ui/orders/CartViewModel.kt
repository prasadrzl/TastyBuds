package com.app.tastybuds.ui.orders

import android.util.Log
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

    companion object {
        private const val TAG = "CartViewModel"
    }

    fun addToCart(item: CartItem) {
        Log.d(TAG, "Adding item to cart: ${item.name} (quantity: ${item.quantity})")

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
        Log.d(TAG, "Setting editing item: ${item.name}")
        _editingItem.value = item
    }

    private fun clearEditingItem() {
        Log.d(TAG, "Clearing editing item")
        _editingItem.value = null
    }

    fun updateCartItem(oldItem: CartItem, newItem: CartItem) {
        Log.d(TAG, "Updating cart item: ${oldItem.name} -> ${newItem.name}")

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
            Log.d(TAG, "Cart item updated successfully")
        } else {
            Log.w(TAG, "Cart item not found for update")
        }

        clearEditingItem()
    }

    private fun removeFromCart(item: CartItem) {
        Log.d(TAG, "Removing item from cart: ${item.name}")

        val currentItems = _cartItems.value.toMutableList()
        val removed = currentItems.removeAll { cartItem ->
            cartItem.menuItemId == item.menuItemId &&
                    cartItem.selectedSize?.id == item.selectedSize?.id &&
                    cartItem.selectedToppings == item.selectedToppings &&
                    cartItem.selectedSpiceLevel?.id == item.selectedSpiceLevel?.id
        }

        if (removed) {
            _cartItems.value = currentItems
            Log.d(TAG, "Item removed successfully. Cart now has ${currentItems.size} items")
        } else {
            Log.w(TAG, "Item not found for removal")
        }
    }

    fun updateItemQuantity(item: CartItem, newQuantity: Int) {
        Log.d(TAG, "Updating item quantity: ${item.name} -> $newQuantity")

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
            Log.d(TAG, "Item quantity updated successfully")
        } else {
            Log.w(TAG, "Item not found for quantity update")
        }
    }

    fun clearCart() {
        Log.d(TAG, "Clearing entire cart")
        _cartItems.value = emptyList()
        clearEditingItem()
    }

    fun getCartTotal(): Double {
        val total = _cartItems.value.sumOf { it.calculateItemTotal() }
        Log.d(TAG, "Cart total calculated: $total")
        return total
    }

    fun isEmpty(): Boolean {
        return _cartItems.value.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return _cartItems.value.isNotEmpty()
    }
}