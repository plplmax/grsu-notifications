package com.github.plplmax.notifications.centre

import android.app.Notification
import androidx.core.app.NotificationChannelCompat

interface NotificationCentre {
    val hasNotificationsPermission: Boolean

    fun createChannel(channel: NotificationChannelCompat)
    fun deleteChannel(id: String)
    fun send(notificationId: Int, notification: Notification)
    fun cancelNotification(id: Int)
}
