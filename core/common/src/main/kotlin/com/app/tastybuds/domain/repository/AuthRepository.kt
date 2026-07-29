package com.app.tastybuds.domain.repository

import com.app.tastybuds.domain.model.User
import com.app.tastybuds.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Auth/session contract. Lives in the domain layer so features depend on this interface
 * (not the concrete data-layer implementation).
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    fun isLoggedIn(): Flow<Boolean>
    fun getUserId(): Flow<String?>
    suspend fun logout()
    suspend fun getCurrentUser(): Result<User>
}
