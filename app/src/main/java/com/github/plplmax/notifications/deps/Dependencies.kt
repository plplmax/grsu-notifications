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
import com.github.plplmax.notifications.data.schedule.LocalSchedules
import com.github.plplmax.notifications.data.schedule.LoggedSchedules
import com.github.plplmax.notifications.data.schedule.MappedSchedules
import com.github.plplmax.notifications.data.schedule.RemoteSchedules
import com.github.plplmax.notifications.data.schedule.Schedules
import com.github.plplmax.notifications.data.user.LocalUsers
import com.github.plplmax.notifications.data.user.LoggedUsers
import com.github.plplmax.notifications.data.user.MappedUsers
import com.github.plplmax.notifications.data.user.RemoteUsers
import com.github.plplmax.notifications.data.user.Users
import com.github.plplmax.notifications.resources.AppResources
import com.github.plplmax.notifications.resources.Resources
import com.github.plplmax.notifications.update.ScheduleDiffUpdate
import com.github.plplmax.notifications.update.ScheduleDiffUpdateOf
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

    val users: Users by lazy {
        LoggedUsers(
            MappedUsers(
                LocalUsers(
                    context,
                    RemoteUsers(httpClient),
                )
            )
        )
    }

    val schedules: Schedules by lazy {
        LoggedSchedules(
            MappedSchedules(
                LocalSchedules(
                    RemoteSchedules(httpClient),
                    database
                )
            )
        )
    }

    val scheduleNotifications: ScheduleNotifications by lazy {
        LocalScheduleNotifications(database)
    }

    val scheduleDiffUpdate: ScheduleDiffUpdate by lazy {
        ScheduleDiffUpdateOf(users, schedules)
    }

    val notificationCentre: NotificationCentre by lazy {
        AppNotificationCentre(context, NotificationManagerCompat.from(context))
    }

    val scheduleNotificationChannel: ScheduleNotificationChannel by lazy {
        ScheduleNotificationChannelOf(context, notificationCentre)
    }

    private val database: Database by lazy { RealmDatabase() }
}
