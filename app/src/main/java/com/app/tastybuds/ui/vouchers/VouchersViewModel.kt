package com.app.tastybuds.ui.vouchers

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.data.repo.AuthRepository
import com.app.tastybuds.domain.VouchersUseCase
import com.app.tastybuds.util.onError
import com.app.tastybuds.util.onLoading
import com.app.tastybuds.util.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VouchersViewModel @Inject constructor(
    private val vouchersUseCase: VouchersUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VouchersUiState())
    val uiState: StateFlow<VouchersUiState> = _uiState.asStateFlow()

    private val _userId = MutableStateFlow("")

    companion object {
        private const val TAG = "VouchersViewModel"
    }

    init {
        loadUserId()
    }

    private fun loadUserId() {
        viewModelScope.launch {
            try {
                authRepository.getUserId().first()?.let { userId ->
                    _userId.value = userId
                    Log.d(TAG, "User ID loaded: $userId")
                    loadVouchers()
                } ?: run {
                    Log.w(TAG, "User ID is null")
                    _uiState.update { it.copy(error = "Please log in to view vouchers") }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user ID: ${e.message}")
                _uiState.update { it.copy(error = "Failed to load user information") }
            }
        }
    }

    private fun loadVouchers() {
        if (_userId.value.isEmpty()) {
            return
        }

        viewModelScope.launch {
            loadActiveVouchers()
            loadUsedVouchers()
        }
    }

    private fun loadActiveVouchers() {
        viewModelScope.launch {
            vouchersUseCase.getActiveVouchers(_userId.value).collect { result ->
                result.onLoading {
                    _uiState.update { it.copy(isLoading = true, error = null) }
                }.onSuccess { vouchers ->
                    _uiState.update {
                        it.copy(
                            activeVouchers = vouchers,
                            isLoading = false,
                            error = null
                        )
                    }
                }.onError { errorMessage ->
                    _uiState.update {
                        it.copy(
                            error = errorMessage,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun loadUsedVouchers() {
        viewModelScope.launch {
            vouchersUseCase.getUsedVouchers(_userId.value).collect { result ->
                result.onLoading {}
                    .onSuccess { vouchers ->
                        Log.d(TAG, "Used vouchers loaded: ${vouchers.size}")
                        _uiState.update {
                            it.copy(usedVouchers = vouchers)
                        }
                    }.onError { errorMessage ->
                        Log.e(TAG, "Error loading used vouchers: $errorMessage")
                    }
            }
        }
    }

    fun refreshVouchers() {
        if (_userId.value.isEmpty()) return

        Log.d(TAG, "Refreshing vouchers")
        _uiState.update { it.copy(isRefreshing = true, error = null) }

        viewModelScope.launch {
            try {
                val result = vouchersUseCase.refreshVouchers(_userId.value)
                result.onSuccess {
                    loadVouchers() // Reload all vouchers
                }.onError { errorMessage ->
                    Log.e(TAG, "Error refreshing vouchers: $errorMessage")
                    _uiState.update {
                        it.copy(
                            error = errorMessage,
                            isRefreshing = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Failed to refresh vouchers",
                        isRefreshing = false
                    )
                }
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun onVoucherClick(voucherId: String) {
        Log.d(TAG, "Voucher clicked: $voucherId")
    }
}