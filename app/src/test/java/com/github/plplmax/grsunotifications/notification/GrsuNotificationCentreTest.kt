package com.github.plplmax.grsunotifications.notification

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class GrsuNotificationCentreTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val notificationManager: NotificationManagerCompat = mock()
    private val notificationChannel: NotificationChannelCompat = mock()

    @Test
    fun `when creating a channel should be invoked createNotificationChannel`() {
        val notificationCentre = GrsuNotificationCentre(context, notificationManager)

        notificationCentre.createChannel(notificationChannel)

        verify(notificationManager).createNotificationChannel(notificationChannel)
    }

    @Test
    fun `when deleting a channel should be invoked deleteNotificationChannel`() {
        val notificationCentre = GrsuNotificationCentre(context, notificationManager)
        val channelId = "1"

        notificationCentre.deleteChannel(channelId)

        verify(notificationManager).deleteNotificationChannel(channelId)
    }

    @Test
    fun `when sending a notification should be invoked notify`() {
        val notificationCentre = GrsuNotificationCentre(context, notificationManager)
        val notificationId = 1
        val notification = mock<android.app.Notification>()

        notificationCentre.send(notificationId, notification)

        verify(notificationManager).notify(notificationId, notification)
    }
}
