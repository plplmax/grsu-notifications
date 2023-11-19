package com.github.plplmax.notifications.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.plplmax.notifications.R
import com.github.plplmax.notifications.channel.ScheduleNotificationChannel
import com.github.plplmax.notifications.data.notification.ScheduleNotifications
import com.github.plplmax.notifications.data.notification.models.ScheduleDiffNotification
import com.github.plplmax.notifications.notification.NotificationType
import com.github.plplmax.notifications.notification.ScheduleNotification
import com.github.plplmax.notifications.resources.Resources
import com.github.plplmax.notifications.update.ScheduleDiffUpdate

class ScheduleWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val diffUpdate: ScheduleDiffUpdate,
    private val scheduleNotifications: ScheduleNotifications,
    private val notificationChannel: ScheduleNotificationChannel,
    private val resources: Resources
) : CoroutineWorker(context, workerParams) {
    private val showNotificationWithFailure: Boolean
        get() = runAttemptCount == 3

    override suspend fun doWork(): Result {
        val scheduleDiffResult = diffUpdate.diff()

        if (scheduleDiffResult.isFailure) {
            if (showNotificationWithFailure) {
                ScheduleNotification(
                    title = resources.string(R.string.schedule_update_error),
                    text = resources.string(R.string.lets_try_again),
                    type = NotificationType.FAILED
                ).send(notificationChannel)
            }
            return Result.retry()
        }

        notificationChannel.cancelFailedNotifications()
        val scheduleDiff = scheduleDiffResult.getOrThrow()

        if (scheduleDiff.days.isNotEmpty()) {
            val diffNotification = ScheduleDiffNotification(diff = scheduleDiff)
            scheduleNotifications.save(diffNotification)

            ScheduleNotification(
                title = resources.string(R.string.schedule_changed),
                text = resources.string(R.string.tap_to_view_notifications)
            ).send(notificationChannel)
        }

        return Result.success()
    }
}
