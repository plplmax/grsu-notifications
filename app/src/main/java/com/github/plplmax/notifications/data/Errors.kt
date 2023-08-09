package com.github.plplmax.notifications.data

import java.net.UnknownHostException

enum class Errors {
    CHECK_INTERNET_CONNECTION,
    INVALID_LOGIN,
    GENERIC_ERROR;

    fun toException(): Exception = Exception(toString())

    companion object {
        fun fromExceptionType(th: Throwable): Errors = when (th) {
            is UnknownHostException -> CHECK_INTERNET_CONNECTION
            else -> GENERIC_ERROR
        }
    }
}
