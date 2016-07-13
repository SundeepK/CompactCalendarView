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

    public static List<Event> getSingleEvents(int start, int days, long timeStamp) {
        return getSingleEvents(start, days, timeStamp, Color.BLUE);
    }

    //generate one event per a day for a month
    public static List<Event> getSingleEvents(int start, int days, long timeStamp, int color) {
        Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
        List<Event> events = new ArrayList<>();
        for(int i = start; i < days; i++){
            setDateTime(timeStamp, currentCalender, i);
            events.add(new Event(color, currentCalender.getTimeInMillis()));
        }
        return events;
    }

    public static List<Events> getEvents(int start, int days, long timeStamp) {
        Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
        List<Events> events = new ArrayList<>();
        for(int i = start; i < days; i++){
            setDateTime(timeStamp, currentCalender, i);
            List<Event> eventList = new ArrayList<>();
            eventList.add(new Event(Color.BLUE, currentCalender.getTimeInMillis()));
            Events eventsObject = new Events(currentCalender.getTimeInMillis(), eventList);
            events.add(eventsObject);
        }
        return events;
    }


    public static List<Events> getDayEventWith2EventsPerDay(int start, int days, long timeStamp) {
        Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
        List<Events> events = new ArrayList<>();
        for(int i = start; i < days; i++){
            setDateTime(timeStamp, currentCalender, i);
            List<Event> eventList = new ArrayList<>();
            eventList.add(new Event(Color.BLUE, currentCalender.getTimeInMillis()));
            eventList.add(new Event(Color.RED, currentCalender.getTimeInMillis() + 3600 * 1000));
            Events eventsObject = new Events(currentCalender.getTimeInMillis(), eventList);
            events.add(eventsObject);
        }
        return events;
    }

    public static List<Events> getDayEventWithMultipleEventsPerDay(int start, int days, long timeStamp) {
        Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
        List<Events> events = new ArrayList<>();
        for(int i = start; i < days; i++){
            setDateTime(timeStamp, currentCalender, i);
            List<Event> eventsList = Arrays.asList(new Event(Color.BLUE, currentCalender.getTimeInMillis()),
                    new Event(Color.RED, currentCalender.getTimeInMillis() + 3600 * 1000),
                    new Event(Color.RED, currentCalender.getTimeInMillis() + (3600 * 2) * 1000),
                    new Event(Color.RED, currentCalender.getTimeInMillis() + (3600 * 3) * 1000));
            Events eventsObject = new Events(currentCalender.getTimeInMillis(), eventsList);
            events.add(eventsObject);
        }
        return events;
    }

    public static Map<Long, List<Event>> getMultipleEventsForEachDayAsMap(int start, int days, long timeStamp) {
        Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
        Map<Long, List<Event>> epochMillisToEvents = new HashMap<>();
        for(int i = start; i < days; i++){
            setDateTime(timeStamp, currentCalender, i);
            List<Event> eventList = new ArrayList<>();
            List<Event> events = Arrays.asList(new Event(Color.BLUE, currentCalender.getTimeInMillis()),
                    new Event(Color.RED, currentCalender.getTimeInMillis() + 3600 * 1000),
                    new Event(Color.RED, currentCalender.getTimeInMillis() + (3600 * 2) * 1000),
                    new Event(Color.RED, currentCalender.getTimeInMillis() + (3600 * 3) * 1000));
            eventList.addAll(events);
            epochMillisToEvents.put(currentCalender.getTimeInMillis(), eventList);
        }
        return epochMillisToEvents;
    }

    public static void setDateTime(long timeStamp, Calendar currentCalender, int i) {
        currentCalender.setTimeInMillis(timeStamp);
        currentCalender.set(Calendar.DATE, 1);
        currentCalender.set(Calendar.HOUR_OF_DAY, 0);
        currentCalender.set(Calendar.MINUTE, 0);
        currentCalender.set(Calendar.SECOND, 0);
        currentCalender.set(Calendar.MILLISECOND, 0);
        currentCalender.add(Calendar.DATE, i);
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
