package com.github.plplmax.grsunotifications.data

interface LocalScheduleDataSource {
    fun saveScheduleHash(hash: String)
    suspend fun scheduleHash(): String
}
