package com.github.sundeepk.compactcalendarview;

import android.graphics.Color;

import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CompactCalendarHelper {

    public static List<Event> getOneEventPerDayForMonth(int start, int days, long timeStamp) {
        return getOneEventPerDayForMonth(start, days, timeStamp, Color.BLUE);
    }

    //generate one event per a day for a month
    public static List<Event> getOneEventPerDayForMonth(int start, int days, long timeStamp, int color) {
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
        List<Event> events = new ArrayList<>();
        for(int i = start; i < days; i++){
            setDateTime(timeStamp, currentCalendar, i);
            events.add(new Event(color, currentCalendar.getTimeInMillis()));
        }
        return events;
    }

    public static List<Events> getEvents(int start, int days, long timeStamp) {
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
        List<Events> events = new ArrayList<>();
        for(int i = start; i < days; i++){
            setDateTime(timeStamp, currentCalendar, i);
            List<Event> eventList = new ArrayList<>();
            eventList.add(new Event(Color.BLUE, currentCalendar.getTimeInMillis()));
            Events eventsObject = new Events(currentCalendar.getTimeInMillis(), eventList);
            events.add(eventsObject);
        }
        return events;
    }


    public static List<Events> getDayEventWith2EventsPerDay(int start, int days, long timeStamp) {
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
        List<Events> events = new ArrayList<>();
        for(int i = start; i < days; i++){
            setDateTime(timeStamp, currentCalendar, i);
            List<Event> eventList = new ArrayList<>();
            eventList.add(new Event(Color.BLUE, currentCalendar.getTimeInMillis()));
            eventList.add(new Event(Color.RED, currentCalendar.getTimeInMillis() + 3600 * 1000));
            Events eventsObject = new Events(currentCalendar.getTimeInMillis(), eventList);
            events.add(eventsObject);
        }
        return events;
    }

    public static List<Events> getDayEventWithMultipleEventsPerDay(int start, int days, long timeStamp) {
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
        List<Events> events = new ArrayList<>();
        for(int i = start; i < days; i++){
            setDateTime(timeStamp, currentCalendar, i);
            List<Event> eventsList = Arrays.asList(new Event(Color.BLUE, currentCalendar.getTimeInMillis()),
                    new Event(Color.RED, currentCalendar.getTimeInMillis() + 3600 * 1000),
                    new Event(Color.RED, currentCalendar.getTimeInMillis() + (3600 * 2) * 1000),
                    new Event(Color.RED, currentCalendar.getTimeInMillis() + (3600 * 3) * 1000));
            Events eventsObject = new Events(currentCalendar.getTimeInMillis(), eventsList);
            events.add(eventsObject);
        }
        return events;
    }

    public static Map<Long, List<Event>> getMultipleEventsForEachDayAsMap(int start, int days, long timeStamp) {
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());
        Map<Long, List<Event>> epochMillisToEvents = new HashMap<>();
        for(int i = start; i < days; i++){
            setDateTime(timeStamp, currentCalendar, i);
            List<Event> eventList = new ArrayList<>();
            List<Event> events = Arrays.asList(new Event(Color.BLUE, currentCalendar.getTimeInMillis()),
                    new Event(Color.RED, currentCalendar.getTimeInMillis() + 3600 * 1000),
                    new Event(Color.RED, currentCalendar.getTimeInMillis() + (3600 * 2) * 1000),
                    new Event(Color.RED, currentCalendar.getTimeInMillis() + (3600 * 3) * 1000));
            eventList.addAll(events);
            epochMillisToEvents.put(currentCalendar.getTimeInMillis(), eventList);
        }
        return epochMillisToEvents;
    }

    public static void setDateTime(long timeStamp, Calendar currentCalendar, int i) {
        currentCalendar.setTimeInMillis(timeStamp);
        currentCalendar.set(Calendar.DATE, 1);
        currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
        currentCalendar.set(Calendar.MINUTE, 0);
        currentCalendar.set(Calendar.SECOND, 0);
        currentCalendar.set(Calendar.MILLISECOND, 0);
        currentCalendar.add(Calendar.DATE, i);
    }

    public static long setTimeToMidnightAndGet(Calendar cal, long epoch) {
        cal.setTime(new Date(epoch));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    
}
