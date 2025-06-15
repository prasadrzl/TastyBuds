package com.app.tastybuds.ui.profile

import com.app.tastybuds.domain.model.User

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isEditing: Boolean = false,
    val isUpdating: Boolean = false,
    val updateSuccess: Boolean = false,
    val validationErrors: ValidationErrors = ValidationErrors()
)

data class ValidationErrors(
    val nameError: String? = null,
    val emailError: String? = null
) {
    fun hasErrors(): Boolean = nameError != null || emailError != null
}

data class EditProfileFormState(
    val name: String = "",
    val email: String = "",
    val profileUrl: String = ""
) {
    fun isValid(): Boolean {
        return name.isNotBlank() &&
                email.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun hasChanges(originalUser: User?): Boolean {
        originalUser ?: return true
        return name != originalUser.name ||
                email != originalUser.email ||
                profileUrl != (originalUser.profileUrl ?: "")
    }
}