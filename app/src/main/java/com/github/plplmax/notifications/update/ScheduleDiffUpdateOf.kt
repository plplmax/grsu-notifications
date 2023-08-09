package com.github.plplmax.notifications.update

import com.github.plplmax.notifications.computed.ComputedScheduleDiffOf
import com.github.plplmax.notifications.data.schedule.Schedules
import com.github.plplmax.notifications.data.schedule.models.Schedule
import com.github.plplmax.notifications.data.schedule.models.ScheduleDiff
import com.github.plplmax.notifications.data.user.Users
import com.github.plplmax.notifications.time.NightTimeOf
import com.github.plplmax.notifications.window.ScheduleUpdateWindowOf
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ScheduleDiffUpdateOf(
    private val users: Users,
    private val schedules: Schedules
) : ScheduleDiffUpdate {
    override suspend fun diff(): Result<ScheduleDiff> {
        val oldScheduleResult = schedules.schedule()
        val isNightNow = NightTimeOf(
            startInclusive = LocalTime.of(0, 0),
            endExclusive = LocalTime.of(6, 0)
        ).isNight(LocalTime.now())

        if (oldScheduleResult.isNotEmpty() && isNightNow) {
            return Result.success(ScheduleDiff())
        }

        val userId = users.id()
        val updateWindow = ScheduleUpdateWindowOf(LocalDate.now())
        val formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu")
        val newScheduleResult = schedules.onWeek(
            userId = userId,
            startDate = updateWindow.startDateAsString(formatter),
            endDate = updateWindow.endDateAsString(formatter)
        )

        newScheduleResult.onFailure { th -> return Result.failure(th) }

        val oldSchedule = oldScheduleResult.firstOrNull() ?: Schedule(days = listOf())
        val newSchedule = newScheduleResult.getOrThrow()
        val scheduleDiff = ComputedScheduleDiffOf(oldSchedule, newSchedule).value()

        schedules.deleteSchedule()
        schedules.save(newSchedule)

        return Result.success(scheduleDiff)
    }
}
