package com.app.tastybuds.util

import com.app.tastybuds.domain.model.CartItem
import com.app.tastybuds.data.model.OrderItemSize
import com.app.tastybuds.data.model.OrderItemTopping
import com.app.tastybuds.data.model.OrderItemSpiceLevel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object CartJsonUtils {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val listType = Types.newParameterizedType(List::class.java, CartItem::class.java)
    private val adapter: JsonAdapter<List<CartItem>> = moshi.adapter(listType)

    fun cartItemsToJson(cartItems: List<CartItem>): String {
        return try {
            val json = adapter.toJson(cartItems)
            URLEncoder.encode(json, StandardCharsets.UTF_8.toString())
        } catch (e: Exception) {
            URLEncoder.encode("[]", StandardCharsets.UTF_8.toString())
        }
    }

    fun jsonToCartItems(json: String): List<CartItem> {
        return try {
            val decodedJson = URLDecoder.decode(json, StandardCharsets.UTF_8.toString())
            adapter.fromJson(decodedJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun createCartItemFromFoodDetails(
        menuItemId: String,
        name: String,
        image: String?,
        basePrice: Double,
        selectedSize: OrderItemSize?,
        selectedToppings: List<OrderItemTopping>,
        selectedSpiceLevel: OrderItemSpiceLevel?,
        quantity: Int,
        notes: String?,
        restaurantId: String?
    ): CartItem {
        return CartItem(
            menuItemId = menuItemId,
            name = name,
            image = image,
            basePrice = basePrice,
            selectedSize = selectedSize,
            selectedToppings = selectedToppings,
            selectedSpiceLevel = selectedSpiceLevel,
            quantity = quantity,
            notes = notes,
            restaurantId = restaurantId
        )
    }
}