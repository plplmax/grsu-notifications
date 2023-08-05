package com.github.plplmax.notifications.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.github.plplmax.notifications.channel.ScheduleNotificationChannel
import com.github.plplmax.notifications.data.notification.ScheduleNotifications
import com.github.plplmax.notifications.resources.Resources
import com.github.plplmax.notifications.update.ScheduleDiffUpdate

class ScheduleWorkerFactory(
    private val diffUpdate: ScheduleDiffUpdate,
    private val scheduleNotifications: ScheduleNotifications,
    private val notificationChannel: ScheduleNotificationChannel,
    private val resources: Resources
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker =
        ScheduleWorker(
            appContext,
            workerParameters,
            diffUpdate,
            scheduleNotifications,
            notificationChannel,
            resources
        )
}
