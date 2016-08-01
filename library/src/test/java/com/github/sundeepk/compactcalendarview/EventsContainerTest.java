package com.github.sundeepk.compactcalendarview;

import android.graphics.Color;

import com.github.sundeepk.compactcalendarview.domain.Event;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.github.sundeepk.compactcalendarview.CompactCalendarHelper.getMultipleEventsForEachDayAsMap;
import static com.github.sundeepk.compactcalendarview.CompactCalendarHelper.getSingleEvents;
import static com.github.sundeepk.compactcalendarview.CompactCalendarHelper.setTimeToMidnightAndGet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EventsContainerTest {

    private EventsContainer underTest;

    @Before
    public void setUp(){
        underTest = new EventsContainer(Calendar.getInstance());
    }

    @Test
    public void testItRemovesAllEvents(){
        //Sun, 01 Feb 2015 00:00:00 GMT
        List<Event> events = getSingleEvents(0, 30, 1422748800000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        underTest.removeAllEvents();

        List<Event> actualEvents = underTest.getEventsFor(1422748800000L);
        Assert.assertEquals(new ArrayList<Event>(), actualEvents);
    }

    @Test
    public void testItAddsAndGetsEvents(){
        //Sun, 01 Feb 2015 00:00:00 GMT
        List<Event> events = getSingleEvents(0, 30, 1422748800000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        events = getSingleEvents(0, 28, 1422748800000L);

        List<Event> actualEvents = underTest.getEventsFor(1422748800000L);
        Assert.assertEquals(events.get(0), actualEvents.get(0));
    }

    @Test
    public void testItAddsEventsUsingList(){
        //Sun, 01 Feb 2015 00:00:00 GMT
        List<Event> events = getSingleEvents(0, 30, 1422748800000L);

        underTest.addEvents(events);

        events = getSingleEvents(0, 28, 1422748800000L);

        List<Event> actualEvents = underTest.getEventsFor(1422748800000L);
        Assert.assertEquals(1, actualEvents.size());
        Assert.assertEquals(events.get(0), actualEvents.get(0));
    }

    @Test
    public void testItRemovesEvents(){
        //Sun, 01 Feb 2015 00:00:00 GMT
        List<Event> events = getSingleEvents(0, 30, 1422748800000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        underTest.removeEvent(events.get(0));
        underTest.removeEvent(events.get(1));
        underTest.removeEvent(events.get(5));
        underTest.removeEvent(events.get(20));

        List<Event> expectedEvents = getSingleEvents(0, 28, 1422748800000L);
        expectedEvents.remove(events.get(0));
        expectedEvents.remove(events.get(1));
        expectedEvents.remove(events.get(5));
        expectedEvents.remove(events.get(20));

        for (Event e : expectedEvents) {
            List<Event> actualEvents = underTest.getEventsFor(e.getTimeInMillis());
            Assert.assertEquals(1, actualEvents.size());
            Assert.assertEquals(e, actualEvents.get(0));
        }
    }

    @Test
    public void testItGetsMultipleEventsThatWereAddedForADay(){
        //Add 3 events per a day for Feb starting from Sun, 01 Feb 2015 00:00:00 GMT
        Map<Long, List<Event>> events = getMultipleEventsForEachDayAsMap(0, 30, 1422748800000L);
        for(Map.Entry<Long, List<Event>> entry : events.entrySet()){
            for (Event event: entry.getValue()) {
                underTest.addEvent(event);
            }
        }

        //if multiple events were added for every day, then check that all events are present by day
        for(Map.Entry<Long, List<Event>> entry : events.entrySet()){
            List<Event> actualEvents = underTest.getEventsFor(entry.getKey());
            Assert.assertEquals(entry.getValue(), actualEvents);
        }
    }

    @Test
    public void testItRemovesEventsUsingList(){
        //Sun, 01 Feb 2015 00:00:00 GMT
        List<Event> events = getSingleEvents(0, 30, 1422748800000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        underTest.removeEvents(Arrays.asList(events.get(0), events.get(1), events.get(5), events.get(20)));

        List<Event> expectedEvents = getSingleEvents(0, 28, 1422748800000L);
        expectedEvents.removeAll(Arrays.asList(events.get(0), events.get(1), events.get(5), events.get(20)));

        for (Event e : expectedEvents) {
            List<Event> actualEvents = underTest.getEventsFor(e.getTimeInMillis());
            Assert.assertEquals(1, actualEvents.size());
            Assert.assertEquals(e, actualEvents.get(0));
        }
    }

    @Test
    public void testItGetsEventsForSpecificDay(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        //get 30 events in total
        List<Event> events = getSingleEvents(0, 30, 1433701251000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        //Wed, 24 Aug 2016 09:21:09 GMT
        //get 30 events in total
        List<Event> events2 = getSingleEvents(0, 30, 1472030469000L);
        for(Event event : events2){
            underTest.addEvent(event);
        }

        //Sun, 07 Jun 2015 18:20:51 GMT
        List<Event> calendarDayEvents = underTest.getEventsFor(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L));
        assertNotNull(calendarDayEvents);
        //Assert 6th item since it will represent Sun, 07 Jun 2015 which is the day that we queried for
        assertEquals(1, calendarDayEvents.size());
        assertEquals(events.get(6), calendarDayEvents.get(0));
    }

    @Test
    public void testItGetsEventsForMonth(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        //get 30 events in total
        List<Event> events = getSingleEvents(0, 30, 1433701251000L);

        //Wed, 06 Jul 2016 13:37:32 GMT
        List<Event> events2 = getSingleEvents(0, 30, 1467812256000L);

        //give a random ordering to elements
        Collections.shuffle(events, new Random());
        Collections.shuffle(events2, new Random());

        for(Event event : events){
            underTest.addEvent(event);
        }

        for(Event event : events2){
            underTest.addEvent(event);
        }

        //Sun, 07 Jun 2015 18:20:51 GMT
        List<Event> calendarDayEvents = underTest.getEventsForMonth(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L));
        assertNotNull(calendarDayEvents);
        //Assert 6th item since it will represent Sun, 07 Jun 2015 which is the day that we queried for
        //Check that events are sorted as expected
        assertEquals(getSingleEvents(0, 30, 1433701251000L), calendarDayEvents);
    }

    @Test
    public void testItReturnsEmptyForMonthWithNotEvents(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        //get 30 events in total
        List<Event> events = getSingleEvents(0, 30, 1433701251000L);
        underTest.addEvents(events);

        //Fri, 07 Aug 2015 12:09:59 GMT
        List<Event> calendarDayEvents = underTest.getEventsForMonth(setTimeToMidnightAndGet(Calendar.getInstance(), 1438949399000L));
        assertEquals(new ArrayList<Event>(), calendarDayEvents);
    }


    @Test
    public void testItRemovesEventByDate(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        //get 30 events in total
        List<Event> events = getSingleEvents(0, 30, 1433701251000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        assertEquals(1, underTest.getEventsFor(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L)).size());
        assertEquals(events.get(6), underTest.getEventsFor(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L)).get(0));


        //Sun, 07 Jun 2015 18:20:51 GMT
        underTest.removeEventByEpochMillis(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L));
        //Remove 6th item since it will represent Sun, 07 Jun 2015 which is the day that was removed
        events.remove(6);
        assertEquals(0, underTest.getEventsFor(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L)).size());
    }

    @Test
    public void testItUpdatesEvents(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        //get 30 events in total
        List<Event> events = getSingleEvents(0, 30, 1433701251000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        //Sun, 07 Jun 2015 18:20:51 GMT
        List<Event> calendarDayEvents = underTest.getEventsFor(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L));
        assertNotNull(calendarDayEvents);
        //Assert 6th item since it will represent Sun, 07 Jun 2015 which is the day that we queried for
        assertEquals(events.get(6), calendarDayEvents.get(0));

        //Add a random event Sun, 07 Jun 2015 21:24:21 GMT
        Event updateItem = new Event(Color.GREEN, 1433712261000L);
        calendarDayEvents.add(updateItem);

        //Query again Sun, 07 Jun 2015 18:20:51 GMT to make sure list is updated
        List<Event> calendarDayEvents2 = underTest.getEventsFor(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L));
        assertTrue(calendarDayEvents2.contains(updateItem));
    }

    @Test
    public void testItAddsEventsToExistingList(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        //get 30 events in total
        List<Event> events = getSingleEvents(0, 30, 1433701251000L);
        underTest.addEvents(events);

        //Sun, 07 Jun 2015 18:20:51 GMT
        List<Event> calendarDayEvents = underTest.getEventsFor(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L));
        //Assert 6th item since it will represent Sun, 07 Jun 2015 which is the day that we queried for
        assertEquals(events.get(6), calendarDayEvents.get(0));

        //add event in Sun, 07 Jun 2015 18:20:51 GMT for same day, making total 2 events for same day now
        Event extraEventAdded = new Event(Color.GREEN, 1433701251000L);
        underTest.addEvent(extraEventAdded);

        //Sun, 07 Jun 2015 18:20:51 GMT
        List<Event> calendarDayEvents2 = underTest.getEventsFor(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L));

        assertNotNull(calendarDayEvents2);
        //Assert 6th item since it will represent Sun, 07 Jun 2015 which is the day that we queried for
        assertEquals(2, calendarDayEvents2.size());
        assertEquals(events.get(6), calendarDayEvents2.get(0));
        assertEquals(extraEventAdded, calendarDayEvents2.get(1));
    }

}
