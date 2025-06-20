package com.app.tastybuds.util

import android.os.Build
import androidx.annotation.RequiresExtension
import okio.IOException
import retrofit2.HttpException

object ErrorHandler {

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun handleApiError(exception: Throwable): String {
        return when (exception) {
            is HttpException -> {
                when (exception.code()) {
                    400 -> "Invalid request. Please check your input."
                    401 -> "Unauthorized. Please login again."
                    403 -> "Access forbidden."
                    404 -> "Resource not found."
                    500 -> "Server error. Please try again later."
                    else -> "Network error: ${exception.message()}"
                }
            }

            is IOException -> "Network connection error. Please check your internet connection."
            else -> exception.message ?: "An unexpected error occurred."
        }
    }
}