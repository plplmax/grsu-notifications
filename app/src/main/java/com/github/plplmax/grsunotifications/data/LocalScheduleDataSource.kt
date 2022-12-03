package com.github.plplmax.grsunotifications.data

interface LocalScheduleDataSource {
    fun saveScheduleHash(hash: String)
    fun deleteScheduleHash()
    suspend fun scheduleHash(): String
}
