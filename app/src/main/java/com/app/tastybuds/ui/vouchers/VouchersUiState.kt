package com.app.tastybuds.ui.vouchers

data class VouchersUiState(
    val isLoading: Boolean = false,
    val activeVouchers: List<Voucher> = emptyList(),
    val usedVouchers: List<Voucher> = emptyList(),
    val selectedTab: Int = 0,
    val error: String? = null,
    val isRefreshing: Boolean = false
) {
    val hasActiveVouchers: Boolean
        get() = activeVouchers.isNotEmpty()
    
    val hasUsedVouchers: Boolean
        get() = usedVouchers.isNotEmpty()
    
    val activeVouchersCount: Int
        get() = activeVouchers.size
        
    val usedVouchersCount: Int
        get() = usedVouchers.size
}