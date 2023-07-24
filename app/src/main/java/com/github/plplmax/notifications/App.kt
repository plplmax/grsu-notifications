package com.github.plplmax.notifications

import android.app.Application
import android.os.Build
import androidx.work.Configuration
import com.github.plplmax.notifications.data.workManager.ScheduleWorkerFactory
import com.github.plplmax.notifications.deps.Dependencies
import com.github.plplmax.notifications.notification.ScheduleNotificationChannel
import com.github.plplmax.notifications.timber.CrashlyticsTree
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
            ScheduleNotificationChannel.Base(
                applicationContext,
                deps.notificationCentre
            ).create()
        }
    }

    private fun initRealm() {
        Realm.init(applicationContext)
        val realmConfig =
            RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .name("appStorage.realm")
                .compactOnLaunch()
                .build()
        Realm.setDefaultConfiguration(realmConfig)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(
            ScheduleWorkerFactory(
                deps.userRepository,
                deps.scheduleRepository,
                deps.diffedScheduleRepository,
                deps.scheduleNotifications,
                deps.scheduleNotificationChannel,
                deps.resources
            )
        ).build()
    }
}
