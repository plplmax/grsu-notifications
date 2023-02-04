package com.github.plplmax.grsunotifications.resources

import androidx.annotation.StringRes

interface Resources {
    fun string(@StringRes id: Int): String
}
