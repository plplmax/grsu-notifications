package com.github.plplmax.notifications.data.schedule.local

interface ScheduleLocalDataSource {
    fun saveScheduleHash(hash: String)
    fun deleteScheduleHash()
    suspend fun scheduleHash(): String
}
