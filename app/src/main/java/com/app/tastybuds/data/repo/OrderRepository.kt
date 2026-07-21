package com.app.tastybuds.data.repo

import com.app.tastybuds.domain.repository.OrderRepository

import android.content.ContentValues.TAG
import android.util.Log
import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.common.toApiString
import com.app.tastybuds.data.model.CreateOrderRequest
import com.app.tastybuds.data.model.UpdateOrderStatusRequest
import com.app.tastybuds.data.model.mapper.toDomain
import com.app.tastybuds.domain.model.AvailableVoucher
import com.app.tastybuds.domain.model.Order
import com.app.tastybuds.domain.model.OrderStatus
import com.app.tastybuds.domain.model.UserAddress
import com.app.tastybuds.util.ErrorHandler
import com.app.tastybuds.util.Result
import com.app.tastybuds.util.getGlobalVouchersExt
import com.app.tastybuds.util.getOrderByExt
import com.app.tastybuds.util.getUserAddressesExt
import com.app.tastybuds.util.getUserOrdersExt
import javax.inject.Inject
import javax.inject.Singleton

// `OrderRepository` interface now lives in :core:common (same package). Every method here
// is a single network call with no genuine ongoing observation, so these are plain suspend
// functions returning Result<T> — not Flow<Result<T>>. Loading state is the ViewModel's job
// to set before calling, not something the repository broadcasts.

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val apiService: TastyBudsApiService
) : OrderRepository {

    override suspend fun getUserAddresses(userId: String): Result<List<UserAddress>> {
        return try {
            val response = apiService.getUserAddressesExt(userId)
            if (response.isSuccessful) {
                Result.Success(response.body()?.map { it.toDomain() } ?: emptyList())
            } else {
                Result.Error("Failed to fetch addresses: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun createOrder(orderRequest: CreateOrderRequest): Result<Order> {
        return try {
            val response = apiService.createOrder(orderRequest)
            if (response.isSuccessful) {
                val orders = response.body() ?: emptyList()
                if (orders.isNotEmpty()) {
                    Result.Success(orders.first().toDomain())
                } else {
                    Result.Error("Failed to create order")
                }
            } else {
                Result.Error("Failed to create order: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun getAvailableVouchers(userId: String): Result<List<AvailableVoucher>> {
        return try {
            val response = apiService.getGlobalVouchersExt(userId)
            if (response.isSuccessful) {
                Result.Success(response.body()?.map { it.toDomain() } ?: emptyList())
            } else {
                Result.Error("Failed to fetch vouchers: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun getUserOrders(userId: String): Result<List<Order>> {
        return try {
            val response = apiService.getUserOrdersExt(userId)

            if (response.isSuccessful) {
                val orders = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(orders.sortedByDescending { it.createdAt })
            } else {
                val errorMsg = "Failed to fetch orders: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                Result.Error(errorMsg)
            }
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val response = apiService.getOrderByExt(orderId)

            if (response.isSuccessful) {
                val orders = response.body() ?: emptyList()
                if (orders.isNotEmpty()) {
                    Result.Success(orders.first().toDomain())
                } else {
                    val errorMsg = "Order not found with ID: $orderId"
                    Log.e(TAG, errorMsg)
                    Result.Error(errorMsg)
                }
            } else {
                val errorMsg = "Failed to fetch order: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                Result.Error(errorMsg)
            }
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun getUserOrdersByStatus(
        userId: String,
        status: OrderStatus
    ): Result<List<Order>> {
        return try {
            val response = apiService.getUserOrdersByStatus(
                userId = userId,
                status = "eq.${status.toApiString()}"
            )

            if (response.isSuccessful) {
                val orders = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(orders.sortedByDescending { it.createdAt })
            } else {
                val errorMsg = "Failed to fetch orders: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                Result.Error(errorMsg)
            }
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun getActiveUserOrders(userId: String): Result<List<Order>> {
        return try {
            val response = apiService.getActiveUserOrders(userId)

            if (response.isSuccessful) {
                val orders = response.body()?.map { it.toDomain() } ?: emptyList()
                Result.Success(orders.sortedByDescending { it.createdAt })
            } else {
                val errorMsg =
                    "Failed to fetch active orders: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                Result.Error(errorMsg)
            }
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    override suspend fun updateOrderStatus(
        orderId: String,
        newStatus: OrderStatus
    ): Result<Order> {
        return try {
            val updateRequest = UpdateOrderStatusRequest(
                status = newStatus.toApiString()
            )

            val response = apiService.updateOrderStatusExt(orderId, updateRequest)

            if (response.isSuccessful) {
                val orders = response.body() ?: emptyList()
                if (orders.isNotEmpty()) {
                    Result.Success(orders.first().toDomain())
                } else {
                    val errorMsg = "Failed to update order status"
                    Log.e(TAG, errorMsg)
                    Result.Error(errorMsg)
                }
            } else {
                val errorMsg =
                    "Failed to update order status: ${response.code()} - ${response.message()}"
                Log.e(TAG, errorMsg)
                Result.Error(errorMsg)
            }
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }
}