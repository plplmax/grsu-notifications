package com.github.plplmax.notifications

import android.app.Application
import android.os.Build
import com.github.plplmax.notifications.channel.ScheduleNotificationChannel
import com.github.plplmax.notifications.crashlytics.CrashlyticsTree
import com.github.plplmax.notifications.di.appModule
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(applicationContext)
            modules(appModule)
            workManagerFactory()
        }
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
            get<ScheduleNotificationChannel>().create()
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
}
