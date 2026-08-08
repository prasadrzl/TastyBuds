package com.app.tastybuds.messaging

import android.util.Log
import com.app.tastybuds.domain.InboxUseCase
import com.app.tastybuds.domain.model.NotificationItem
import com.app.tastybuds.domain.repository.AuthRepository
import com.app.tastybuds.domain.repository.DeviceRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * Entry point for FCM pushes. For each message it (1) saves a [NotificationItem] to the Room inbox
 * so the Inbox tab shows history, and (2) shows a system-tray notification.
 *
 * Works with both "notification" messages (delivered to the tray by the system when the app is in
 * the background) and "data" messages (delivered here in all states).
 */
@AndroidEntryPoint
class TastyBudsMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var inboxUseCase: InboxUseCase

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var deviceRepository: DeviceRepository

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data
        val item = NotificationItem(
            id = message.messageId ?: UUID.randomUUID().toString(),
            title = message.notification?.title ?: data["title"] ?: "TastyBuds",
            body = message.notification?.body ?: data["body"].orEmpty(),
            type = data["type"] ?: "GENERAL",
            timestamp = System.currentTimeMillis(),
            read = false,
            deepLink = data["deepLink"]
        )

        scope.launch { inboxUseCase.save(item) }
        NotificationHelper.show(applicationContext, item)
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "FCM registration token: $token")
        // Register with the backend so the server can target this device (POST /api/v1/devices).
        // Only when signed in; otherwise it's registered after login / next token refresh.
        scope.launch {
            val userId = authRepository.getUserId().first()
            if (!userId.isNullOrBlank()) {
                deviceRepository.registerToken(userId, token)
            }
        }
    }

    private companion object {
        const val TAG = "TastyBudsFCM"
    }
}
