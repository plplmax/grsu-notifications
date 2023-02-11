package com.github.plplmax.notifications.data.result

interface NetworkResult<out T> {
    suspend fun result(): Result<T>
}