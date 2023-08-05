package com.github.plplmax.notifications.update

import com.github.plplmax.notifications.computed.ComputedScheduleDiffOf
import com.github.plplmax.notifications.data.schedule.ScheduleRepository
import com.github.plplmax.notifications.data.schedule.models.Schedule
import com.github.plplmax.notifications.data.schedule.models.ScheduleDiff
import com.github.plplmax.notifications.data.user.UserRepository
import com.github.plplmax.notifications.time.NightTimeOf
import com.github.plplmax.notifications.window.ScheduleUpdateWindowOf
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ScheduleDiffUpdateOf(
    private val userRepository: UserRepository,
    private val scheduleRepository: ScheduleRepository
) : ScheduleDiffUpdate {
    override suspend fun diff(): Result<ScheduleDiff> {
        val oldScheduleResult = scheduleRepository.schedule()
        val isNightNow = NightTimeOf(
            startInclusive = LocalTime.of(0, 0),
            endExclusive = LocalTime.of(6, 0)
        ).isNight(LocalTime.now())

        if (oldScheduleResult.isNotEmpty() && isNightNow) {
            return Result.success(ScheduleDiff())
        }

        val userId = userRepository.id()
        val updateWindow = ScheduleUpdateWindowOf(LocalDate.now())
        val formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu")
        val newScheduleResult = scheduleRepository.onWeek(
            userId = userId,
            startDate = updateWindow.startDateAsString(formatter),
            endDate = updateWindow.endDateAsString(formatter)
        )

        newScheduleResult.onFailure { th -> return Result.failure(th) }

        val oldSchedule = oldScheduleResult.firstOrNull() ?: Schedule(days = listOf())
        val newSchedule = newScheduleResult.getOrThrow()
        val scheduleDiff = ComputedScheduleDiffOf(oldSchedule, newSchedule).value()

        scheduleRepository.deleteSchedule()
        scheduleRepository.save(newSchedule)

        return Result.success(scheduleDiff)
    }
}
