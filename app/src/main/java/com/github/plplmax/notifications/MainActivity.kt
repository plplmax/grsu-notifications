package com.github.plplmax.notifications

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.work.WorkManager
import com.github.plplmax.notifications.ui.MainScreen
import com.github.plplmax.notifications.ui.theme.GrsuNotificationsTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels(factoryProducer = {
        val deps = (application as App).deps
        MainViewModel(
            deps.userRepository,
            deps.scheduleRepository,
            deps.notificationCentre,
            WorkManager.getInstance(applicationContext)
        ).createFactory()
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GrsuNotificationsTheme {
                MainScreen(viewModel)
            }
        }
    }
}
