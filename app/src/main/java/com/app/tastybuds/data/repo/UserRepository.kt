package com.app.tastybuds.data.repo

import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.mapper.toUpdateUserDataModel
import com.app.tastybuds.data.model.mapper.toUserDomainModel
import com.app.tastybuds.domain.model.UpdateUserRequest
import com.app.tastybuds.domain.model.User
import com.app.tastybuds.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

interface UserRepository {
    suspend fun getUser(userId: String): Result<User>
    suspend fun updateUser(userId: String, updateRequest: UpdateUserRequest): Result<User>
    fun getUserFlow(userId: String): Flow<Result<User>>
}

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: TastyBudsApiService
) : UserRepository {

    override suspend fun getUser(userId: String): Result<User> {
        return try {
            val response = apiService.getUser("eq.$userId")
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
            Result.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    override suspend fun updateUser(
        userId: String,
        updateRequest: UpdateUserRequest
    ): Result<User> {
        return try {
            val response =
                apiService.updateUser("eq.$userId", updateRequest.toUpdateUserDataModel())
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
            Result.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    override fun getUserFlow(userId: String): Flow<Result<User>> = flow {
        emit(Result.Loading)
        try {
            val result = getUser(userId)
            emit(result)
        } catch (e: Exception) {
            emit(Result.Error("Failed to load user: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }
}