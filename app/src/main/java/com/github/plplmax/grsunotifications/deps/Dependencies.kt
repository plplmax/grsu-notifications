package com.github.plplmax.grsunotifications.deps

import android.content.Context
import com.github.plplmax.grsunotifications.data.ScheduleRepository
import com.github.plplmax.grsunotifications.data.UserRepository
import com.github.plplmax.grsunotifications.data.impl.*
import com.github.plplmax.grsunotifications.notification.GrsuNotificationCentre
import com.github.plplmax.grsunotifications.notification.NotificationCentre
import okhttp3.OkHttpClient

class Dependencies(context: Context) {
    private val httpClient: OkHttpClient by lazy { OkHttpClient() }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(
            RemoteUserDataSourceImpl(httpClient),
            LocalUserDataSourceImpl(context)
        )
    }

    val scheduleRepository: ScheduleRepository by lazy {
        ScheduleRepositoryImpl(
            RemoteScheduleDataSourceImpl(httpClient),
            LocalScheduleDataSourceImpl(context)
        )
    }

    val notificationCentre: NotificationCentre by lazy {
        GrsuNotificationCentre(context)
    }
}
