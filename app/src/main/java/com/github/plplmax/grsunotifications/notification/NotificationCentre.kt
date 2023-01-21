package com.github.plplmax.grsunotifications.notification

import android.app.Notification
import androidx.core.app.NotificationChannelCompat

interface NotificationCentre {
    val hasNotificationsPermission: Boolean

    fun createChannel(channel: NotificationChannelCompat)
    fun deleteChannel(id: String)
    fun send(notificationId: Int, notification: Notification)
}
