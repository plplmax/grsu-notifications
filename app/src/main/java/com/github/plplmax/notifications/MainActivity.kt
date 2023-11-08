package com.github.plplmax.notifications

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.plplmax.notifications.ui.MainScreen
import com.github.plplmax.notifications.ui.theme.GrsuNotificationsTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {
    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GrsuNotificationsTheme {
                KoinAndroidContext {
                    MainScreen()
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return ev?.pointerCount == 1 && super.dispatchTouchEvent(ev)
    }
}
