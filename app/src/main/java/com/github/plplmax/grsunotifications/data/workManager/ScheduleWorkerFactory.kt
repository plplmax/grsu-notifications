package com.github.plplmax.grsunotifications.data.workManager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.github.plplmax.grsunotifications.data.ScheduleRepository
import com.github.plplmax.grsunotifications.data.UserRepository
import com.github.plplmax.grsunotifications.notification.NotificationCentre

class ScheduleWorkerFactory(
    private val userRepository: UserRepository,
    private val scheduleRepository: ScheduleRepository,
    private val notificationCentre: NotificationCentre
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
            notificationCentre
        )
}
