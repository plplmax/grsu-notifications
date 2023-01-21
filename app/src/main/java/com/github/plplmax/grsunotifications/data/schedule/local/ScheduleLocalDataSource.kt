package com.github.plplmax.grsunotifications.data.schedule.local

interface ScheduleLocalDataSource {
    fun saveScheduleHash(hash: String)
    fun deleteScheduleHash()
    suspend fun scheduleHash(): String
}
