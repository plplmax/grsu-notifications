package com.github.plplmax.grsunotifications.notification

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.same
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class ScheduleNotificationChannelTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val notificationCentre: NotificationCentre = mock()
    private lateinit var channel: ScheduleNotificationChannel

    @Before
    fun before() {
        channel = ScheduleNotificationChannel(context, notificationCentre)
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

        channel.send(notification)

        verify(notificationCentre).send(any(), same(notification))
    }
}
