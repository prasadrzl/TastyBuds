package com.app.tastybuds.domain.repository

import com.app.tastybuds.domain.model.Voucher
import com.app.tastybuds.util.Result
import kotlinx.coroutines.flow.Flow

interface VouchersRepository {
    fun getUserVouchers(userId: String): Flow<Result<List<Voucher>>>
    fun getActiveVouchers(userId: String): Flow<Result<List<Voucher>>>
    fun getUsedVouchers(userId: String): Flow<Result<List<Voucher>>>
    fun getRestaurantVouchers(userId: String, restaurantId: String): Flow<Result<List<Voucher>>>
    suspend fun markVoucherAsUsed(voucherId: String, orderId: String): Result<Voucher>
    suspend fun refreshVouchers(userId: String): Result<List<Voucher>>
}
