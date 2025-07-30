package com.example.startup_app.utils

import com.example.startup_app.viewmodels.WeekDay
import com.example.startup_app.viewmodels.WorkingTimeModel
import java.text.SimpleDateFormat
import java.util.*

fun isBookingValidForDoctor(bookingDate: Date, workingTime: WorkingTimeModel): Boolean {
    val calendar = Calendar.getInstance()
    calendar.time = bookingDate

    val dayOfWeekIndex = calendar.get(Calendar.DAY_OF_WEEK)
    val weekDays = listOf(
        WeekDay.Sunday, WeekDay.Monday, WeekDay.Tuesday, WeekDay.Wednesday,
        WeekDay.Thursday, WeekDay.Friday, WeekDay.Saturday
    )
    val bookingWeekDay = weekDays[dayOfWeekIndex - 1]

    val startDayIndex = workingTime.startDay.ordinal
    val endDayIndex = workingTime.endDay.ordinal
    val bookingDayIndex = bookingWeekDay.ordinal

    val isDayValid = if (startDayIndex <= endDayIndex) {
        bookingDayIndex in startDayIndex..endDayIndex
    } else {
        bookingDayIndex >= startDayIndex || bookingDayIndex <= endDayIndex
    }

    if (!isDayValid) return false

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val bookingTimeStr = timeFormat.format(bookingDate)

    val bookingTime = timeFormat.parse(bookingTimeStr)!!
    val startTime = timeFormat.parse(workingTime.startTime)!!
    val endTime = timeFormat.parse(workingTime.endTime)!!

    return bookingTime in startTime..endTime
}