package com.github.plplmax.notifications.notification

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class GrsuNotificationCentreTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val notificationManager: NotificationManagerCompat = mock()
    private val notificationChannel: NotificationChannelCompat = mock()
    private lateinit var notificationCentre: GrsuNotificationCentre

    @Before
    fun before() {
        this.notificationCentre = GrsuNotificationCentre(context, notificationManager)
    }

    @Test
    fun `when creating a channel should be invoked createNotificationChannel`() {
        notificationCentre.createChannel(notificationChannel)

        verify(notificationManager).createNotificationChannel(notificationChannel)
    }

    @Test
    fun `when deleting a channel should be invoked deleteNotificationChannel`() {
        val channelId = "1"

        notificationCentre.deleteChannel(channelId)

        verify(notificationManager).deleteNotificationChannel(channelId)
    }

    @Test
    fun `when sending a notification should be invoked notify`() {
        val notification = mock<android.app.Notification>()

        notificationCentre.send(NOTIFICATION_ID, notification)

        verify(notificationManager).notify(NOTIFICATION_ID, notification)
    }

    @Test
    fun `when canceling a notification should be invoked cancel`() {
        notificationCentre.cancelNotification(NOTIFICATION_ID)

        verify(notificationManager).cancel(NOTIFICATION_ID)
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}
