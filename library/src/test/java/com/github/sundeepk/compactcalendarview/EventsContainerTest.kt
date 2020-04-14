package com.github.sundeepk.compactcalendarview

import android.graphics.Color
import com.github.sundeepk.compactcalendarview.domain.Event
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

class EventsContainerTest {
    private var underTest: EventsContainer? = null
    @Before
    fun setUp() {
        Locale.setDefault(Locale.ENGLISH)
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
        underTest = EventsContainer(Calendar.getInstance())
    }

    @Test
    fun testItRemovesAllEvents() { //Sun, 01 Feb 2015 00:00:00 GMT
        val events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1422748800000L)
        for (event in events) {
            underTest!!.addEvent(event)
        }
        underTest!!.removeAllEvents()
        val actualEvents = underTest!!.getEventsFor(1422748800000L)
        Assert.assertEquals(ArrayList<Event>(), actualEvents)
    }

    @Test
    fun testItAddsAndGetsEvents() { //Sun, 01 Feb 2015 00:00:00 GMT
        var events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1422748800000L)
        for (event in events) {
            underTest!!.addEvent(event)
        }
        events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 28, 1422748800000L)
        val actualEvents = underTest!!.getEventsFor(1422748800000L)
        Assert.assertEquals(events[0], actualEvents!![0])
    }

    @Test
    fun testItAddsEventsUsingList() { //Sun, 01 Feb 2015 00:00:00 GMT
        var events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1422748800000L)
        underTest!!.addEvents(events)
        events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 28, 1422748800000L)
        val actualEvents = underTest!!.getEventsFor(1422748800000L)
        Assert.assertEquals(1, actualEvents!!.size)
        Assert.assertEquals(events[0], actualEvents[0])
    }

    @Test
    fun testItRemovesEvents() { //Sun, 01 Feb 2015 00:00:00 GMT
        val events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1422748800000L)
        for (event in events) {
            underTest!!.addEvent(event)
        }
        underTest!!.removeEvent(events[0])
        underTest!!.removeEvent(events[1])
        underTest!!.removeEvent(events[5])
        underTest!!.removeEvent(events[20])
        val expectedEvents = CompactCalendarHelper.getOneEventPerDayForMonth(0, 28, 1422748800000L)
        expectedEvents.remove(events[0])
        expectedEvents.remove(events[1])
        expectedEvents.remove(events[5])
        expectedEvents.remove(events[20])
        for (e in expectedEvents) {
            val actualEvents = underTest!!.getEventsFor(e.timeInMillis)
            Assert.assertEquals(1, actualEvents!!.size)
            Assert.assertEquals(e, actualEvents[0])
        }
    }

    @Test
    fun testItRemovesEventFromCacheIfEmpty() { //Sun, 01 Feb 2015 00:00:00 GMT
        val event = Event(Color.BLUE, 1422748800001L)
        val event2 = Event(Color.BLUE, 1442758800000L)
        underTest!!.addEvent(event)
        underTest!!.addEvent(event2)
        Assert.assertEquals(event, underTest!!.getEventsForMonthAndYear(1, 2015)!![0].events[0])
        underTest!!.removeEvent(event)
        Assert.assertNull(underTest!!.getEventsForMonthAndYear(1, 2015))
        Assert.assertEquals(mutableListOf(event2), underTest!!.getEventsFor(1442758800000L))
    }

    @Test
    fun testItRemovesEventFromCacheIfEmptyUsingEpoch() { //Sun, 01 Feb 2015 00:00:00 GMT
        val event = Event(Color.BLUE, 1422748800001L)
        val event2 = Event(Color.BLUE, 1442758800000L)
        underTest!!.addEvent(event)
        underTest!!.addEvent(event2)
        Assert.assertEquals(event, underTest!!.getEventsForMonthAndYear(1, 2015)!![0].events[0])
        underTest!!.removeEventByEpochMillis(1422748800001L)
        Assert.assertNull(underTest!!.getEventsForMonthAndYear(1, 2015))
        Assert.assertEquals(mutableListOf(event2), underTest!!.getEventsFor(1442758800000L))
    }

    @Test
    fun testItDoesNotInterfereWithOtherEventsWhenRemovingUnknownEvent() { //Sun, 01 Feb 2015 00:00:00 GMT
        val expectedEvents = CompactCalendarHelper.getOneEventPerDayForMonth(0, 28, 1422748800000L)
        underTest!!.addEvents(expectedEvents)
        //Sun, 20 September 2015 14:20:00 GMT
        underTest!!.removeEvent(Event(Color.BLUE, 1442758800000L))
        val actualEvents = underTest!!.getEventsForMonth(1422748800000L)
        val empty = underTest!!.getEventsForMonth(1442758800000L)
        Assert.assertEquals(empty, ArrayList<Event>())
        Assert.assertEquals(expectedEvents, actualEvents)
    }

    @Test
    fun testItGetsMultipleEventsThatWereAddedForADay() { //Add 3 events per a day for Feb starting from Sun, 01 Feb 2015 00:00:00 GMT
        val events = CompactCalendarHelper.getMultipleEventsForEachDayAsMap(0, 30, 1422748800000L)
        for ((_, value) in events) {
            for (event in value) {
                underTest!!.addEvent(event)
            }
        }
        //if multiple events were added for every day, then check that all events are present by day
        for ((key, value) in events) {
            val actualEvents = underTest!!.getEventsFor(key)
            Assert.assertEquals(value, actualEvents)
        }
    }

    @Test
    fun testItRemovesEventsUsingList() { //Sun, 01 Feb 2015 00:00:00 GMT
        val events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1422748800000L)
        for (event in events) {
            underTest!!.addEvent(event)
        }
        underTest!!.removeEvents(mutableListOf(events[0], events[1], events[5], events[20]))
        val expectedEvents = CompactCalendarHelper.getOneEventPerDayForMonth(0, 28, 1422748800000L)
        expectedEvents.removeAll(mutableListOf(events[0], events[1], events[5], events[20]))
        for (e in expectedEvents) {
            val actualEvents = underTest!!.getEventsFor(e.timeInMillis)
            Assert.assertEquals(1, actualEvents!!.size)
            Assert.assertEquals(e, actualEvents[0])
        }
    }

    @Test
    fun testItGetsEventsForSpecificDay() { //Sun, 07 Jun 2015 18:20:51 GMT
//get 30 events in total
        val events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1433701251000L)
        for (event in events) {
            underTest!!.addEvent(event)
        }
        //Wed, 24 Aug 2016 09:21:09 GMT
//get 30 events in total
        val events2 = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1472030469000L)
        for (event in events2) {
            underTest!!.addEvent(event)
        }
        //Sun, 07 Jun 2015 18:20:51 GMT
        val calendarDayEvents = underTest!!.getEventsFor(CompactCalendarHelper.setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L))
        Assert.assertNotNull(calendarDayEvents)
        //Assert 6th item since it will represent Sun, 07 Jun 2015 which is the day that we queried for
        Assert.assertEquals(1, calendarDayEvents!!.size.toLong())
        Assert.assertEquals(events[6], calendarDayEvents[0])
    }

    @Test
    fun testItGetsEventsForMonth() { //Sun, 07 Jun 2015 18:20:51 GMT
//get 30 events in total
        val events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1433701251000L)
        //Wed, 06 Jul 2016 13:37:32 GMT
        val events2 = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1467812256000L)
        //give a random ordering to elements
        events.shuffle(Random())
        events2.shuffle(Random())
        for (event in events) {
            underTest!!.addEvent(event)
        }
        for (event in events2) {
            underTest!!.addEvent(event)
        }
        //Sun, 07 Jun 2015 18:20:51 GMT
        val calendarDayEvents = underTest!!.getEventsForMonth(CompactCalendarHelper.setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L))
        Assert.assertNotNull(calendarDayEvents)
        //Assert 6th item since it will represent Sun, 07 Jun 2015 which is the day that we queried for
//Check that events are sorted as expected
        Assert.assertEquals(CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1433701251000L), calendarDayEvents)
    }

    @Test
    fun testItReturnsEmptyForMonthWithNotEvents() { //Sun, 07 Jun 2015 18:20:51 GMT
//get 30 events in total
        val events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1433701251000L)
        underTest!!.addEvents(events)
        //Fri, 07 Aug 2015 12:09:59 GMT
        val calendarDayEvents = underTest!!.getEventsForMonth(CompactCalendarHelper.setTimeToMidnightAndGet(Calendar.getInstance(), 1438949399000L))
        Assert.assertEquals(ArrayList<Event>(), calendarDayEvents)
    }

    @Test
    fun testItRemovesEventByDate() { //Sun, 07 Jun 2015 18:20:51 GMT
//get 30 events in total
        val events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1433701251000L)
        for (event in events) {
            underTest!!.addEvent(event)
        }
        Assert.assertEquals(1, underTest!!.getEventsFor(CompactCalendarHelper.setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L))!!.size.toLong())
        Assert.assertEquals(events[6], underTest!!.getEventsFor(CompactCalendarHelper.setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L))!![0])
        //Sun, 07 Jun 2015 18:20:51 GMT
        underTest!!.removeEventByEpochMillis(CompactCalendarHelper.setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L))
        //Remove 6th item since it will represent Sun, 07 Jun 2015 which is the day that was removed
        events.removeAt(6)
        Assert.assertEquals(0, underTest!!.getEventsFor(CompactCalendarHelper.setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L))!!.size.toLong())
    }

    @Test
    fun testItUpdatesEvents() { //Sun, 07 Jun 2015 18:20:51 GMT
//get 30 events in total
        val events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1433701251000L)
        for (event in events) {
            underTest!!.addEvent(event)
        }
        //Sun, 07 Jun 2015 18:20:51 GMT
        val calendarDayEvents = underTest!!.getEventsFor(CompactCalendarHelper.setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L))
        Assert.assertNotNull(calendarDayEvents)
        //Assert 6th item since it will represent Sun, 07 Jun 2015 which is the day that we queried for
        Assert.assertEquals(events[6], calendarDayEvents!![0])
        //Add a random event Sun, 07 Jun 2015 21:24:21 GMT
        val updateItem = Event(Color.GREEN, 1433712261000L)
        calendarDayEvents as MutableList
        calendarDayEvents.add(updateItem)
        //Query again Sun, 07 Jun 2015 18:20:51 GMT to make sure list is updated
        val calendarDayEvents2 = underTest!!.getEventsFor(CompactCalendarHelper.setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L))
        Assert.assertTrue(calendarDayEvents2!!.contains(updateItem))
    }

    @Test
    fun testItAddsEventsToExistingList() { //Sun, 07 Jun 2015 18:20:51 GMT
//get 30 events in total
        val events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1433701251000L)
        underTest!!.addEvents(events)
        //Sun, 07 Jun 2015 18:20:51 GMT
        val calendarDayEvents = underTest!!.getEventsFor(CompactCalendarHelper.setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L))
        //Assert 6th item since it will represent Sun, 07 Jun 2015 which is the day that we queried for
        Assert.assertEquals(events[6], calendarDayEvents!![0])
        //add event in Sun, 07 Jun 2015 18:20:51 GMT for same day, making total 2 events for same day now
        val extraEventAdded = Event(Color.GREEN, 1433701251000L)
        underTest!!.addEvent(extraEventAdded)
        //Sun, 07 Jun 2015 18:20:51 GMT
        val calendarDayEvents2 = underTest!!.getEventsFor(CompactCalendarHelper.setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L))
        Assert.assertNotNull(calendarDayEvents2)
        //Assert 6th item since it will represent Sun, 07 Jun 2015 which is the day that we queried for
        Assert.assertEquals(2, calendarDayEvents2!!.size.toLong())
        Assert.assertEquals(events[6], calendarDayEvents2[0])
        Assert.assertEquals(extraEventAdded, calendarDayEvents2[1])
    }
}
