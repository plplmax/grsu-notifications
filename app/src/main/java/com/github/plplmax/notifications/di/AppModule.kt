package com.github.plplmax.notifications.di

import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import com.github.plplmax.notifications.MainViewModel
import com.github.plplmax.notifications.centre.AppNotificationCentre
import com.github.plplmax.notifications.centre.NotificationCentre
import com.github.plplmax.notifications.channel.ScheduleNotificationChannel
import com.github.plplmax.notifications.channel.ScheduleNotificationChannelOf
import com.github.plplmax.notifications.data.Constants
import com.github.plplmax.notifications.data.database.Database
import com.github.plplmax.notifications.data.database.RealmDatabase
import com.github.plplmax.notifications.data.notification.LocalScheduleNotifications
import com.github.plplmax.notifications.data.notification.LoggedScheduleNotifications
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
import com.github.plplmax.notifications.data.worker.ScheduleWorker
import com.github.plplmax.notifications.resources.AppResources
import com.github.plplmax.notifications.resources.Resources
import com.github.plplmax.notifications.ui.diff.DiffViewModel
import com.github.plplmax.notifications.ui.notification.NotificationViewModel
import com.github.plplmax.notifications.update.ScheduleDiffUpdate
import com.github.plplmax.notifications.update.ScheduleDiffUpdateOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import timber.log.Timber

val appModule: Module = module {
    single {
        HttpClient(Android) {
            defaultRequest {
                url(Constants.BASE_URL)
            }
            install(Logging) {
                level = LogLevel.INFO
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.tag("Ktor").d(message)
                    }
                }
            }
        }
    }
    single { Json { ignoreUnknownKeys = true } }
    single { WorkManager.getInstance(androidContext()) }
    singleOf(::RealmDatabase) bind Database::class
    singleOf(::AppResources) bind Resources::class
    factory {
        LoggedUsers(
            MappedUsers(
                LocalUsers(
                    context = androidContext(),
                    origin = RemoteUsers(client = get()),
                    database = get()
                )
            )
        )
    } bind Users::class
    factory {
        LoggedSchedules(
            MappedSchedules(
                LocalSchedules(
                    context = androidContext(),
                    origin = RemoteSchedules(client = get(), json = get()),
                    database = get()
                )
            )
        )
    } bind Schedules::class
    factory {
        LoggedScheduleNotifications(
            LocalScheduleNotifications(database = get())
        )
    } bind ScheduleNotifications::class
    factory {
        AppNotificationCentre(
            context = androidContext(),
            manager = NotificationManagerCompat.from(androidContext())
        )
    } bind NotificationCentre::class
    factoryOf(::ScheduleDiffUpdateOf) bind ScheduleDiffUpdate::class
    factoryOf(::ScheduleNotificationChannelOf) bind ScheduleNotificationChannel::class
    workerOf(::ScheduleWorker)
    viewModel {
        MainViewModel(
            users = get(),
            schedules = get(),
            notificationCentre = get(),
            workManager = get()
        )
    }
    viewModel { NotificationViewModel(notifications = get()) }
    viewModel { DiffViewModel(scheduleNotifications = get()) }
}
