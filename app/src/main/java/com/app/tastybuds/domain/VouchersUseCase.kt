package com.app.tastybuds.domain

import com.app.tastybuds.domain.model.Voucher
import com.app.tastybuds.data.repo.VouchersRepository
import com.app.tastybuds.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VouchersUseCase @Inject constructor(
    private val repository: VouchersRepository
) {

    fun getActiveVouchers(userId: String): Flow<Result<List<Voucher>>> {
        return repository.getActiveVouchers(userId)
            .map { result ->
                when (result) {
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
    }

    fun getUsedVouchers(userId: String): Flow<Result<List<Voucher>>> {
        return repository.getUsedVouchers(userId)
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        val sortedVouchers = result.data
                            .sortedByDescending { it.createdAt }
                        Result.Success(sortedVouchers)
                    }

                    is Result.Error -> result
                    is Result.Loading -> result
                }
            }
    }

    suspend fun refreshVouchers(userId: String): Result<List<Voucher>> {
        return repository.refreshVouchers(userId)
    }
}