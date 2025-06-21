package com.app.tastybuds.data.repo

import com.app.tastybuds.domain.model.User
import com.app.tastybuds.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val isPasswordValid = localDataSource.validatePassword(email, password)
            if (!isPasswordValid) {
                return Result.Error("Invalid email or password")
            }

            when (val userResult = remoteDataSource.getUserByEmail(email)) {
                is Result.Success -> {
                    val user = userResult.data
                    localDataSource.saveLoginState(user)
                    Result.Success(user)
                }

                is Result.Error -> {
                    Result.Error("Failed to fetch user profile: ${userResult.message}")
                }

                is Result.Loading -> Result.Loading
            }
        } catch (e: Exception) {
            Result.Error("Login failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    fun isLoggedIn(): Flow<Boolean> = localDataSource.isLoggedIn()

    fun getUserId(): Flow<String?> = localDataSource.getUserId()

    suspend fun logout() {
        localDataSource.clearLoginState()
    }

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val userId = localDataSource.getUserId()
            remoteDataSource.getUserById(userId.first() ?: "")
        } catch (e: Exception) {
            Result.Error("Failed to get current user: ${e.localizedMessage}")
        }
    }
}