package com.github.sundeepk.compactcalendarview

import com.github.sundeepk.compactcalendarview.WeekUtils.getWeekdayNames
import org.junit.Assert
import org.junit.Test
import java.util.*

class WeekUtilsTest {
    @Test
    fun itShouldReturnCorrectWeekDaysWhenSundayIsFirstDay() {
        val expectedWeekDays = arrayOf("S", "M", "T", "W", "T", "F", "S")
        val actualWeekDays = getWeekdayNames(Locale.ENGLISH, Calendar.SUNDAY, false)
        Assert.assertArrayEquals(expectedWeekDays, actualWeekDays)
    }

    @Test
    fun itShouldReturnCorrectWeekDaysWhenMondayIsFirstDay() {
        val expectedWeekDays = arrayOf("M", "T", "W", "T", "F", "S", "S")
        val actualWeekDays = getWeekdayNames(Locale.ENGLISH, Calendar.MONDAY, false)
        Assert.assertArrayEquals(expectedWeekDays, actualWeekDays)
    }

    @Test
    fun itShouldReturnCorrectWeekDaysWhenTuesdayIsFirstDay() {
        val expectedWeekDays = arrayOf("T", "W", "T", "F", "S", "S", "M")
        val actualWeekDays = getWeekdayNames(Locale.ENGLISH, Calendar.TUESDAY, false)
        Assert.assertArrayEquals(expectedWeekDays, actualWeekDays)
    }

    @Test
    fun itShouldReturnCorrectWeekDaysWhenWednesdayIsFirstDay() {
        val expectedWeekDays = arrayOf("W", "T", "F", "S", "S", "M", "T")
        val actualWeekDays = getWeekdayNames(Locale.ENGLISH, Calendar.WEDNESDAY, false)
        Assert.assertArrayEquals(expectedWeekDays, actualWeekDays)
    }

    @Test
    fun itShouldReturnCorrectWeekDaysWhenThursdayIsFirstDay() {
        val expectedWeekDays = arrayOf("T", "F", "S", "S", "M", "T", "W")
        val actualWeekDays = getWeekdayNames(Locale.ENGLISH, Calendar.THURSDAY, false)
        Assert.assertArrayEquals(expectedWeekDays, actualWeekDays)
    }

    @Test
    fun itShouldReturnCorrectWeekDaysWhenFridayIsFirstDay() {
        val expectedWeekDays = arrayOf("F", "S", "S", "M", "T", "W", "T")
        val actualWeekDays = getWeekdayNames(Locale.ENGLISH, Calendar.FRIDAY, false)
        Assert.assertArrayEquals(expectedWeekDays, actualWeekDays)
    }

    @Test
    fun itShouldReturnCorrectWeekDaysWhenSaturdayIsFirstDay() {
        val expectedWeekDays = arrayOf("S", "S", "M", "T", "W", "T", "F")
        val actualWeekDays = getWeekdayNames(Locale.ENGLISH, Calendar.SATURDAY, false)
        Assert.assertArrayEquals(expectedWeekDays, actualWeekDays)
    }

    @Test
    fun itShouldReturnCorrectWeekDaysWhenSundayIsFirstDayWith3Letters() {
        val expectedWeekDays = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val actualWeekDays = getWeekdayNames(Locale.ENGLISH, Calendar.SUNDAY, true)
        Assert.assertArrayEquals(expectedWeekDays, actualWeekDays)
    }

    @Test
    fun itShouldReturnCorrectWeekDaysWhenMondayIsFirstDayWith3Letters() {
        val expectedWeekDays = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val actualWeekDays = getWeekdayNames(Locale.ENGLISH, Calendar.MONDAY, true)
        Assert.assertArrayEquals(expectedWeekDays, actualWeekDays)
    }

    @Test
    fun itShouldReturnCorrectWeekDaysWhenTuesdayIsFirstDayWith3Letters() {
        val expectedWeekDays = arrayOf("Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon")
        val actualWeekDays = getWeekdayNames(Locale.ENGLISH, Calendar.TUESDAY, true)
        Assert.assertArrayEquals(expectedWeekDays, actualWeekDays)
    }

    @Test
    fun itShouldReturnCorrectWeekDaysWhenWednesdayIsFirstDayWith3Letters() {
        val expectedWeekDays = arrayOf("Wed", "Thu", "Fri", "Sat", "Sun", "Mon", "Tue")
        val actualWeekDays = getWeekdayNames(Locale.ENGLISH, Calendar.WEDNESDAY, true)
        Assert.assertArrayEquals(expectedWeekDays, actualWeekDays)
    }

    @Test
    fun itShouldReturnCorrectWeekDaysWhenThursdayIsFirstDayWith3Letters() {
        val expectedWeekDays = arrayOf("Thu", "Fri", "Sat", "Sun", "Mon", "Tue", "Wed")
        val actualWeekDays = getWeekdayNames(Locale.ENGLISH, Calendar.THURSDAY, true)
        Assert.assertArrayEquals(expectedWeekDays, actualWeekDays)
    }

    @Test
    fun itShouldReturnCorrectWeekDaysWhenFridayIsFirstDayWith3Letters() {
        val expectedWeekDays = arrayOf("Fri", "Sat", "Sun", "Mon", "Tue", "Wed", "Thu")
        val actualWeekDays = getWeekdayNames(Locale.ENGLISH, Calendar.FRIDAY, true)
        Assert.assertArrayEquals(expectedWeekDays, actualWeekDays)
    }

    @Test
    fun itShouldReturnCorrectWeekDaysWhenSaturdayIsFirstDayWith3Letters() {
        val expectedWeekDays = arrayOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")
        val actualWeekDays = getWeekdayNames(Locale.ENGLISH, Calendar.SATURDAY, true)
        Assert.assertArrayEquals(expectedWeekDays, actualWeekDays)
    }
}