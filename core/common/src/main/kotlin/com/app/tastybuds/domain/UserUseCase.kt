package com.app.tastybuds.domain

import com.app.tastybuds.domain.repository.UserRepository
import com.app.tastybuds.domain.model.User
import com.app.tastybuds.data.model.UpdateUserRequest
import com.app.tastybuds.util.Result
import javax.inject.Inject

class UserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend fun getUser(userId: String): Result<User> {
        return userRepository.getUser(userId)
    }

    suspend fun updateUser(userId: String, updateRequest: UpdateUserRequest): Result<User> {
        return if (isValidUpdateRequest(updateRequest)) {
            userRepository.updateUser(userId, updateRequest)
        } else {
            Result.Error("Invalid user data")
        }
    }

    private fun isValidUpdateRequest(updateRequest: UpdateUserRequest): Boolean {
        val hasUpdate = updateRequest.name != null ||
                updateRequest.email != null ||
                updateRequest.profileUrl != null

        if (!hasUpdate) return false

        updateRequest.email?.let { email ->
            if (email.isNotBlank() && !isValidEmail(email)) {
                return false
            }
        }

        updateRequest.name?.let { name ->
            if (name.isBlank()) {
                return false
            }
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return EMAIL_REGEX.matches(email)
    }

    companion object {
        // Pure-Kotlin substitute for android.util.Patterns.EMAIL_ADDRESS, which isn't
        // available outside the Android SDK (this is a plain Kotlin JVM module).
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}