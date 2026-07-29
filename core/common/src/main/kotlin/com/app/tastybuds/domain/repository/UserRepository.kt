package com.app.tastybuds.domain.repository

import com.app.tastybuds.domain.model.UpdateProfileRequest
import com.app.tastybuds.domain.model.User
import com.app.tastybuds.util.Result

interface UserRepository {
    suspend fun getUser(userId: String): Result<User>
    suspend fun updateUser(userId: String, updateRequest: UpdateProfileRequest): Result<User>
}
