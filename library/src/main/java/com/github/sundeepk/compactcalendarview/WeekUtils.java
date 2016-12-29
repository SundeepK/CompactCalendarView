package com.github.sundeepk.compactcalendarview;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

public class WeekUtils {

    static String[] getWeekdayNames(Locale locale, int day, boolean useThreeLetterAbbreviation){
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
        String[] dayNames = dateFormatSymbols.getShortWeekdays();
        if (dayNames == null) {
            throw new IllegalStateException("Unable to determine weekday names from default locale");
        }
        if (dayNames.length != 8) {
            throw new IllegalStateException("Expected weekday names from default locale to be of size 7 but: "
                    + Arrays.toString(dayNames) + " with size " + dayNames.length + " was returned.");
        }

        String[] weekDayNames = new String[7];
        String[] weekDaysFromSunday = {dayNames[1], dayNames[2], dayNames[3], dayNames[4], dayNames[5], dayNames[6], dayNames[7]};
        for (int currentDay = day - 1, i = 0; i <= 6; i++, currentDay++) {
            currentDay = currentDay >= 7 ? 0 : currentDay;
            weekDayNames[i] = weekDaysFromSunday[currentDay];
        }

        if (!useThreeLetterAbbreviation) {
            for (int i = 0; i < weekDayNames.length; i++) {
                weekDayNames[i] = weekDayNames[i].substring(0, 1);
            }
        }

        return weekDayNames;
    }


}
