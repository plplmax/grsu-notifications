package com.github.plplmax.notifications

import android.app.Application
import android.os.Build
import androidx.work.Configuration
import com.github.plplmax.notifications.channel.ScheduleNotificationChannelOf
import com.github.plplmax.notifications.crashlytics.CrashlyticsTree
import com.github.plplmax.notifications.data.worker.ScheduleWorkerFactory
import com.github.plplmax.notifications.deps.Dependencies
import io.realm.Realm
import io.realm.RealmConfiguration
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
        initRealm()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ScheduleNotificationChannelOf(
                applicationContext,
                deps.notificationCentre
            ).create()
        }
    }

    private fun initRealm() {
        Realm.init(applicationContext)
        val realmConfig =
            RealmConfiguration.Builder()
                .name("appStorage.realm")
                .compactOnLaunch()
                .build()
        Realm.setDefaultConfiguration(realmConfig)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(
            ScheduleWorkerFactory(
                deps.scheduleDiffUpdate,
                deps.scheduleNotifications,
                deps.scheduleNotificationChannel,
                deps.resources
            )
        ).build()
    }
}
