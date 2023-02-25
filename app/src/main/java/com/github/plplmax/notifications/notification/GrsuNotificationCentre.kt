package com.github.plplmax.notifications.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class GrsuNotificationCentre(
    private val context: Context,
    private val manager: NotificationManagerCompat
) : NotificationCentre {
    override val hasNotificationsPermission: Boolean
        @SuppressLint("InlinedApi")
        get() = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

    override fun createChannel(channel: NotificationChannelCompat) {
        this.manager.createNotificationChannel(channel)
    }

    override fun deleteChannel(id: String) {
        this.manager.deleteNotificationChannel(id)
    }

    override fun send(notificationId: Int, notification: Notification) {
        this.manager.notify(notificationId, notification)
    }

    override fun cancelNotification(id: Int) {
        this.manager.cancel(id)
    }
}
