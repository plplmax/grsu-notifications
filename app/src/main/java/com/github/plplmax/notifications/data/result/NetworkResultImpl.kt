package com.github.plplmax.notifications.data.result

import com.github.plplmax.notifications.data.Errors
import timber.log.Timber
import java.io.IOException
import java.net.UnknownHostException

class NetworkResultImpl<out T>(private val block: suspend () -> T) : NetworkResult<T> {
    override suspend fun result(): Result<T> {
        return try {
            Result.success(block())
        } catch (e: IOException) {
            Timber.e(e)
            val exception = when (e) {
                is UnknownHostException -> Exception(Errors.CHECK_INTERNET_CONNECTION.toString())
                else -> Exception(Errors.GENERIC_ERROR.toString())
            }
            Result.failure(exception)
        }
    }
}