package com.github.sundeepk.compactcalendarview

import com.github.sundeepk.compactcalendarview.comparators.EventComparator
import com.github.sundeepk.compactcalendarview.domain.Event
import java.util.*

class EventsContainer(private val eventsCalendar: Calendar) {
    private val eventsByMonthAndYearMap: MutableMap<String, MutableList<Events>> = HashMap()
    private val eventsComparator: Comparator<Event> = EventComparator()
    fun addEvent(event: Event) {
        eventsCalendar.timeInMillis = event.timeInMillis
        val key = getKeyForCalendarEvent(eventsCalendar)
        var eventsForMonth = eventsByMonthAndYearMap[key]
        if (eventsForMonth == null) {
            eventsForMonth = ArrayList()
        }
        val eventsForTargetDay = getEventDayEvent(event.timeInMillis)
        if (eventsForTargetDay == null) {
            val events: MutableList<Event> = ArrayList()
            events.add(event)
            eventsForMonth.add(Events(event.timeInMillis, events))
        } else {
            eventsForTargetDay.events.add(event)
        }
        eventsByMonthAndYearMap[key] = eventsForMonth
    }

    fun removeAllEvents() {
        eventsByMonthAndYearMap.clear()
    }

    fun addEvents(events: List<Event>) {
        val count = events.size
        for (i in 0 until count) {
            addEvent(events[i])
        }
    }

    fun getEventsFor(epochMillis: Long): List<Event?>? {
        val events = getEventDayEvent(epochMillis)
        return events?.events ?: ArrayList()
    }

    fun getEventsForMonthAndYear(month: Int, year: Int): List<Events>? {
        return eventsByMonthAndYearMap[year.toString() + "_" + month]
    }

    fun getEventsForMonth(eventTimeInMillis: Long): List<Event> {
        eventsCalendar.timeInMillis = eventTimeInMillis
        val keyForCalendarEvent = getKeyForCalendarEvent(eventsCalendar)
        val allEventsForMonth: MutableList<Event> = ArrayList()
        eventsByMonthAndYearMap[keyForCalendarEvent]?.forEach {
            allEventsForMonth.addAll(it.events)
        }
        Collections.sort(allEventsForMonth, eventsComparator)
        return allEventsForMonth
    }

    private fun getEventDayEvent(eventTimeInMillis: Long): Events? {
        eventsCalendar.timeInMillis = eventTimeInMillis
        val dayInMonth = eventsCalendar[Calendar.DAY_OF_MONTH]
        val keyForCalendarEvent = getKeyForCalendarEvent(eventsCalendar)
        val eventsForMonthsAndYear: List<Events>? = eventsByMonthAndYearMap[keyForCalendarEvent]
        if (eventsForMonthsAndYear != null) {
            for (events in eventsForMonthsAndYear) {
                eventsCalendar.timeInMillis = events.timeInMillis
                val dayInMonthFromCache = eventsCalendar[Calendar.DAY_OF_MONTH]
                if (dayInMonthFromCache == dayInMonth) {
                    return events
                }
            }
        }
        return null
    }

    fun removeEventByEpochMillis(epochMillis: Long) {
        eventsCalendar.timeInMillis = epochMillis
        val dayInMonth = eventsCalendar[Calendar.DAY_OF_MONTH]
        val key = getKeyForCalendarEvent(eventsCalendar)
        val eventsForMonthAndYear = eventsByMonthAndYearMap[key]
        if (eventsForMonthAndYear != null) {
            val calendarDayEventIterator = eventsForMonthAndYear.iterator()
            while (calendarDayEventIterator.hasNext()) {
                val next = calendarDayEventIterator.next()
                eventsCalendar.timeInMillis = next.timeInMillis
                val dayInMonthFromCache = eventsCalendar[Calendar.DAY_OF_MONTH]
                if (dayInMonthFromCache == dayInMonth) {
                    calendarDayEventIterator.remove()
                    break
                }
            }
            if (eventsForMonthAndYear.isEmpty()) {
                eventsByMonthAndYearMap.remove(key)
            }
        }
    }

    fun removeEvent(event: Event) {
        eventsCalendar.timeInMillis = event.timeInMillis
        val key = getKeyForCalendarEvent(eventsCalendar)
        val eventsForMonthAndYear = eventsByMonthAndYearMap[key]
        if (eventsForMonthAndYear != null) {
            val eventsForMonthYrItr = eventsForMonthAndYear.iterator()
            while (eventsForMonthYrItr.hasNext()) {
                val events = eventsForMonthYrItr.next()
                val indexOfEvent = events.events.indexOf(event)
                if (indexOfEvent >= 0) {
                    if (events.events.size == 1) {
                        eventsForMonthYrItr.remove()
                    } else {
                        events.events.removeAt(indexOfEvent)
                    }
                    break
                }
            }
            if (eventsForMonthAndYear.isEmpty()) {
                eventsByMonthAndYearMap.remove(key)
            }
        }
    }

    fun removeEvents(events: List<Event>) {
        val count = events.size
        for (i in 0 until count) {
            removeEvent(events[i])
        }
    }

    //E.g. 4 2016 becomes 2016_4
    private fun getKeyForCalendarEvent(cal: Calendar): String {
        return cal[Calendar.YEAR].toString() + "_" + cal[Calendar.MONTH]
    }
}
