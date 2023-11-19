package com.github.plplmax.notifications.data

import java.net.UnknownHostException

enum class Errors {
    CHECK_INTERNET_CONNECTION,
    INVALID_LOGIN,
    GENERIC_ERROR;

    companion object {
        fun from(th: Throwable): Throwable = when (th) {
            is UnknownHostException -> Exception(CHECK_INTERNET_CONNECTION.toString(), th)
            else -> Exception(GENERIC_ERROR.toString(), th)
        }
    }
}
