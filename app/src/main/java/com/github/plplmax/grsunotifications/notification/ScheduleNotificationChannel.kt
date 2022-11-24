package com.github.plplmax.grsunotifications.notification

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.github.plplmax.grsunotifications.R

class ScheduleNotificationChannel(
    private val context: Context,
    private val manager: NotificationManager
) : NotificationChannel {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun create() {
        manager.createNotificationChannel(channel())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun delete() {
        manager.deleteNotificationChannel(CHANNEL_ID)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun channel(): android.app.NotificationChannel {
        val name = context.getString(R.string.channel_name)
        val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        return android.app.NotificationChannel(
            CHANNEL_ID,
            name,
            importance
        ).apply {
            description = descriptionText
            enableVibration(true)
        }
    }

    companion object {
        const val CHANNEL_ID = "1"
    }
}
