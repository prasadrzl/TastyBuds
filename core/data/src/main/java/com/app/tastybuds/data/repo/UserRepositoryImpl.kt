package com.app.tastybuds.data.repo

import com.app.tastybuds.domain.repository.UserRepository

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.mapper.toUserDomainModel
import com.app.tastybuds.domain.model.UpdateProfileRequest
import com.app.tastybuds.domain.model.User
import com.app.tastybuds.core.data.util.ErrorHandler
import com.app.tastybuds.util.Result
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: TastyBudsApiService
) : UserRepository {

    override suspend fun getUser(userId: String): Result<User> {
        return try {
            val response = apiService.getUser(userId)
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

    override suspend fun updateUser(
        userId: String,
        updateRequest: UpdateProfileRequest
    ): Result<User> {
        return try {
            val response =
                apiService.updateUser(userId, updateRequest)
            if (response.isSuccessful) {
                val updatedUser = response.body()?.firstOrNull()
                if (updatedUser != null) {
                    Result.Success(updatedUser.toUserDomainModel())
                } else {
                    Result.Error("Failed to update user")
                }
            } else {
                Result.Error("Update failed: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error(ErrorHandler.handleApiError(e))
        }
    }
}