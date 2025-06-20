package com.app.tastybuds.data.repo

import android.content.ContentValues.TAG
import android.util.Log
import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.*
import com.app.tastybuds.util.Result
import com.app.tastybuds.util.getGlobalVouchersExt
import com.app.tastybuds.util.getOrderByExt
import com.app.tastybuds.util.getUserAddressesExt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

interface OrderRepository {
    suspend fun getUserAddresses(userId: String): Flow<Result<List<UserAddress>>>
    suspend fun createOrder(orderRequest: CreateOrderRequest): Flow<Result<Order>>
    suspend fun getUserOrders(userId: String): Flow<Result<List<Order>>>
    suspend fun getAvailableVouchers(userId: String): Flow<Result<List<Voucher>>>
    suspend fun getOrderById(orderId: String): Flow<Result<Order>>
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
        } catch (e: HttpException) {
            emit(Result.Error("Network error: ${e.message()}"))
        } catch (e: IOException) {
            emit(Result.Error("Please check your internet connection"))
        } catch (e: Exception) {
            emit(Result.Error("An unexpected error occurred: ${e.message}"))
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
        } catch (e: HttpException) {
            emit(Result.Error("Network error: ${e.message()}"))
        } catch (e: IOException) {
            emit(Result.Error("Please check your internet connection"))
        } catch (e: Exception) {
            emit(Result.Error("An unexpected error occurred: ${e.message}"))
        }
    }

    override suspend fun getOrderById(orderId: String): Flow<Result<Order>> = flow {
        try {
            emit(Result.Loading)
            Log.d(TAG, "Fetching order details for ID: $orderId")

            val response = apiService.getOrderByExt(orderId)
            if (response.isSuccessful) {
                val orders = response.body() ?: emptyList()
                if (orders.isNotEmpty()) {
                    val order = orders.first()
                    Log.d(TAG, "Successfully fetched order: ${order.id}")
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
        } catch (e: HttpException) {
            val errorMsg = "Network error: ${e.code()} - ${e.message()}"
            Log.e(TAG, errorMsg, e)
            emit(Result.Error(errorMsg))
        } catch (e: IOException) {
            val errorMsg = "Please check your internet connection"
            Log.e(TAG, errorMsg, e)
            emit(Result.Error(errorMsg))
        } catch (e: Exception) {
            val errorMsg =
                "An unexpected error occurred: ${e.localizedMessage ?: e.message ?: "Unknown error"}"
            Log.e(TAG, errorMsg, e)
            emit(Result.Error(errorMsg))
        }
    }

    override suspend fun getUserOrders(userId: String): Flow<Result<List<Order>>> = flow {
        try {
            emit(Result.Loading)
            val response = apiService.getUserOrders(userId)
            if (response.isSuccessful) {
                emit(Result.Success(response.body() ?: emptyList()))
            } else {
                emit(Result.Error("Failed to fetch orders: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Result.Error("Network error: ${e.message()}"))
        } catch (e: IOException) {
            emit(Result.Error("Please check your internet connection"))
        } catch (e: Exception) {
            emit(Result.Error("An unexpected error occurred: ${e.message}"))
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
        } catch (e: HttpException) {
            emit(Result.Error("Network error: ${e.message()}"))
        } catch (e: IOException) {
            emit(Result.Error("Please check your internet connection"))
        } catch (e: Exception) {
            emit(Result.Error("An unexpected error occurred: ${e.message}"))
        }
    }
}