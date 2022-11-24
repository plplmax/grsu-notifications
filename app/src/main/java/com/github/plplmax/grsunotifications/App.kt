package com.github.plplmax.grsunotifications

import android.app.Application
import android.app.NotificationManager
import android.os.Build
import androidx.work.Configuration
import com.github.plplmax.grsunotifications.data.workManager.ScheduleWorkerFactory
import com.github.plplmax.grsunotifications.deps.Dependencies
import com.github.plplmax.grsunotifications.notification.ScheduleNotificationChannel

class App : Application(), Configuration.Provider {
    val deps: Dependencies by lazy { Dependencies(applicationContext) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ScheduleNotificationChannel(
                applicationContext,
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            ).create()
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(
            ScheduleWorkerFactory(
                deps.userRepository,
                deps.scheduleRepository,
            )
        ).build()
    }
}
