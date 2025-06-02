package com.app.tastybuds.ui.profile

sealed class ProfileUiModel {
    data object Loading : ProfileUiModel()
    data class Success(val profileData: ProfileData) : ProfileUiModel()
    data class Error(val message: String) : ProfileUiModel()
    data object Empty : ProfileUiModel()

    data class ProfileData(
        val name: String,
        val email: String,
        val imageUrl: String
    )
}