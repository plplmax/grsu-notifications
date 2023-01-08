package com.github.plplmax.grsunotifications.data.result

interface NetworkResult<out T> {
    suspend fun result(): Result<T>
}