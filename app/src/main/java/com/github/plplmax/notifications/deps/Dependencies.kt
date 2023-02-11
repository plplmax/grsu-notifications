package com.github.plplmax.notifications.deps

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.github.plplmax.notifications.data.schedule.ScheduleRepository
import com.github.plplmax.notifications.data.schedule.ScheduleRepositoryImpl
import com.github.plplmax.notifications.data.schedule.local.ScheduleLocalDataSourceImpl
import com.github.plplmax.notifications.data.schedule.remote.ScheduleRemoteDataSourceImpl
import com.github.plplmax.notifications.data.user.UserRepository
import com.github.plplmax.notifications.data.user.UserRepositoryImpl
import com.github.plplmax.notifications.data.user.local.LocalUserDataSourceImpl
import com.github.plplmax.notifications.data.user.remote.RemoteUserDataSourceImpl
import com.github.plplmax.notifications.notification.GrsuNotificationCentre
import com.github.plplmax.notifications.notification.NotificationCentre
import com.github.plplmax.notifications.notification.NotificationChannel
import com.github.plplmax.notifications.notification.ScheduleNotificationChannel
import com.github.plplmax.notifications.resources.GrsuResources
import com.github.plplmax.notifications.resources.Resources
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

class Dependencies(context: Context) {
    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().addInterceptor(
            HttpLoggingInterceptor { message -> Timber.tag("OkHttp").d(message) }.setLevel(
                HttpLoggingInterceptor.Level.BASIC
            )
        ).build()
    }

    val resources: Resources by lazy { GrsuResources(context) }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(
            RemoteUserDataSourceImpl(httpClient),
            LocalUserDataSourceImpl(context)
        )
    }

    val scheduleRepository: ScheduleRepository by lazy {
        ScheduleRepositoryImpl(
            ScheduleRemoteDataSourceImpl(httpClient),
            ScheduleLocalDataSourceImpl(context)
        )
    }

    val notificationCentre: NotificationCentre by lazy {
        GrsuNotificationCentre(context, NotificationManagerCompat.from(context))
    }

    val scheduleNotificationChannel: NotificationChannel by lazy {
        ScheduleNotificationChannel(context, notificationCentre)
    }
}
