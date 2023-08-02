package com.github.plplmax.notifications.deps

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.github.plplmax.notifications.centre.AppNotificationCentre
import com.github.plplmax.notifications.centre.NotificationCentre
import com.github.plplmax.notifications.channel.ScheduleNotificationChannel
import com.github.plplmax.notifications.channel.ScheduleNotificationChannelOf
import com.github.plplmax.notifications.data.database.Database
import com.github.plplmax.notifications.data.database.RealmDatabase
import com.github.plplmax.notifications.data.notification.LocalScheduleNotifications
import com.github.plplmax.notifications.data.notification.ScheduleNotifications
import com.github.plplmax.notifications.data.schedule.ScheduleRepository
import com.github.plplmax.notifications.data.schedule.ScheduleRepositoryImpl
import com.github.plplmax.notifications.data.schedule.local.ScheduleLocalDataSourceImpl
import com.github.plplmax.notifications.data.schedule.remote.ScheduleRemoteDataSourceImpl
import com.github.plplmax.notifications.data.user.UserRepository
import com.github.plplmax.notifications.data.user.UserRepositoryImpl
import com.github.plplmax.notifications.data.user.local.LocalUserDataSourceImpl
import com.github.plplmax.notifications.data.user.remote.RemoteUserDataSourceImpl
import com.github.plplmax.notifications.resources.AppResources
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

    val resources: Resources by lazy { AppResources(context) }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(
            RemoteUserDataSourceImpl(httpClient),
            LocalUserDataSourceImpl(context)
        )
    }

    val scheduleRepository: ScheduleRepository by lazy {
        ScheduleRepositoryImpl(
            ScheduleRemoteDataSourceImpl(httpClient),
            ScheduleLocalDataSourceImpl(database)
        )
    }

    val scheduleNotifications: ScheduleNotifications by lazy {
        LocalScheduleNotifications(database)
    }

    val notificationCentre: NotificationCentre by lazy {
        AppNotificationCentre(context, NotificationManagerCompat.from(context))
    }

    val scheduleNotificationChannel: ScheduleNotificationChannel by lazy {
        ScheduleNotificationChannelOf(context, notificationCentre)
    }

    private val database: Database by lazy { RealmDatabase() }
}
