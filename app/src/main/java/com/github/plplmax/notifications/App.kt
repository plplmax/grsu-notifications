package com.github.plplmax.notifications

import android.app.Application
import android.os.Build
import androidx.work.Configuration
import com.github.plplmax.notifications.data.workManager.ScheduleWorkerFactory
import com.github.plplmax.notifications.deps.Dependencies
import com.github.plplmax.notifications.notification.ScheduleNotificationChannel
import com.github.plplmax.notifications.timber.CrashlyticsTree
import timber.log.Timber

class App : Application(), Configuration.Provider {
    val deps: Dependencies by lazy { Dependencies(applicationContext) }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ScheduleNotificationChannel(
                applicationContext,
                deps.notificationCentre
            ).create()
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(
            ScheduleWorkerFactory(
                deps.userRepository,
                deps.scheduleRepository,
                deps.scheduleNotificationChannel,
                deps.resources
            )
        ).build()
    }
}
