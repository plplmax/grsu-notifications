package com.github.plplmax.notifications.computed

import com.github.plplmax.notifications.data.schedule.enums.ModificationType
import com.github.plplmax.notifications.data.schedule.models.Day
import com.github.plplmax.notifications.data.schedule.models.Lesson
import com.github.plplmax.notifications.data.schedule.models.Schedule
import com.github.plplmax.notifications.data.schedule.models.ScheduleDiff
import com.github.plplmax.notifications.data.schedule.models.Teacher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ComputedScheduleDiffOfTest {
    private lateinit var firstLesson: Lesson
    private lateinit var secondLesson: Lesson

    @Before
    fun setUp() {
        firstLesson = Lesson(timeStart = "8:30", timeEnd = "9:50", title = "Information Security")
        secondLesson = Lesson(timeStart = "13:30", timeEnd = "14:50", title = "Elegant Objects")
    }

    @Test
    fun diffsEmptySchedules() {
        val old = Schedule(days = listOf())
        val new = Schedule(days = listOf())

        val result = ComputedScheduleDiffOf(old, new).value()

        assertEquals(ScheduleDiff(days = listOf()), result)
    }

    @Test
    fun diffsEqualSchedules() {
        val day = Day("17.04.2023", listOf(firstLesson))
        val old = Schedule(listOf(day))
        val new = Schedule(listOf(day))

        val result = ComputedScheduleDiffOf(old, new).value()

        assertEquals(ScheduleDiff(days = listOf()), result)
    }

    @Test
    fun diffsSchedulesWithAddedDay() {
        val addedDay = Day("24.05.2023", listOf(secondLesson))
        val old = Schedule(listOf())
        val new = Schedule(listOf(addedDay))

        val result = ComputedScheduleDiffOf(old, new).value()

        val expectedLessons = listOf(secondLesson.copy(modificationType = ModificationType.Added))
        val expectedSchedule = ScheduleDiff(
            days = listOf(addedDay.copy(lessons = expectedLessons))
        )
        assertEquals(expectedSchedule, result)
    }

    @Test
    fun diffsSchedulesWithDeletedDay() {
        val deletedDay = Day("08.09.2023", listOf(secondLesson))
        val old = Schedule(listOf(deletedDay))
        val new = Schedule(listOf())

        val result = ComputedScheduleDiffOf(old, new).value()

        val expectedLessons = listOf(secondLesson.copy(modificationType = ModificationType.Deleted))
        val expectedSchedule = ScheduleDiff(
            days = listOf(deletedDay.copy(lessons = expectedLessons))
        )
        assertEquals(expectedSchedule, result)
    }

    @Test
    fun diffsSchedulesWithChangedLessonTitle() {
        val day = Day("07.07.2023", listOf())
        val old = Schedule(listOf(day.copy(lessons = listOf(firstLesson))))
        val changedLesson = firstLesson.copy(title = "Elegant Objects")
        val new = Schedule(listOf(day.copy(lessons = listOf(changedLesson))))

        val result = ComputedScheduleDiffOf(old, new).value()

        val expectedLessons = listOf(
            firstLesson.copy(modificationType = ModificationType.Deleted),
            changedLesson.copy(modificationType = ModificationType.Added)
        )
        val expected = ScheduleDiff(days = listOf(day.copy(lessons = expectedLessons)))
        assertEquals(expected, result)
    }

    @Test
    fun diffsSchedulesWithChangedLesson() {
        val day = Day("07.07.2023", listOf())
        val old = Schedule(listOf(day.copy(lessons = listOf(firstLesson))))
        val changedLesson = firstLesson.copy(
            teacher = Teacher(fullname = "Adam Smith"),
            type = "Exam",
            address = "Ozhesko, 22",
            room = "226",
            fullAddress = "Ozhesko, 22, 226"
        )
        val new = Schedule(listOf(day.copy(lessons = listOf(changedLesson))))

        val result = ComputedScheduleDiffOf(old, new).value()

        val expectedLessons = listOf(
            changedLesson.copy(
                teacher = changedLesson.teacher.copy(fullname = "+ ${changedLesson.teacher.fullname}"),
                type = "+ ${changedLesson.type}",
                address = "+ ${changedLesson.address}",
                room = "+ ${changedLesson.room}",
                fullAddress = "+ ${changedLesson.fullAddress}",
                modificationType = ModificationType.Edited
            )
        )
        val expected = ScheduleDiff(days = listOf(day.copy(lessons = expectedLessons)))
        assertEquals(expected, result)
    }

    @Test
    fun diffsSchedulesWithAddedLesson() {
        val day = Day("15.12.2023", lessons = listOf())
        val old = Schedule(listOf(day.copy(lessons = listOf(firstLesson))))
        val new = Schedule(listOf(day.copy(lessons = listOf(firstLesson, secondLesson))))

        val result = ComputedScheduleDiffOf(old, new).value()

        val expectedLessons = listOf(secondLesson.copy(modificationType = ModificationType.Added))
        val expectedSchedule = ScheduleDiff(days = listOf(day.copy(lessons = expectedLessons)))
        assertEquals(expectedSchedule, result)
    }

    @Test
    fun diffsSchedulesWithDeletedLesson() {
        val day = Day("08.08.2022", lessons = listOf())
        val old = Schedule(listOf(day.copy(lessons = listOf(firstLesson, secondLesson))))
        val new = Schedule(listOf(day.copy(lessons = listOf(firstLesson))))

        val result = ComputedScheduleDiffOf(old, new).value()

        val expectedLessons = listOf(secondLesson.copy(modificationType = ModificationType.Deleted))
        val expectedSchedule = ScheduleDiff(days = listOf(day.copy(lessons = expectedLessons)))
        assertEquals(expectedSchedule, result)
    }
}
