package com.github.plplmax.notifications.channel

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.plplmax.notifications.FakeApp
import com.github.plplmax.notifications.centre.NotificationCentre
import com.github.plplmax.notifications.notification.NotificationType
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.*
import org.robolectric.annotation.Config

@Config(application = FakeApp::class)
@RunWith(AndroidJUnit4::class)
class ScheduleNotificationChannelOfTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val notificationCentre: NotificationCentre = mock()
    private val notificationId = NotificationType.SUCCESSFUL.id
    private lateinit var channel: ScheduleNotificationChannelOf

    @Before
    fun before() {
        channel = ScheduleNotificationChannelOf(context, notificationCentre)
    }

    @Test
    fun `when creating a channel should be invoked createChannel`() {
        channel.create()

        verify(notificationCentre).createChannel(any())
    }

    @Test
    fun `when deleting a channel should be invoked deleteChannel`() {
        channel.delete()

        verify(notificationCentre).deleteChannel(any())
    }

    @Test
    fun `when sending a notification should be invoked send`() {
        val notification = mock<android.app.Notification>()

        channel.send(notificationId, notification)

        verify(notificationCentre).send(eq(notificationId), same(notification))
    }

    @Test
    fun `when canceling notification cancelNotification should be invoked`() {
        channel.cancelNotification(notificationId)

        verify(notificationCentre).cancelNotification(notificationId)
    }

    @Test
    fun `when canceling failed notifications cancelNotification should be invoked`() {
        val channelSpy = spy(channel)

        channelSpy.cancelFailedNotifications()

        verify(channelSpy).cancelNotification(NotificationType.FAILED.id)
    }
}
