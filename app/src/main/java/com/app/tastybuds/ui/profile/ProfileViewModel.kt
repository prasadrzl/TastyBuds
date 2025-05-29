package com.app.tastybuds.ui.profile

@HiltViewModel
class ProfileViewModel @inject constructor(): ViewModel() {
    private val _profileUiState = MutableStateFlow<ProfileUiModel>(ProfileUiModel.Empty)
    val profileUiState: StateFlow<ProfileUiModel> = _profileUiState

    fun getProfileData(){
        _profileUiState.value = ProfileUiModel.Loading

    }

}