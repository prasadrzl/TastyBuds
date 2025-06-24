package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.mapper.toUserDomainModel
import com.app.tastybuds.domain.model.User
import com.app.tastybuds.util.ErrorHandler
import com.app.tastybuds.util.Result
import com.app.tastybuds.util.getUserBy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteDataSource @Inject constructor(
    private val apiService: TastyBudsApiService
) {
    suspend fun getUserByEmail(email: String): Result<User> {
        return try {
            val response = apiService.getUserBy(email = email)
            if (response.isSuccessful) {
                val users = response.body() ?: emptyList()
                val user = users.find { it.email.equals(email, ignoreCase = true) }

                if (user != null) {
                    Result.Success(user.toUserDomainModel())
                } else {
                    Result.Error("User not found with email: $email")
                }
            } else {
                Result.Error("Failed to fetch user: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }

    suspend fun getUserById(userId: String): Result<User> {
        return try {
            val response = apiService.getUser(userId = "eq.$userId")
            if (response.isSuccessful) {
                val userResponse = response.body()?.firstOrNull()
                if (userResponse != null) {
                    Result.Success(userResponse.toUserDomainModel())
                } else {
                    Result.Error("User not found")
                }
            } else {
                Result.Error("Failed to fetch user: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }
}