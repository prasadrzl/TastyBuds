package com.app.tastybuds.data.repo

import android.util.Log
import com.app.tastybuds.common.TastyBudsApiService
import com.app.tastybuds.data.model.DeviceRegistrationRequest
import com.app.tastybuds.domain.repository.DeviceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceRepositoryImpl @Inject constructor(
    private val apiService: TastyBudsApiService
) : DeviceRepository {

    override suspend fun registerToken(userId: String, token: String) {
        // Best-effort: a failed registration shouldn't crash the FCM callback.
        runCatching { apiService.registerDevice(DeviceRegistrationRequest(userId, token)) }
            .onFailure { Log.w(TAG, "Device token registration failed: ${it.message}") }
    }

    private companion object {
        const val TAG = "DeviceRepository"
    }
}
