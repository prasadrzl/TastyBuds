package com.app.tastybuds.domain.repository

/** Registers this device's FCM token with the backend so the server can target it for push. */
interface DeviceRepository {
    suspend fun registerToken(userId: String, token: String)
}
