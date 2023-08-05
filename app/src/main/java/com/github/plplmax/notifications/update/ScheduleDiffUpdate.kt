package com.github.plplmax.notifications.update

import com.github.plplmax.notifications.data.schedule.models.ScheduleDiff

interface ScheduleDiffUpdate {
    suspend fun diff(): Result<ScheduleDiff>
}
