package com.app.tastybuds.domain

import com.app.tastybuds.data.repo.UserRepository
import com.app.tastybuds.domain.model.User
import com.app.tastybuds.domain.model.UpdateUserRequest
import com.app.tastybuds.util.Result
import kotlinx.coroutines.flow.Flow
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
    
    fun getUserFlow(userId: String): Flow<Result<User>> {
        return userRepository.getUserFlow(userId)
    }
    
    // Business logic validation
    private fun isValidUpdateRequest(updateRequest: UpdateUserRequest): Boolean {
        // Validate that at least one field is being updated
        val hasUpdate = updateRequest.name != null || 
                       updateRequest.email != null || 
                       updateRequest.profileUrl != null
        
        if (!hasUpdate) return false
        
        // Validate email if it's being updated
        updateRequest.email?.let { email ->
            if (email.isNotBlank() && !isValidEmail(email)) {
                return false
            }
        }
        
        // Validate name if it's being updated
        updateRequest.name?.let { name ->
            if (name.isBlank()) {
                return false
            }
        }
        
        return true
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}