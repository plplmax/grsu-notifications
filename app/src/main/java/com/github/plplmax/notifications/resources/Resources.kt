package com.github.plplmax.notifications.resources

import androidx.annotation.StringRes

interface Resources {
    fun string(@StringRes id: Int): String
}
