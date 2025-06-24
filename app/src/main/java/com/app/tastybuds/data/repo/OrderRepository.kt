package com.app.tastybuds.data.repo

import android.content.ContentValues.TAG
import android.util.Log
import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.common.toApiString
import com.app.tastybuds.data.model.CreateOrderRequest
import com.app.tastybuds.data.model.Order
import com.app.tastybuds.data.model.OrderStatus
import com.app.tastybuds.data.model.UpdateOrderStatusRequest
import com.app.tastybuds.data.model.UserAddress
import com.app.tastybuds.data.model.Voucher
import com.app.tastybuds.util.ErrorHandler
import com.app.tastybuds.util.Result
import com.app.tastybuds.util.getGlobalVouchersExt
import com.app.tastybuds.util.getOrderByExt
import com.app.tastybuds.util.getUserAddressesExt
import com.app.tastybuds.util.getUserOrdersExt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface OrderRepository {
    suspend fun getUserAddresses(userId: String): Flow<Result<List<UserAddress>>>
    suspend fun createOrder(orderRequest: CreateOrderRequest): Flow<Result<Order>>
    suspend fun getUserOrders(userId: String): Flow<Result<List<Order>>>
    suspend fun getAvailableVouchers(userId: String): Flow<Result<List<Voucher>>>
    suspend fun getOrderById(orderId: String): Flow<Result<Order>>
    suspend fun getActiveUserOrders(userId: String): Flow<Result<List<Order>>>
    suspend fun getUserOrdersByStatus(
        userId: String,
        status: OrderStatus
    ): Flow<Result<List<Order>>>

    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Flow<Result<Order>>
}

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val apiService: TastyBudsApiService
) : OrderRepository {

    override suspend fun getUserAddresses(userId: String): Flow<Result<List<UserAddress>>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getUserAddressesExt(userId)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Failed to fetch addresses: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(ErrorHandler.handleApiError(e)))
        }
    }

    override suspend fun createOrder(orderRequest: CreateOrderRequest): Flow<Result<Order>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.createOrder(orderRequest)
            if (response.isSuccessful) {
                val orders = response.body() ?: emptyList()
                if (orders.isNotEmpty()) {
                    emit(Result.Success(orders.first()))
                } else {
                    emit(Result.Error("Failed to create order"))
                }
            } else {
                emit(Result.Error("Failed to create order: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(ErrorHandler.handleApiError(e)))
        }
    }

    override suspend fun getAvailableVouchers(userId: String): Flow<Result<List<Voucher>>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getGlobalVouchersExt(userId)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Failed to fetch vouchers: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(ErrorHandler.handleApiError(e)))
        }
    }

    override suspend fun getUserOrders(userId: String): Flow<Result<List<Order>>> = flow {
        try {
            emit(Result.Loading)

            val response = apiService.getUserOrdersExt(userId)

            if (response.isSuccessful) {
                val orders = response.body() ?: emptyList()
                val sortedOrders = orders.sortedByDescending { it.createdAt }
                emit(Result.Success(sortedOrders))
            } else {
                val errorMsg = "Failed to fetch orders: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                emit(Result.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Result.Error(ErrorHandler.handleApiError(e)))
        }
    }

    override suspend fun getOrderById(orderId: String): Flow<Result<Order>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getOrderByExt(orderId)

            if (response.isSuccessful) {
                val orders = response.body() ?: emptyList()
                if (orders.isNotEmpty()) {
                    val order = orders.first()
                    emit(Result.Success(order))
                } else {
                    val errorMsg = "Order not found with ID: $orderId"
                    Log.e(TAG, errorMsg)
                    emit(Result.Error(errorMsg))
                }
            } else {
                val errorMsg = "Failed to fetch order: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                emit(Result.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Result.Error(ErrorHandler.handleApiError(e)))
        }
    }

    override suspend fun getUserOrdersByStatus(
        userId: String,
        status: OrderStatus
    ): Flow<Result<List<Order>>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getUserOrdersByStatus(
                userId = userId,
                status = "eq.${status.toApiString()}"
            )

            if (response.isSuccessful) {
                val orders = response.body() ?: emptyList()
                emit(Result.Success(orders.sortedByDescending { it.createdAt }))
            } else {
                val errorMsg = "Failed to fetch orders: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                emit(Result.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Result.Error(ErrorHandler.handleApiError(e)))
        }
    }

    override suspend fun getActiveUserOrders(userId: String): Flow<Result<List<Order>>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getActiveUserOrders(userId)

            if (response.isSuccessful) {
                val orders = response.body() ?: emptyList()
                emit(Result.Success(orders.sortedByDescending { it.createdAt }))
            } else {
                val errorMsg =
                    "Failed to fetch active orders: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                emit(Result.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Result.Error(ErrorHandler.handleApiError(e)))
        }
    }

    override suspend fun updateOrderStatus(
        orderId: String,
        newStatus: OrderStatus
    ): Flow<Result<Order>> = flow {
        try {
            emit(Result.Loading)
            val updateRequest = UpdateOrderStatusRequest(
                status = newStatus.toApiString()
            )

            val response = apiService.updateOrderStatusExt(orderId, updateRequest)

            if (response.isSuccessful) {
                val orders = response.body() ?: emptyList()
                if (orders.isNotEmpty()) {
                    val updatedOrder = orders.first()
                    emit(Result.Success(updatedOrder))
                } else {
                    val errorMsg = "Failed to update order status"
                    Log.e(TAG, errorMsg)
                    emit(Result.Error(errorMsg))
                }
            } else {
                val errorMsg =
                    "Failed to update order status: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                emit(Result.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(Result.Error(ErrorHandler.handleApiError(e)))
        }
    }
}