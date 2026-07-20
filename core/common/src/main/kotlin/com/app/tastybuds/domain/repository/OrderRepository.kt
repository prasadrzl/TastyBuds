package com.app.tastybuds.domain.repository

// NOTE: `createOrder` still accepts the data-layer CreateOrderRequest DTO directly as
// input, rather than domain-level params (cart items / address / voucher) that the impl
// would translate internally. That would be the fully correct shape per Clean
// Architecture, but is a larger follow-up than this pass covers — every other method here
// now returns domain models, not DTOs
import com.app.tastybuds.data.model.CreateOrderRequest
import com.app.tastybuds.domain.model.AvailableVoucher
import com.app.tastybuds.domain.model.Order
import com.app.tastybuds.domain.model.OrderStatus
import com.app.tastybuds.domain.model.UserAddress
import com.app.tastybuds.util.Result

interface OrderRepository {
    suspend fun getUserAddresses(userId: String): Result<List<UserAddress>>
    suspend fun createOrder(orderRequest: CreateOrderRequest): Result<Order>
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
