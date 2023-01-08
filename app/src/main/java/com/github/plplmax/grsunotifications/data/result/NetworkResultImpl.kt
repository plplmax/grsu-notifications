package com.github.plplmax.grsunotifications.data.result

import com.github.plplmax.grsunotifications.data.Errors
import java.net.UnknownHostException

class NetworkResultImpl<out T>(private val block: suspend () -> T) : NetworkResult<T> {
    override suspend fun result(): Result<T> {
        return kotlin.runCatching {
            try {
                block()
            } catch (e: Exception) {
                when (e) {
                    is UnknownHostException -> error(Errors.CHECK_INTERNET_CONNECTION)
                    else -> error(Errors.GENERIC_ERROR)
                }
            }
        }
    }
}