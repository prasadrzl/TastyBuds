package com.app.tastybuds.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.domain.HomeUseCase
import com.app.tastybuds.ui.home.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeUseCase: HomeUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                homeUseCase.getHomeData().collect { homeData ->
                    _uiState.value = HomeUiState(
                        isLoading = false,
                        banners = homeData.banners,
                        categories = homeData.categories,
                        voucherCount = homeData.voucherCount,
                        collections = homeData.collections,
                        recommendedRestaurants = homeData.recommendedRestaurants,
                        deals = homeData.deals
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = when (e) {
                            is IOException -> "Network error. Please check your connection."
                            else -> e.localizedMessage ?: "An unknown error occurred"
                        }
                    )
                }
            }
        }
    }

    fun retry() {
        loadHomeData()
    }
}