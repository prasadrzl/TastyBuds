package com.app.tastybuds.domain.repository

import com.app.tastybuds.domain.model.PlaceOrderData
import com.app.tastybuds.domain.model.AvailableVoucher
import com.app.tastybuds.domain.model.Order
import com.app.tastybuds.domain.model.OrderStatus
import com.app.tastybuds.domain.model.UserAddress
import com.app.tastybuds.util.Result

interface OrderRepository {
    suspend fun getUserAddresses(userId: String): Result<List<UserAddress>>
    suspend fun createOrder(orderData: PlaceOrderData): Result<Order>
    suspend fun getUserOrders(userId: String): Result<List<Order>>
    suspend fun getAvailableVouchers(userId: String): Result<List<AvailableVoucher>>
    suspend fun getOrderById(orderId: String): Result<Order>
    suspend fun getActiveUserOrders(userId: String): Result<List<Order>>
    suspend fun getUserOrdersByStatus(
        userId: String,
        status: OrderStatus
    ): Result<List<Order>>

    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Order>
}
