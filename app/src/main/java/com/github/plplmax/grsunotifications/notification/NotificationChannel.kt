package com.github.plplmax.grsunotifications.notification

import android.app.Notification
import androidx.core.app.NotificationCompat

interface NotificationChannel {
    val builder: NotificationCompat.Builder

    fun create()
    fun delete()
    fun send(notification: Notification)
}
