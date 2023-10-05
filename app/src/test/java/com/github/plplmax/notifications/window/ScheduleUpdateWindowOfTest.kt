package com.github.plplmax.notifications.window

import com.github.plplmax.notifications.data.schedule.models.Day
import com.github.plplmax.notifications.data.schedule.models.Lesson
import com.github.plplmax.notifications.data.schedule.models.Schedule
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class ScheduleUpdateWindowOfTest {
    private lateinit var scheduleWindow: ScheduleUpdateWindowOf
    private lateinit var firstLesson: Lesson
    private lateinit var secondLesson: Lesson
    private lateinit var today: LocalDate

    @Before
    fun setUp() {
        today = LocalDate.now()
        scheduleWindow = ScheduleUpdateWindowOf(today)
        firstLesson = Lesson(title = "Elegant Objects")
        secondLesson = Lesson(title = "Information security")
    }

    @Test
    fun normalizesOldScheduleWithoutShift() {
        val old = Schedule(listOf(Day(today.toString(), listOf(firstLesson))))
        val new = Schedule(listOf(Day(today.toString(), listOf(secondLesson))))

        val result = scheduleWindow.normalizedOldSchedule(old, new, today)

        assertEquals(old, result)
    }

    @Test
    fun normalizesOldScheduleWithOneDayShift() {
        val yesterday = today.minusDays(1)
        val windowEnd = today.plusDays(scheduleWindow.durationInDays - 1)
        val old = Schedule(listOf(Day(yesterday.toString(), listOf(firstLesson))))
        val new = Schedule(listOf(Day(windowEnd.toString(), listOf(secondLesson))))

        val result = scheduleWindow.normalizedOldSchedule(old, new, yesterday)

        assertEquals(new, result)
    }

    @Test
    fun normalizesOldScheduleWithLongShift() {
        val lastUpdate = today.minusDays(scheduleWindow.durationInDays)
        val old = Schedule(listOf(Day(lastUpdate.toString(), listOf(firstLesson))))
        val new = Schedule(listOf(Day(today.toString(), listOf(secondLesson))))

        val result = scheduleWindow.normalizedOldSchedule(old, new, lastUpdate)

        assertEquals(new, result)
    }

    @Test
    fun normalizesOldScheduleWithThreeDaysShift() {
        val lastUpdate = today.minusDays(3)
        val windowEnd = today.plusDays(scheduleWindow.durationInDays - 1)
        val old = Schedule(listOf(Day(lastUpdate.toString(), listOf(firstLesson))))
        val new = Schedule(
            listOf(
                Day(windowEnd.minusDays(2).toString(), listOf(firstLesson)),
                Day(windowEnd.minusDays(1).toString(), listOf(firstLesson)),
                Day(windowEnd.toString(), listOf(firstLesson))
            )
        )

        val result = scheduleWindow.normalizedOldSchedule(old, new, lastUpdate)

        assertEquals(new, result)
    }
}
