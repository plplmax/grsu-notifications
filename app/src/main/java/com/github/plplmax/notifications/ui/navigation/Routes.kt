package com.github.plplmax.notifications.ui.navigation

import androidx.annotation.StringRes
import com.github.plplmax.notifications.R

sealed class Routes(val route: String, @StringRes val title: Int) {
    object Welcome : Routes("welcome", R.string.welcome)
    object Login : Routes("login", R.string.login)
    object Notifications : Routes("notifications", R.string.notifications)
    object Diff : Routes("diff/{id}", R.string.diff)
    object Undefined : Routes("", android.R.string.unknownName)
}
