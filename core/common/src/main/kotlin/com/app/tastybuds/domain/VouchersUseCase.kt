package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.Voucher
import com.app.tastybuds.domain.repository.VouchersRepository
import com.app.tastybuds.util.Result
import javax.inject.Inject

class VouchersUseCase @Inject constructor(
    private val repository: VouchersRepository
) {

    suspend fun getActiveVouchers(userId: String): Result<List<Voucher>> {
        return when (val result = repository.getActiveVouchers(userId)) {
            is Result.Success -> {
                val filteredVouchers = result.data
                    .filter { voucher -> voucher.canBeUsed && !voucher.isExpired }
                    .sortedWith(
                        compareBy<Voucher> { it.isExpired }
                            .thenByDescending { it.value }
                            .thenBy { it.expiryDate }
                    )
                Result.Success(filteredVouchers)
            }

            is Result.Error -> result
            is Result.Loading -> result
        }
    }

    suspend fun getUsedVouchers(userId: String): Result<List<Voucher>> {
        return when (val result = repository.getUsedVouchers(userId)) {
            is Result.Success -> {
                val sortedVouchers = result.data
                    .sortedByDescending { it.createdAt }
                Result.Success(sortedVouchers)
            }

            is Result.Error -> result
            is Result.Loading -> result
        }
    }

    suspend fun refreshVouchers(userId: String): Result<List<Voucher>> {
        return repository.refreshVouchers(userId)
    }
}