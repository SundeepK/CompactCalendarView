package com.github.sundeepk.compactcalendarview;

import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.assertArrayEquals;

public class WeekUtilsTest {

    @Test
    public void itShouldReturnCorrectWeekDaysWhenSundayIsFirstDay(){
        String[] expectedWeekDays = {"S", "M", "T", "W", "T", "F", "S"};
        String[] actualWeekDays = WeekUtils.getWeekdayNames(Locale.ENGLISH, Calendar.SUNDAY, false);
        assertArrayEquals(expectedWeekDays, actualWeekDays);
    }

    @Test
    public void itShouldReturnCorrectWeekDaysWhenMondayIsFirstDay(){
        String[] expectedWeekDays = {"M", "T", "W", "T", "F", "S", "S"};
        String[] actualWeekDays = WeekUtils.getWeekdayNames(Locale.ENGLISH, Calendar.MONDAY, false);
        assertArrayEquals(expectedWeekDays, actualWeekDays);
    }

    @Test
    public void itShouldReturnCorrectWeekDaysWhenTuesdayIsFirstDay(){
        String[] expectedWeekDays = {"T", "W", "T", "F", "S", "S", "M"};
        String[] actualWeekDays = WeekUtils.getWeekdayNames(Locale.ENGLISH, Calendar.TUESDAY, false);
        assertArrayEquals(expectedWeekDays, actualWeekDays);
    }

    @Test
    public void itShouldReturnCorrectWeekDaysWhenWednesdayIsFirstDay(){
        String[] expectedWeekDays = {"W", "T", "F", "S", "S", "M", "T"};
        String[] actualWeekDays = WeekUtils.getWeekdayNames(Locale.ENGLISH, Calendar.WEDNESDAY, false);
        assertArrayEquals(expectedWeekDays, actualWeekDays);
    }

    @Test
    public void itShouldReturnCorrectWeekDaysWhenThursdayIsFirstDay(){
        String[] expectedWeekDays = {"T", "F", "S", "S", "M", "T", "W"};
        String[] actualWeekDays = WeekUtils.getWeekdayNames(Locale.ENGLISH, Calendar.THURSDAY, false);
        assertArrayEquals(expectedWeekDays, actualWeekDays);
    }

    @Test
    public void itShouldReturnCorrectWeekDaysWhenFridayIsFirstDay(){
        String[] expectedWeekDays = {"F", "S", "S", "M", "T", "W", "T"};
        String[] actualWeekDays = WeekUtils.getWeekdayNames(Locale.ENGLISH, Calendar.FRIDAY, false);
        assertArrayEquals(expectedWeekDays, actualWeekDays);
    }

    @Test
    public void itShouldReturnCorrectWeekDaysWhenSaturdayIsFirstDay(){
        String[] expectedWeekDays = {"S", "S", "M", "T", "W", "T", "F"};
        String[] actualWeekDays = WeekUtils.getWeekdayNames(Locale.ENGLISH, Calendar.SATURDAY, false);
        assertArrayEquals(expectedWeekDays, actualWeekDays);
    }

    @Test
    public void itShouldReturnCorrectWeekDaysWhenSundayIsFirstDayWith3Letters(){
        String[] expectedWeekDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        String[] actualWeekDays = WeekUtils.getWeekdayNames(Locale.ENGLISH, Calendar.SUNDAY, true);
        assertArrayEquals(expectedWeekDays, actualWeekDays);
    }

    @Test
    public void itShouldReturnCorrectWeekDaysWhenMondayIsFirstDayWith3Letters(){
        String[] expectedWeekDays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        String[] actualWeekDays = WeekUtils.getWeekdayNames(Locale.ENGLISH, Calendar.MONDAY, true);
        assertArrayEquals(expectedWeekDays, actualWeekDays);
    }

    @Test
    public void itShouldReturnCorrectWeekDaysWhenTuesdayIsFirstDayWith3Letters(){
        String[] expectedWeekDays = {"Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon"};
        String[] actualWeekDays = WeekUtils.getWeekdayNames(Locale.ENGLISH, Calendar.TUESDAY, true);
        assertArrayEquals(expectedWeekDays, actualWeekDays);
    }

    @Test
    public void itShouldReturnCorrectWeekDaysWhenWednesdayIsFirstDayWith3Letters(){
        String[] expectedWeekDays = {"Wed", "Thu", "Fri", "Sat", "Sun", "Mon", "Tue"};
        String[] actualWeekDays = WeekUtils.getWeekdayNames(Locale.ENGLISH, Calendar.WEDNESDAY, true);
        assertArrayEquals(expectedWeekDays, actualWeekDays);
    }

    @Test
    public void itShouldReturnCorrectWeekDaysWhenThursdayIsFirstDayWith3Letters(){
        String[] expectedWeekDays = {"Thu", "Fri", "Sat", "Sun", "Mon", "Tue", "Wed"};
        String[] actualWeekDays = WeekUtils.getWeekdayNames(Locale.ENGLISH, Calendar.THURSDAY, true);
        assertArrayEquals(expectedWeekDays, actualWeekDays);
    }

    @Test
    public void itShouldReturnCorrectWeekDaysWhenFridayIsFirstDayWith3Letters(){
        String[] expectedWeekDays = {"Fri", "Sat", "Sun", "Mon", "Tue", "Wed", "Thu"};
        String[] actualWeekDays = WeekUtils.getWeekdayNames(Locale.ENGLISH, Calendar.FRIDAY, true);
        assertArrayEquals(expectedWeekDays, actualWeekDays);
    }

    @Test
    public void itShouldReturnCorrectWeekDaysWhenSaturdayIsFirstDayWith3Letters(){
        String[] expectedWeekDays = {"Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri"};
        String[] actualWeekDays = WeekUtils.getWeekdayNames(Locale.ENGLISH, Calendar.SATURDAY, true);
        assertArrayEquals(expectedWeekDays, actualWeekDays);
    }

}