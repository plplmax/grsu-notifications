package com.github.plplmax.notifications

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.work.WorkManager
import com.github.plplmax.notifications.ui.login.LoginScreen
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
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen(viewModel)
                }
            }
        }
    }
}
