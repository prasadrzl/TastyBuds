package com.app.tastybuds.data.repo

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.domain.model.Voucher
import com.app.tastybuds.data.model.VoucherUsageRequest
import com.app.tastybuds.data.model.mapper.toVoucherDomainModelList
import com.app.tastybuds.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface VouchersRepository {
    fun getUserVouchers(userId: String): Flow<Result<List<Voucher>>>
    fun getActiveVouchers(userId: String): Flow<Result<List<Voucher>>>
    fun getUsedVouchers(userId: String): Flow<Result<List<Voucher>>>
    fun getRestaurantVouchers(userId: String, restaurantId: String): Flow<Result<List<Voucher>>>
    suspend fun markVoucherAsUsed(voucherId: String, orderId: String): Result<Voucher>
    suspend fun refreshVouchers(userId: String): Result<List<Voucher>>
}


@Singleton
class VouchersRepositoryImpl @Inject constructor(
    private val apiService: TastyBudsApiService
) : VouchersRepository {

    companion object {
        private const val TAG = "VouchersRepository"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getUserVouchers(userId: String): Flow<Result<List<Voucher>>> = flow {
        emit(Result.Loading)
        try {
            Log.d(TAG, "Fetching all vouchers for user: $userId")
            val response = apiService.getUserVouchers(userId = "eq.$userId")

            if (response.isSuccessful) {
                val vouchersApiResponse = response.body() ?: emptyList()

                val restaurantIds = vouchersApiResponse
                    .mapNotNull { it.restaurantId }
                    .distinct()

                val restaurantNames = if (restaurantIds.isNotEmpty()) {
                    fetchRestaurantNames(restaurantIds)
                } else {
                    emptyMap()
                }

                val vouchers = vouchersApiResponse.toVoucherDomainModelList(restaurantNames)
                emit(Result.Success(vouchers))
            } else {
                val errorMessage = "Failed to fetch vouchers: ${response.message()}"
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "unknown error"))
        }
    }

    override fun getActiveVouchers(userId: String): Flow<Result<List<Voucher>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getActiveVouchers(userId = "eq.$userId")

            if (response.isSuccessful) {
                val vouchersApiResponse = response.body() ?: emptyList()

                val restaurantIds = vouchersApiResponse
                    .mapNotNull { it.restaurantId }
                    .distinct()

                val restaurantNames = if (restaurantIds.isNotEmpty()) {
                    fetchRestaurantNames(restaurantIds)
                } else {
                    emptyMap()
                }

                val vouchers = vouchersApiResponse.toVoucherDomainModelList(restaurantNames)
                    .filter { it.canBeUsed } // Additional client-side filtering

                emit(Result.Success(vouchers))
            } else {
                val errorMessage = "Failed to fetch active vouchers: ${response.message()}"
                Log.e(TAG, errorMessage)
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            val errorMessage = "Network error: ${e.localizedMessage ?: "Unknown error"}"
            Log.e(TAG, errorMessage, e)
            emit(Result.Error(errorMessage))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getUsedVouchers(userId: String): Flow<Result<List<Voucher>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getUsedVouchers(userId = "eq.$userId")

            if (response.isSuccessful) {
                val vouchersApiResponse = response.body() ?: emptyList()

                val restaurantIds = vouchersApiResponse
                    .mapNotNull { it.restaurantId }
                    .distinct()

                val restaurantNames = if (restaurantIds.isNotEmpty()) {
                    fetchRestaurantNames(restaurantIds)
                } else {
                    emptyMap()
                }

                val vouchers = vouchersApiResponse.toVoucherDomainModelList(restaurantNames)
                emit(Result.Success(vouchers))
            } else {
                val errorMessage = "Failed to fetch used vouchers: ${response.message()}"
                Log.e(TAG, errorMessage)
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            val errorMessage = "Network error: ${e.localizedMessage ?: "Unknown error"}"
            Log.e(TAG, errorMessage, e)
            emit(Result.Error(errorMessage))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getRestaurantVouchers(
        userId: String,
        restaurantId: String
    ): Flow<Result<List<Voucher>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getRestaurantVouchers(
                userId = "eq.$userId",
                restaurantId = "eq.$restaurantId"
            )

            if (response.isSuccessful) {
                val vouchersApiResponse = response.body() ?: emptyList()

                val restaurantNames = if (restaurantId.isNotEmpty()) {
                    fetchRestaurantNames(listOf(restaurantId))
                } else {
                    emptyMap()
                }

                val vouchers = vouchersApiResponse.toVoucherDomainModelList(restaurantNames)
                emit(Result.Success(vouchers))
            } else {
                val errorMessage = "Failed to fetch restaurant vouchers: ${response.message()}"
                Log.e(TAG, errorMessage)
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            val errorMessage = "Network error: ${e.localizedMessage ?: "Unknown error"}"
            Log.e(TAG, errorMessage, e)
            emit(Result.Error(errorMessage))
        }
    }

    override suspend fun markVoucherAsUsed(voucherId: String, orderId: String): Result<Voucher> {
        return try {
            val voucherUsage = VoucherUsageRequest(
                isUsed = true,
                usedCount = 1
            )

            val response = apiService.markVoucherAsUsed(
                voucherId = "eq.$voucherId",
                voucherUsage = voucherUsage
            )

            if (response.isSuccessful) {
                val updatedVouchersApi = response.body()?.toVoucherDomainModelList()
                val updatedVoucher = updatedVouchersApi?.firstOrNull()

                if (updatedVoucher != null) {
                    Result.Success(updatedVoucher)
                } else {
                    Result.Error("Failed to get updated voucher")
                }
            } else {
                val errorMessage = "Failed to mark voucher as used: ${response.message()}"
                Log.e(TAG, errorMessage)
                Result.Error(errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "Network error: ${e.localizedMessage ?: "Unknown error"}"
            Log.e(TAG, errorMessage, e)
            Result.Error(errorMessage)
        }
    }

    override suspend fun refreshVouchers(userId: String): Result<List<Voucher>> {
        return try {
            val response = apiService.getUserVouchers(userId = "eq.$userId")

            if (response.isSuccessful) {
                val vouchersApiResponse = response.body() ?: emptyList()

                val restaurantIds = vouchersApiResponse
                    .mapNotNull { it.restaurantId }
                    .distinct()

                val restaurantNames = if (restaurantIds.isNotEmpty()) {
                    fetchRestaurantNames(restaurantIds)
                } else {
                    emptyMap()
                }

                val vouchers = vouchersApiResponse.toVoucherDomainModelList(restaurantNames)
                Result.Success(vouchers)
            } else {
                val errorMessage = "Failed to refresh vouchers: ${response.message()}"
                Log.e(TAG, errorMessage)
                Result.Error(errorMessage)
            }
        } catch (e: Exception) {
            val errorMessage = "Network error: ${e.localizedMessage ?: "Unknown error"}"
            Log.e(TAG, errorMessage, e)
            Result.Error(errorMessage)
        }
    }

    private suspend fun fetchRestaurantNames(restaurantIds: List<String>): Map<String, String> {
        return try {
            val idsQuery = restaurantIds.joinToString(",")
            val response = apiService.getRestaurantsByIds(restaurantIds = "($idsQuery)")

            if (response.isSuccessful) {
                response.body()?.associate { it.id to it.name } ?: emptyMap()
            } else {
                emptyMap()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error fetching restaurant names: ${e.message}")
            emptyMap()
        }
    }
}