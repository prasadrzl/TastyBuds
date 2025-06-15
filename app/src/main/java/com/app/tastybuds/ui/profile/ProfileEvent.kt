package com.app.tastybuds.ui.profile

sealed class ProfileEvent {
    data object LoadProfile : ProfileEvent()
    data object StartEditing : ProfileEvent()
    data object CancelEditing : ProfileEvent()
    data object SaveProfile : ProfileEvent()
    data object DismissError : ProfileEvent()
    data object DismissSuccess : ProfileEvent()
    data class UpdateName(val name: String) : ProfileEvent()
    data class UpdateEmail(val email: String) : ProfileEvent()
    data class UpdateProfileImage(val imageUrl: String) : ProfileEvent()
}