package com.app.tastybuds.ui.vouchers

import com.app.tastybuds.domain.model.Voucher

data class VouchersUiState(
    val isLoading: Boolean = false,
    val activeVouchers: List<Voucher> = emptyList(),
    val usedVouchers: List<Voucher> = emptyList(),
    val selectedTab: Int = 0,
    val error: String? = null,
    val isRefreshing: Boolean = false
) {
    val activeVouchersCount: Int
        get() = activeVouchers.size

    val usedVouchersCount: Int
        get() = usedVouchers.size
}