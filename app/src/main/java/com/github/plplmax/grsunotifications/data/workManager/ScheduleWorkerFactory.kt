package com.github.plplmax.grsunotifications.data.workManager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.github.plplmax.grsunotifications.data.schedule.ScheduleRepository
import com.github.plplmax.grsunotifications.data.user.UserRepository
import com.github.plplmax.grsunotifications.notification.NotificationChannel
import com.github.plplmax.grsunotifications.resources.Resources

class ScheduleWorkerFactory(
    private val userRepository: UserRepository,
    private val scheduleRepository: ScheduleRepository,
    private val notificationChannel: NotificationChannel,
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
            userRepository,
            scheduleRepository,
            notificationChannel,
            resources
        )
}
