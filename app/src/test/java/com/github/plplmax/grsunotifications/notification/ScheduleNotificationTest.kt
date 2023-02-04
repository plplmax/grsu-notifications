package com.github.plplmax.grsunotifications.notification

import androidx.core.app.NotificationCompat
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class ScheduleNotificationTest {
    private lateinit var notification: ScheduleNotification
    private val channel: NotificationChannel = mock()
    private val builder: NotificationCompat.Builder = mock()
    private val androidNotification: android.app.Notification = mock()

    @Before
    fun before() {
        this.notification = ScheduleNotification(TITLE, TEXT)
        whenever(channel.builder).thenReturn(builder)
        whenever(builder.setContentTitle(any())).thenReturn(builder)
        whenever(builder.setContentText(any())).thenReturn(builder)
        whenever(builder.setStyle(any())).thenReturn(builder)
        whenever(builder.build()).thenReturn(androidNotification)
    }

    @Test
    fun `when sending notification should use send`() {
        notification.send(channel)

        verify(channel).send(any())
    }

    @Test
    fun `when sending notification should use notification builder`() {
        notification.send(channel)

        builder.inOrder {
            verify().setContentTitle(eq(TITLE))
            verify().setContentText(eq(TEXT))
            verify().setStyle(any())
            verify().build()
            Unit
        }
    }

    @Test
    fun `when sending notification should send notification from builder`() {
        notification.send(channel)

        verify(channel).send(androidNotification)
    }

    companion object {
        private const val TITLE = "TITLE"
        private const val TEXT = "TEXT"
    }
}
