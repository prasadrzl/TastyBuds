package com.app.tastybuds.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingManager: OnboardingManager
) : ViewModel() {

    fun isOnboardingCompleted(): Boolean = onboardingManager.isOnboardingCompleted()

    fun markOnboardingCompleted() = onboardingManager.markOnboardingCompleted()

    fun isUserLoggedIn(): Boolean = onboardingManager.isUserLoggedIn()

    fun clearUserSession(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                onboardingManager.clearUserSessionAsync()
                onComplete()
            } catch (e: Exception) {
                onComplete()
            }
        }
    }
}