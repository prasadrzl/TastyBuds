package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.LoginRequest
import com.app.tastybuds.data.model.mapper.toUserDomainModel
import com.app.tastybuds.domain.model.User
import com.app.tastybuds.domain.repository.AuthRepository
import com.app.tastybuds.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val localDataSource: UserLocalDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val apiService: TastyBudsApiService,
    private val tokenStore: AuthTokenStore
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            val body = response.body()

            if (response.isSuccessful && body?.user != null) {
                tokenStore.setToken(body.token)
                val user = body.user.toUserDomainModel()
                localDataSource.saveLoginState(user)
                Result.Success(user)
            } else if (response.code() == 401) {
                Result.Error("Invalid email or password")
            } else {
                Result.Error("Login failed: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Login failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    override fun isLoggedIn(): Flow<Boolean> = localDataSource.isLoggedIn()

    override fun getUserId(): Flow<String?> = localDataSource.getUserId()

    override suspend fun logout() {
        tokenStore.clear()
        localDataSource.clearLoginState()
    }

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val userId = localDataSource.getUserId()
            userRemoteDataSource.getUserById(userId.first() ?: "")
        } catch (e: Exception) {
            Result.Error("Failed to get current user: ${e.localizedMessage}")
        }
    }
}