package com.app.tastybuds.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tastybuds.domain.UserUseCase
import com.app.tastybuds.domain.model.UpdateUserRequest
import com.app.tastybuds.util.Result
import com.app.tastybuds.util.ui.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val themeManager: ThemeManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(EditProfileFormState())
    val formState: StateFlow<EditProfileFormState> = _formState.asStateFlow()

    private var currentUserId: String = ""

    fun handleEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> loadProfile()
            is ProfileEvent.StartEditing -> startEditing()
            is ProfileEvent.CancelEditing -> cancelEditing()
            is ProfileEvent.SaveProfile -> saveProfile()
            is ProfileEvent.DismissError -> dismissError()
            is ProfileEvent.DismissSuccess -> dismissSuccess()
            is ProfileEvent.UpdateName -> updateName(event.name)
            is ProfileEvent.UpdateEmail -> updateEmail(event.email)
            is ProfileEvent.UpdateProfileImage -> updateProfileImage(event.imageUrl)
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            userUseCase.getUserFlow(currentUserId)
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _uiState.update { it.copy(isLoading = true, error = null) }
                        }

                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    user = result.data,
                                    error = null
                                )
                            }
                            initializeFormState(result.data)
                        }

                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun initializeFormState(user: com.app.tastybuds.domain.model.User) {
        _formState.update {
            EditProfileFormState(
                name = user.name,
                email = user.email,
                profileUrl = user.profileUrl ?: ""
            )
        }
    }

    private fun startEditing() {
        _uiState.update { it.copy(isEditing = true, error = null) }
    }

    private fun cancelEditing() {
        _uiState.update { it.copy(isEditing = false, validationErrors = ValidationErrors()) }
        _uiState.value.user?.let { user ->
            initializeFormState(user)
        }
    }

    private fun saveProfile() {
        val currentForm = _formState.value
        val currentUser = _uiState.value.user

        val validationErrors = validateForm(currentForm)
        if (validationErrors.hasErrors()) {
            _uiState.update { it.copy(validationErrors = validationErrors) }
            return
        }

        if (!currentForm.hasChanges(currentUser)) {
            _uiState.update { it.copy(isEditing = false) }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isUpdating = true,
                    error = null,
                    validationErrors = ValidationErrors()
                )
            }

            val updateRequest = UpdateUserRequest(
                name = if (currentForm.name != currentUser?.name) currentForm.name else null,
                email = if (currentForm.email != currentUser?.email) currentForm.email else null,
                profileUrl = if (currentForm.profileUrl != (currentUser?.profileUrl
                        ?: "")
                ) currentForm.profileUrl else null
            )

            when (val result = userUseCase.updateUser(currentUserId, updateRequest)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            isEditing = false,
                            user = result.data,
                            updateSuccess = true,
                            error = null
                        )
                    }
                    initializeFormState(result.data)
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            error = result.message
                        )
                    }
                }

                is Result.Loading -> { /* Already handling */
                }
            }
        }
    }

    private fun validateForm(form: EditProfileFormState): ValidationErrors {
        return ValidationErrors(
            nameError = if (form.name.isBlank()) "Name is required" else null,
            emailError = when {
                form.email.isBlank() -> "Email is required"
                !android.util.Patterns.EMAIL_ADDRESS.matcher(form.email)
                    .matches() -> "Invalid email format"

                else -> null
            }
        )
    }

    private fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun dismissSuccess() {
        _uiState.update { it.copy(updateSuccess = false) }
    }

    private fun updateName(name: String) {
        _formState.update { it.copy(name = name) }
        if (_uiState.value.validationErrors.nameError != null) {
            _uiState.update {
                it.copy(validationErrors = it.validationErrors.copy(nameError = null))
            }
        }
    }

    private fun updateEmail(email: String) {
        _formState.update { it.copy(email = email) }
        if (_uiState.value.validationErrors.emailError != null) {
            _uiState.update {
                it.copy(validationErrors = it.validationErrors.copy(emailError = null))
            }
        }
    }

    private fun updateProfileImage(imageUrl: String) {
        _formState.update { it.copy(profileUrl = imageUrl) }
    }

    fun initialize(userId: String) {
        currentUserId = userId
        handleEvent(ProfileEvent.LoadProfile)
    }

    val isDarkMode = themeManager.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun toggleDarkMode() {
        viewModelScope.launch {
            themeManager.toggleDarkMode()
        }
    }

    fun setDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            themeManager.setDarkMode(isDark)
        }
    }
}