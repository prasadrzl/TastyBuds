package com.app.tastybuds.domain.repository

import com.app.tastybuds.domain.model.Voucher
import com.app.tastybuds.util.Result

// Every method here is a single network call, not an ongoing observation — plain suspend
// functions returning Result<T>, not Flow<Result<T>>.

interface VouchersRepository {
    suspend fun getUserVouchers(userId: String): Result<List<Voucher>>
    suspend fun getActiveVouchers(userId: String): Result<List<Voucher>>
    suspend fun getUsedVouchers(userId: String): Result<List<Voucher>>
    suspend fun getRestaurantVouchers(userId: String, restaurantId: String): Result<List<Voucher>>
    suspend fun markVoucherAsUsed(voucherId: String, orderId: String): Result<Voucher>
    suspend fun refreshVouchers(userId: String): Result<List<Voucher>>
}
