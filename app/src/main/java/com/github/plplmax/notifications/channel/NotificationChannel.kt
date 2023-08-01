package com.github.plplmax.notifications.channel

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat

interface NotificationChannel {
    val builder: NotificationCompat.Builder

    fun create()
    fun delete()
    fun send(id: Int, notification: Notification)
    fun cancelNotification(id: Int)
    fun pendingIntent(configuration: Intent.() -> Unit): PendingIntent
}
