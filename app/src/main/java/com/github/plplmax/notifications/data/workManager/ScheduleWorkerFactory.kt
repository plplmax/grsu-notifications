package com.github.plplmax.notifications.data.workManager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.github.plplmax.notifications.data.diffedSchedule.DiffedScheduleRepository
import com.github.plplmax.notifications.data.schedule.ScheduleRepository
import com.github.plplmax.notifications.data.user.UserRepository
import com.github.plplmax.notifications.notification.ScheduleNotificationChannel
import com.github.plplmax.notifications.resources.Resources

class ScheduleWorkerFactory(
    private val userRepository: UserRepository,
    private val scheduleRepository: ScheduleRepository,
    private val diffedScheduleRepository: DiffedScheduleRepository,
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
            userRepository,
            scheduleRepository,
            diffedScheduleRepository,
            notificationChannel,
            resources
        )
}
