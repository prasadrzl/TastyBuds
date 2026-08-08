package com.app.tastybuds.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.tastybuds.R
import com.app.tastybuds.domain.model.NotificationItem

/** Builds the notification channel and shows an incoming notification in the system tray. */
object NotificationHelper {

    const val CHANNEL_ID = "tastybuds_default"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "General",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Order updates, promotions and messages" }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    fun show(context: Context, item: NotificationItem) {
        ensureChannel(context)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bn_inbox)
            .setContentTitle(item.title)
            .setContentText(item.body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            try {
                NotificationManagerCompat.from(context).notify(item.id.hashCode(), notification)
            } catch (_: SecurityException) {
                // permission not granted yet — the inbox tab still records it
            }
        }
    }
}
