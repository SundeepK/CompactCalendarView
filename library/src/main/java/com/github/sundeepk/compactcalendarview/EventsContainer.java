package com.github.sundeepk.compactcalendarview;

import com.github.sundeepk.compactcalendarview.comparators.EventComparator;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EventsContainer {

    private Map<String, List<Events>> eventsByMonthAndYearMap = new HashMap<>();
    private Map<String, List<Events>> eventsByMonthAndYearMapForDateRange = new HashMap<>();

    private EventComparator eventsComparator = new EventComparator();
    private Calendar eventsCalendar;

    public EventsContainer(Calendar eventsCalendar) {
        this.eventsCalendar = eventsCalendar;
    }

    void addEvent(Event event) {
        eventsCalendar.setTimeInMillis(event.getTimeInMillis());
        String key = getKeyForCalendarEvent(eventsCalendar);
        List<Events> eventsForMonth = eventsByMonthAndYearMap.get(key);
        if (eventsForMonth == null) {
            eventsForMonth = new ArrayList<>();
        }
        Events eventsForTargetDay = getEventDayEvent(event.getTimeInMillis());
        if (eventsForTargetDay == null) {
            List<Event> events = new ArrayList<>();
            events.add(event);
            eventsForMonth.add(new Events(event.getTimeInMillis(), events));
        } else {
            eventsForTargetDay.getEvents().add(event);
        }
        eventsByMonthAndYearMap.put(key, eventsForMonth);
    }

    void addEventForDateRange(Event event) {
        eventsCalendar.setTimeInMillis(event.getTimeInMillis());
        String key = getKeyForCalendarEventForDateRange(eventsCalendar);
        List<Events> eventsForMonth = eventsByMonthAndYearMapForDateRange.get(key);
        if (eventsForMonth == null) {
            eventsForMonth = new ArrayList<>();
        }
        Events eventsForTargetDay = getEventDayEventForDateRange(event.getTimeInMillis());
        if (eventsForTargetDay == null) {
            List<Event> events = new ArrayList<>();
            events.add(event);
            eventsForMonth.add(new Events(event.getTimeInMillis(), events));
        } else {
            eventsForTargetDay.getEvents().add(event);
        }
        eventsByMonthAndYearMapForDateRange.put(key, eventsForMonth);
    }

    void removeAllEvents() {
        eventsByMonthAndYearMap.clear();
    }

    void removeAllEventsForDateRange() {
        eventsByMonthAndYearMapForDateRange.clear();
    }

    void addEvents(List<Event> events) {
        int count = events.size();
        for (int i = 0; i < count; i++) {
            addEvent(events.get(i));
        }
    }

    void addEventsForDateRange(List<Event> events) {
        int count = events.size();
        for (int i = 0; i < count; i++) {
            addEventForDateRange(events.get(i));
        }
    }

    List<Event> getEventsFor(long epochMillis) {
        Events events = getEventDayEvent(epochMillis);
        if (events == null) {
            return new ArrayList<>();
        } else {
            return events.getEvents();
        }
    }

    List<Event> getEventsForForDateRange(long epochMillis) {
        Events events = getEventDayEventForDateRange(epochMillis);
        if (events == null) {
            return new ArrayList<>();
        } else {
            return events.getEvents();
        }
    }

    List<Events> getEventsForMonthAndYear(int month, int year){
        return eventsByMonthAndYearMap.get(year + "_" + month);
    }

    List<Events> getEventsForMonthAndYearForDateRange(int month, int year){
        return eventsByMonthAndYearMapForDateRange.get(year + "_" + month);
    }

    List<Event> getEventsForMonth(long eventTimeInMillis){
        eventsCalendar.setTimeInMillis(eventTimeInMillis);
        String keyForCalendarEvent = getKeyForCalendarEvent(eventsCalendar);
        List<Events> events = eventsByMonthAndYearMap.get(keyForCalendarEvent);
        List<Event> allEventsForMonth = new ArrayList<>();
        if (events != null) {
            for(Events eve : events){
                if (eve != null) {
                    allEventsForMonth.addAll(eve.getEvents());
                }
            }
        }
        Collections.sort(allEventsForMonth, eventsComparator);
        return allEventsForMonth;
    }

    List<Event> getEventsForMonthForDateRange(long eventTimeInMillis){
        eventsCalendar.setTimeInMillis(eventTimeInMillis);
        String keyForCalendarEvent = getKeyForCalendarEventForDateRange(eventsCalendar);
        List<Events> events = eventsByMonthAndYearMapForDateRange.get(keyForCalendarEvent);
        List<Event> allEventsForMonth = new ArrayList<>();
        if (events != null) {
            for(Events eve : events){
                if (eve != null) {
                    allEventsForMonth.addAll(eve.getEvents());
                }
            }
        }
        Collections.sort(allEventsForMonth, eventsComparator);
        return allEventsForMonth;
    }


    private Events getEventDayEvent(long eventTimeInMillis){
        eventsCalendar.setTimeInMillis(eventTimeInMillis);
        int dayInMonth = eventsCalendar.get(Calendar.DAY_OF_MONTH);
        String keyForCalendarEvent = getKeyForCalendarEvent(eventsCalendar);
        List<Events> eventsForMonthsAndYear = eventsByMonthAndYearMap.get(keyForCalendarEvent);
        if (eventsForMonthsAndYear != null) {
            for (Events events : eventsForMonthsAndYear) {
                eventsCalendar.setTimeInMillis(events.getTimeInMillis());
                int dayInMonthFromCache = eventsCalendar.get(Calendar.DAY_OF_MONTH);
                if (dayInMonthFromCache == dayInMonth) {
                    return events;
                }
            }
        }
        return null;
    }

    private Events getEventDayEventForDateRange(long eventTimeInMillis){
        eventsCalendar.setTimeInMillis(eventTimeInMillis);
        int dayInMonth = eventsCalendar.get(Calendar.DAY_OF_MONTH);
        String keyForCalendarEvent = getKeyForCalendarEventForDateRange(eventsCalendar);
        List<Events> eventsForMonthsAndYear = eventsByMonthAndYearMapForDateRange.get(keyForCalendarEvent);
        if (eventsForMonthsAndYear != null) {
            for (Events events : eventsForMonthsAndYear) {
                eventsCalendar.setTimeInMillis(events.getTimeInMillis());
                int dayInMonthFromCache = eventsCalendar.get(Calendar.DAY_OF_MONTH);
                if (dayInMonthFromCache == dayInMonth) {
                    return events;
                }
            }
        }
        return null;
    }

    void removeEventByEpochMillis(long epochMillis) {
        eventsCalendar.setTimeInMillis(epochMillis);
        int dayInMonth = eventsCalendar.get(Calendar.DAY_OF_MONTH);
        String key = getKeyForCalendarEvent(eventsCalendar);
        List<Events> eventsForMonthAndYear = eventsByMonthAndYearMap.get(key);
        if (eventsForMonthAndYear != null) {
            Iterator<Events> calendarDayEventIterator = eventsForMonthAndYear.iterator();
            while (calendarDayEventIterator.hasNext()) {
                Events next = calendarDayEventIterator.next();
                eventsCalendar.setTimeInMillis(next.getTimeInMillis());
                int dayInMonthFromCache = eventsCalendar.get(Calendar.DAY_OF_MONTH);
                if (dayInMonthFromCache == dayInMonth) {
                    calendarDayEventIterator.remove();
                    return;
                }
            }
        }
    }

    void removeEventByEpochMillisForDateRange(long epochMillis) {
        eventsCalendar.setTimeInMillis(epochMillis);
        int dayInMonth = eventsCalendar.get(Calendar.DAY_OF_MONTH);
        String key = getKeyForCalendarEventForDateRange(eventsCalendar);
        List<Events> eventsForMonthAndYear = eventsByMonthAndYearMapForDateRange.get(key);
        if (eventsForMonthAndYear != null) {
            Iterator<Events> calendarDayEventIterator = eventsForMonthAndYear.iterator();
            while (calendarDayEventIterator.hasNext()) {
                Events next = calendarDayEventIterator.next();
                eventsCalendar.setTimeInMillis(next.getTimeInMillis());
                int dayInMonthFromCache = eventsCalendar.get(Calendar.DAY_OF_MONTH);
                if (dayInMonthFromCache == dayInMonth) {
                    calendarDayEventIterator.remove();
                    return;
                }
            }
        }
    }

    void removeEvent(Event event) {
        eventsCalendar.setTimeInMillis(event.getTimeInMillis());
        String key = getKeyForCalendarEvent(eventsCalendar);
        List<Events> eventsForMonthAndYear = eventsByMonthAndYearMap.get(key);
        if (eventsForMonthAndYear != null) {
            for(Events events : eventsForMonthAndYear){
                int indexOfEvent = events.getEvents().indexOf(event);
                if (indexOfEvent >= 0) {
                    events.getEvents().remove(indexOfEvent);
                    return;
                }
            }
        }
    }

    void removeEventForDateRange(Event event) {
        eventsCalendar.setTimeInMillis(event.getTimeInMillis());
        String key = getKeyForCalendarEventForDateRange(eventsCalendar);
        List<Events> eventsForMonthAndYear = eventsByMonthAndYearMapForDateRange.get(key);
        if (eventsForMonthAndYear != null) {
            for(Events events : eventsForMonthAndYear){
                int indexOfEvent = events.getEvents().indexOf(event);
                if (indexOfEvent >= 0) {
                    events.getEvents().remove(indexOfEvent);
                    return;
                }
            }
        }
    }

    void removeEvents(List<Event> events) {
        int count = events.size();
        for (int i = 0; i < count; i++) {
            removeEvent(events.get(i));
        }
    }

    void removeEventsForDateRange(List<Event> events) {
        int count = events.size();
        for (int i = 0; i < count; i++) {
            removeEventForDateRange(events.get(i));
        }
    }

    //E.g. 4 2016 becomes 2016_4
    private String getKeyForCalendarEvent(Calendar cal) {
        return cal.get(Calendar.YEAR) + "_" + cal.get(Calendar.MONTH);
    }

    private String getKeyForCalendarEventForDateRange(Calendar cal) {
        return cal.get(Calendar.YEAR) + "_" + cal.get(Calendar.MONTH);
    }

}
