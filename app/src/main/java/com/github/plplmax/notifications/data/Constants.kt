package com.github.plplmax.notifications.data

import android.os.Build

object Constants {
    const val BASE_URL = "https://api.grsu.by/1.x/app1/"
    const val DONT_KILL_BASE_URL = "https://dontkillmyapp.com"
    val MANUFACTURER = Build.MANUFACTURER.lowercase().replace(oldChar = ' ', newChar = '-')
}
