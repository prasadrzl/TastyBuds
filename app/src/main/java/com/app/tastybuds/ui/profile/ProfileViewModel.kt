package com.app.tastybuds.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(): ViewModel() {
    private val _profileUiState = MutableStateFlow<ProfileUiModel>(ProfileUiModel.Empty)
    val profileUiState: StateFlow<ProfileUiModel> = _profileUiState

    fun getProfileData(){
        _profileUiState.value = ProfileUiModel.Loading

    }

}