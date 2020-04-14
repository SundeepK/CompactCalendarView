package com.github.sundeepk.compactcalendarview

import android.graphics.Color
import com.github.sundeepk.compactcalendarview.domain.Event
import java.util.*

object CompactCalendarHelper {
    fun getOneEventPerDayForMonth(start: Int, days: Int, timeStamp: Long): MutableList<Event> {
        return getOneEventPerDayForMonth(start, days, timeStamp, Color.BLUE)
    }

    //generate one event per a day for a month
    fun getOneEventPerDayForMonth(start: Int, days: Int, timeStamp: Long, color: Int): MutableList<Event> {
        val currentCalender = Calendar.getInstance(Locale.getDefault())
        val events: MutableList<Event> = ArrayList()
        for (i in start until days) {
            setDateTime(timeStamp, currentCalender, i)
            events.add(Event(color, currentCalender.timeInMillis))
        }
        return events
    }

    fun getEvents(start: Int, days: Int, timeStamp: Long): List<Events> {
        val currentCalender = Calendar.getInstance(Locale.getDefault())
        val events: MutableList<Events> = ArrayList()
        for (i in start until days) {
            setDateTime(timeStamp, currentCalender, i)
            val eventList: MutableList<Event> = ArrayList()
            eventList.add(Event(Color.BLUE, currentCalender.timeInMillis))
            val eventsObject = Events(currentCalender.timeInMillis, eventList)
            events.add(eventsObject)
        }
        return events
    }

    fun getDayEventWith2EventsPerDay(start: Int, days: Int, timeStamp: Long): List<Events> {
        val currentCalender = Calendar.getInstance(Locale.getDefault())
        val events: MutableList<Events> = ArrayList()
        for (i in start until days) {
            setDateTime(timeStamp, currentCalender, i)
            val eventList: MutableList<Event> = ArrayList()
            eventList.add(Event(Color.BLUE, currentCalender.timeInMillis))
            eventList.add(Event(Color.RED, currentCalender.timeInMillis + 3600 * 1000))
            val eventsObject = Events(currentCalender.timeInMillis, eventList)
            events.add(eventsObject)
        }
        return events
    }

    fun getDayEventWithMultipleEventsPerDay(start: Int, days: Int, timeStamp: Long): List<Events> {
        val currentCalender = Calendar.getInstance(Locale.getDefault())
        val events: MutableList<Events> = ArrayList()
        for (i in start until days) {
            setDateTime(timeStamp, currentCalender, i)
            val eventsList = mutableListOf(Event(Color.BLUE, currentCalender.timeInMillis),
                    Event(Color.RED, currentCalender.timeInMillis + 3600 * 1000),
                    Event(Color.RED, currentCalender.timeInMillis + 3600 * 2 * 1000),
                    Event(Color.RED, currentCalender.timeInMillis + 3600 * 3 * 1000))
            val eventsObject = Events(currentCalender.timeInMillis, eventsList)
            events.add(eventsObject)
        }
        return events
    }

    fun getMultipleEventsForEachDayAsMap(start: Int, days: Int, timeStamp: Long): Map<Long, List<Event>> {
        val currentCalender = Calendar.getInstance(Locale.getDefault())
        val epochMillisToEvents: MutableMap<Long, List<Event>> = HashMap()
        for (i in start until days) {
            setDateTime(timeStamp, currentCalender, i)
            val eventList: MutableList<Event> = ArrayList()
            val events = mutableListOf(Event(Color.BLUE, currentCalender.timeInMillis),
                    Event(Color.RED, currentCalender.timeInMillis + 3600 * 1000),
                    Event(Color.RED, currentCalender.timeInMillis + 3600 * 2 * 1000),
                    Event(Color.RED, currentCalender.timeInMillis + 3600 * 3 * 1000))
            eventList.addAll(events)
            epochMillisToEvents[currentCalender.timeInMillis] = eventList
        }
        return epochMillisToEvents
    }

    fun setDateTime(timeStamp: Long, currentCalender: Calendar, i: Int) {
        currentCalender.timeInMillis = timeStamp
        currentCalender[Calendar.DATE] = 1
        currentCalender[Calendar.HOUR_OF_DAY] = 0
        currentCalender[Calendar.MINUTE] = 0
        currentCalender[Calendar.SECOND] = 0
        currentCalender[Calendar.MILLISECOND] = 0
        currentCalender.add(Calendar.DATE, i)
    }

    fun setTimeToMidnightAndGet(cal: Calendar, epoch: Long): Long {
        cal.time = Date(epoch)
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        return cal.timeInMillis
    }
}
