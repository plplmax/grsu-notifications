package com.github.plplmax.grsunotifications.notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class GrsuNotificationCentre(private val context: Context) : NotificationCentre {
    override val hasNotificationsPermission: Boolean
        @SuppressLint("InlinedApi")
        get() = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
}
