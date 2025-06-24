package com.app.tastybuds.util

import okio.IOException
import retrofit2.HttpException

object ErrorHandler {

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