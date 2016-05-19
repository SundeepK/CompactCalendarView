package com.github.sundeepk.compactcalendarview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.OverScroller;

import com.github.sundeepk.compactcalendarview.domain.Event;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompactCalendarControllerTest {

    @Mock private Paint paint;
    @Mock private OverScroller overScroller;
    @Mock private Canvas canvas;
    @Mock private Rect rect;
    @Mock private Calendar calendar;
    @Mock private MotionEvent motionEvent;
    @Mock private VelocityTracker velocityTracker;

    private static final String[] dayColumnNames = {"M", "T", "W", "T", "F", "S", "S"};

    CompactCalendarController underTest;

    @Before
    public void setUp(){
        when(velocityTracker.getXVelocity()).thenReturn(-200f);
        underTest = new CompactCalendarController(paint, overScroller, rect, null, null, 0, 0, 0, velocityTracker, 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testItThrowsWhenDayColumnsIsNotLengthSeven(){
        String[] dayNames = {"Mon", "Tue", "Wed", "Thur", "Fri"};
        underTest.setDayColumnNames(dayNames);
    }

    @Test
    public void testManualScrollAndGestureScrollPlayNicelyTogether(){
        //Set width of view so that scrolling will return a correct value
        underTest.onMeasure(720, 1080, 0, 0);

        Calendar cal = Calendar.getInstance();

        //Sun, 08 Feb 2015 00:00:00 GMT
        underTest.setCurrentDate(setTimeToMidnightAndGet(cal, 1423353600000L));

        underTest.showNextMonth();

        //Sun, 01 Mar 2015 00:00:00 GMT - expected
        assertEquals(setTimeToMidnightAndGet(cal, 1425168000000L), underTest.getFirstDayOfCurrentMonth());

        when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_UP);

        //Scroll enough to push calender to next month
        underTest.onScroll(motionEvent, motionEvent, 600, 0);
        underTest.onDraw(canvas);
        underTest.onTouch(motionEvent);

        //Wed, 01 Apr 2015 00:00:00 GMT
        assertEquals(setTimeToMidnightAndGet(cal, 1427842800000L), underTest.getFirstDayOfCurrentMonth());
    }

    @Test
    public void testItScrollsToNextMonth(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        underTest.setCurrentDate(new Date(1423353600000L));

        underTest.showNextMonth();
        Date actualDate = underTest.getFirstDayOfCurrentMonth();

        //Sun, 01 Mar 2015 00:00:00 GMT - expected
        assertEquals(new Date(1425168000000L), actualDate);
    }

    @Test
    public void testItScrollsToPreviousMonth(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        underTest.setCurrentDate(new Date(1423353600000L));

        underTest.showPreviousMonth();
        Date actualDate = underTest.getFirstDayOfCurrentMonth();

        // Thu, 01 Jan 2015 00:00:00 GMT - expected
        assertEquals(new Date(1420070400000L), actualDate);
    }

    @Test
    public void testItSetsDayColumns(){
        //simulate Feb month
        when(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(1);
        when(calendar.get(Calendar.MONTH)).thenReturn(1);
        when(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).thenReturn(28);

        String[] dayNames = {"Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun"};
        underTest.setGrowProgress(1000); //set grow progress so that it simulates the calendar being open
        underTest.setDayColumnNames(dayNames);
        underTest.drawMonth(canvas, calendar, 0);

        InOrder inOrder = inOrder(canvas);
        inOrder.verify(canvas).drawText(eq("Mon"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("Tue"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("Wed"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("Thur"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("Fri"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("Sat"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("Sun"), anyInt(), anyInt(), eq(paint));
    }

    @Test
    public void testListenerIsCalledOnMonthScroll(){
        //Sun, 01 Mar 2015 00:00:00 GMT
        Date expectedDateOnScroll = new Date(1425168000000L);

        when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_UP);

        //Set width of view so that scrolling will return a correct value
        underTest.onMeasure(720, 1080, 0, 0);

        //Sun, 08 Feb 2015 00:00:00 GMT
        underTest.setCurrentDate(new Date(1423353600000L));

        //Scroll enough to push calender to next month
        underTest.onScroll(motionEvent, motionEvent, 600, 0);
        underTest.onDraw(canvas);
        underTest.onTouch(motionEvent);
        assertEquals(expectedDateOnScroll, underTest.getFirstDayOfCurrentMonth());
    }

    @Test
    public void testItAbbreviatesDayNames(){
        //simulate Feb month
        when(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(1);
        when(calendar.get(Calendar.MONTH)).thenReturn(1);
        when(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).thenReturn(28);

        underTest.setGrowProgress(1000); //set grow progress so that it simulates the calendar being open
        underTest.setLocale(Locale.FRANCE);
        reset(canvas); //reset because invalidate is called
        underTest.setUseWeekDayAbbreviation(true);
        reset(canvas); //reset because invalidate is called
        underTest.drawMonth(canvas, calendar, 0);

        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.FRANCE);
        String[] dayNames = dateFormatSymbols.getShortWeekdays();

        InOrder inOrder = inOrder(canvas);
        inOrder.verify(canvas).drawText(eq(dayNames[2]), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq(dayNames[3]), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq(dayNames[4]), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq(dayNames[5]), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq(dayNames[6]), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq(dayNames[7]), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq(dayNames[1]), anyInt(), anyInt(), eq(paint));
    }

    @Test
    public void testItReturnsFirstDayOfMonthAfterDateHasBeenSet(){
        //Sun, 01 Feb 2015 00:00:00 GMT
        Date expectedDate = new Date(1422748800000L);

        //Sun, 08 Feb 2015 00:00:00 GMT
        underTest.setCurrentDate(new Date(1423353600000L));

        Date actualDate = underTest.getFirstDayOfCurrentMonth();
        assertEquals(expectedDate, actualDate);
    }

    @Test
    public void testItReturnsFirstDayOfMonth(){
        Calendar currentCalender =  Calendar.getInstance();
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
        currentCalender.set(Calendar.HOUR_OF_DAY, 0);
        currentCalender.set(Calendar.MINUTE, 0);
        currentCalender.set(Calendar.SECOND, 0);
        currentCalender.set(Calendar.MILLISECOND, 0);
        Date expectFirstDayOfMonth = currentCalender.getTime();

        Date actualDate = underTest.getFirstDayOfCurrentMonth();

        assertEquals(expectFirstDayOfMonth, actualDate);
    }

    @Test
    public void testItDrawsSundayAsFirstDay(){
        //simulate Feb month
        when(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(1);
        when(calendar.get(Calendar.MONTH)).thenReturn(1);
        when(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).thenReturn(28);

        underTest.setGrowProgress(1000); //set grow progress so that it simulates the calendar being open
        underTest.setShouldShowMondayAsFirstDay(false);
        underTest.setUseWeekDayAbbreviation(true);
        underTest.drawMonth(canvas, calendar, 0);

        InOrder inOrder = inOrder(canvas);
        inOrder.verify(canvas).drawText(eq("Sun"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("Mon"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("Tue"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("Wed"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("Thu"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("Fri"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("Sat"), anyInt(), anyInt(), eq(paint));
    }

    @Test
    public void testItDrawsFirstLetterOfEachDay(){
        //simulate Feb month
        when(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(1);
        when(calendar.get(Calendar.MONTH)).thenReturn(1);
        when(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).thenReturn(28);

        underTest.setGrowProgress(1000); //set grow progress so that it simulates the calendar being open
        underTest.drawMonth(canvas, calendar, 0);

        InOrder inOrder = inOrder(canvas);
        inOrder.verify(canvas).drawText(eq("M"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("T"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("W"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("T"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("F"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("S"), anyInt(), anyInt(), eq(paint));
        inOrder.verify(canvas).drawText(eq("S"), anyInt(), anyInt(), eq(paint));
    }

    @Test
    public void testItDrawsDaysOnCalender(){
        //simulate Feb month
        underTest.setGrowProgress(1000); //set grow progress so that it simulates the calendar being open
        when(calendar.get(Calendar.DAY_OF_WEEK)).thenReturn(1);
        when(calendar.get(Calendar.MONTH)).thenReturn(1);
        when(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).thenReturn(28);

        underTest.drawMonth(canvas, calendar, 0);

        for(int dayColumn = 0, dayRow = 0; dayColumn <= 6; dayRow++){
            if(dayRow == 7){
                dayRow = 0;
                if(dayColumn <= 6){
                    dayColumn++;
                }
            }
            if(dayColumn == dayColumnNames.length){
                break;
            }
            if(dayColumn == 0){
                verify(canvas).drawText(eq(dayColumnNames[dayColumn]), anyInt(), anyInt(), eq(paint));
            }else{
                int day = ((dayRow - 1) * 7 + dayColumn + 1) - 6;
                if( day > 0 && day <= 28){
                    verify(canvas).drawText(eq(String.valueOf(day)), anyInt(), anyInt(), eq(paint));
                }
            }
        }
    }

    @Test
    public void testItRemovesAllEvents(){
        //Sun, 01 Feb 2015 00:00:00 GMT
        List<Event> events = getEvents(0, 30, 1422748800000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        underTest.removeAllEvents();

        List<Event> actualEvents = underTest.getCalendarEventsFor(new Date(1422748800000L));
        Assert.assertEquals(new ArrayList<Event>(), actualEvents);
    }

    @Test
    public void testItAddsAndGetsEvents(){
        //Sun, 01 Feb 2015 00:00:00 GMT
        List<Event> events = getEvents(0, 30, 1422748800000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        events = getEvents(0, 28, 1422748800000L);

        List<Event> actualEvents = underTest.getCalendarEventsFor(new Date(1422748800000L));
        Assert.assertEquals(events.get(0), actualEvents.get(0));
    }

    @Test
    public void testItAddsEventsUsingList(){
        //Sun, 01 Feb 2015 00:00:00 GMT
        List<Event> events = getEvents(0, 30, 1422748800000L);

        underTest.addEvents(events);

        events = getEvents(0, 28, 1422748800000L);

        List<Event> actualEvents = underTest.getCalendarEventsFor(new Date(1422748800000L));
        Assert.assertEquals(1, actualEvents.size());
        Assert.assertEquals(events.get(0), actualEvents.get(0));
    }

    @Test
    public void testItRemovesEvents(){
        //Sun, 01 Feb 2015 00:00:00 GMT
        List<Event> events = getEvents(0, 30, 1422748800000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        underTest.removeEvent(events.get(0));
        underTest.removeEvent(events.get(1));
        underTest.removeEvent(events.get(5));
        underTest.removeEvent(events.get(20));

        List<Event> expectedEvents = getEvents(0, 28, 1422748800000L);
        expectedEvents.remove(events.get(0));
        expectedEvents.remove(events.get(1));
        expectedEvents.remove(events.get(5));
        expectedEvents.remove(events.get(20));

        for (Event e : expectedEvents) {
            List<Event> actualEvents = underTest.getCalendarEventsFor(new Date(e.getTimeInMillis()));
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
            List<Event> actualEvents = underTest.getCalendarEventsFor(new Date(entry.getKey()));
            Assert.assertEquals(entry.getValue(), actualEvents);
        }
    }

    @Test
    public void testItRemovesEventsUsingList(){
        //Sun, 01 Feb 2015 00:00:00 GMT
        List<Event> events = getEvents(0, 30, 1422748800000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        underTest.removeEvents(Arrays.asList(events.get(0), events.get(1), events.get(5), events.get(20)));

        List<Event> expectedEvents = getEvents(0, 28, 1422748800000L);
        expectedEvents.removeAll(Arrays.asList(events.get(0), events.get(1), events.get(5), events.get(20)));

        for (Event e : expectedEvents) {
            List<Event> actualEvents = underTest.getCalendarEventsFor(new Date(e.getTimeInMillis()));
            Assert.assertEquals(1, actualEvents.size());
            Assert.assertEquals(e, actualEvents.get(0));
        }
    }

    @Test
    public void testItDrawsEventDaysOnCalendar(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        //get 30 events in total
        List<Event> events = getEvents(0, 30, 1433701251000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        when(calendar.get(Calendar.MONTH)).thenReturn(5);
        when(calendar.get(Calendar.YEAR)).thenReturn(2015);

        underTest.setGrowProgress(1000); //set grow progress so that it simulates the calendar being open
        underTest.drawEvents(canvas, calendar, 0);

        //draw events 29 times because we don't draw events for the current selected day since it wil be highlighted with another indicator
        verify(canvas, times(29)).drawCircle(anyFloat(), anyFloat(), anyFloat(), eq(paint));
    }

    @Test
    public void testItDrawsMultipleEventDaysOnCalendar(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        //get 60 events in total
        List<Event> events = getDayEventWith2EventsPerDay(0, 30, 1433701251000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        when(calendar.get(Calendar.MONTH)).thenReturn(5);
        when(calendar.get(Calendar.YEAR)).thenReturn(2015);

        underTest.setGrowProgress(1000); //set grow progress so that it simulates the calendar being open
        underTest.drawEvents(canvas, calendar, 0);

        //draw events 58 times because we don't draw events for the current selected day since it wil be highlighted with another indicator
        verify(canvas, times(58)).drawCircle(anyFloat(), anyFloat(), anyFloat(), eq(paint));
    }

    @Test
    public void testItDrawsMultipleEventDaysOnCalendarWithPlusIndicator(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        //get 120 events in total but only draw 3 event indicators per a day
        List<Event> events = getDayEventWithMultipleEventsPerDay(0, 30, 1433701251000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        when(calendar.get(Calendar.MONTH)).thenReturn(5);
        when(calendar.get(Calendar.YEAR)).thenReturn(2015);

        underTest.setGrowProgress(1000); //set grow progress so that it simulates the calendar being open
        underTest.drawEvents(canvas, calendar, 0);

        //draw events 58 times because we don't draw more than 3 indicators
        verify(canvas, times(58)).drawCircle(anyFloat(), anyFloat(), anyFloat(), eq(paint));

        //draw event indicator with lines
        // 2 calls for each plus event indicator since it takes 2 draw calls to make a plus sign
        verify(canvas, times(58)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), eq(paint));
    }

    @Test
    public void testItGetsEventsForSpecificDay(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        //get 30 events in total
        List<Event> events = getEvents(0, 30, 1433701251000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        //Wed, 24 Aug 2016 09:21:09 GMT
        //get 30 events in total
        List<Event> events2 = getEvents(0, 30, 1472030469000L);
        for(Event event : events2){
            underTest.addEvent(event);
        }

        //Sun, 07 Jun 2015 18:20:51 GMT
        List<Event> calendarDayEvents = underTest.getCalendarEventsFor(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L));
        assertNotNull(calendarDayEvents);
        //Assert 6th item since it will represent Sun, 07 Jun 2015 which is the day that we queried for
        assertEquals(1, calendarDayEvents.size());
        assertEquals(events.get(6), calendarDayEvents.get(0));
    }

    @Test
    public void testItRemovesEventByDate(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        //get 30 events in total
        List<Event> events = getEvents(0, 30, 1433701251000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        assertEquals(1, underTest.getCalendarEventsFor(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L)).size());
        assertEquals(events.get(6), underTest.getCalendarEventsFor(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L)).get(0));


        //Sun, 07 Jun 2015 18:20:51 GMT
        underTest.removeEventsByDate(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L));
        //Remove 6th item since it will represent Sun, 07 Jun 2015 which is the day that was removed
        events.remove(6);
        assertEquals(0, underTest.getCalendarEventsFor(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L)).size());
    }

    @Test
    public void testItUpdatesEvents(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        //get 30 events in total
        List<Event> events = getEvents(0, 30, 1433701251000L);
        for(Event event : events){
            underTest.addEvent(event);
        }

        //Sun, 07 Jun 2015 18:20:51 GMT
        List<Event> calendarDayEvents = underTest.getCalendarEventsFor(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L));
        assertNotNull(calendarDayEvents);
        //Assert 6th item since it will represent Sun, 07 Jun 2015 which is the day that we queried for
        assertEquals(events.get(6), calendarDayEvents.get(0));

        //Add a random event Sun, 07 Jun 2015 21:24:21 GMT
        Event updateItem = new Event(Color.GREEN, 1433712261000L);
        calendarDayEvents.add(updateItem);

        //Query again Sun, 07 Jun 2015 18:20:51 GMT to make sure list is updated
        List<Event> calendarDayEvents2 = underTest.getCalendarEventsFor(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L));
        assertTrue(calendarDayEvents2.contains(updateItem));
    }

    @Test
    public void testItAddsEventsToExistingList(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        //get 30 events in total
        List<Event> events = getEvents(0, 30, 1433701251000L);
        underTest.addEvents(events);

        //Sun, 07 Jun 2015 18:20:51 GMT
        List<Event> calendarDayEvents = underTest.getCalendarEventsFor(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L));
        //Assert 6th item since it will represent Sun, 07 Jun 2015 which is the day that we queried for
        assertEquals(events.get(6), calendarDayEvents.get(0));

        //add event in Sun, 07 Jun 2015 18:20:51 GMT for same day, making total 2 events for same day now
        Event extraEventAdded = new Event(Color.GREEN, 1433701251000L);
        underTest.addEvent(extraEventAdded);

        //Sun, 07 Jun 2015 18:20:51 GMT
        List<Event> calendarDayEvents2 = underTest.getCalendarEventsFor(setTimeToMidnightAndGet(Calendar.getInstance(), 1433701251000L));

        assertNotNull(calendarDayEvents2);
        //Assert 6th item since it will represent Sun, 07 Jun 2015 which is the day that we queried for
        assertEquals(2, calendarDayEvents2.size());
        assertEquals(events.get(6), calendarDayEvents2.get(0));
        assertEquals(extraEventAdded, calendarDayEvents2.get(1));
    }

    private List<Event> getEvents(int start, int days, long timeStamp) {
        return getEvents(start, days, timeStamp, Color.BLUE);
    }

    //generate one event per a day for a month
    private List<Event> getEvents(int start, int days, long timeStamp, int color) {
        Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
        List<Event> events = new ArrayList<>();
        for(int i = start; i < days; i++){
            setDateTime(timeStamp, currentCalender, i);
            events.add(new Event(color, currentCalender.getTimeInMillis()));
        }
        return events;
    }

    private List<Event> getDayEventWith2EventsPerDay(int start, int days, long timeStamp) {
        Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
        List<Event> eventList = new ArrayList<>();
        for(int i = start; i < days; i++){
            setDateTime(timeStamp, currentCalender, i);
            eventList.add(new Event(Color.BLUE, currentCalender.getTimeInMillis()));
            eventList.add(new Event(Color.RED, currentCalender.getTimeInMillis() + 3600 * 1000));
        }
        return eventList;
    }

    private List<Event> getDayEventWithMultipleEventsPerDay(int start, int days, long timeStamp) {
        Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
        List<Event> eventList = new ArrayList<>();
        for(int i = start; i < days; i++){
            setDateTime(timeStamp, currentCalender, i);
            List<Event> events = Arrays.asList(new Event(Color.BLUE, currentCalender.getTimeInMillis()),
                    new Event(Color.RED, currentCalender.getTimeInMillis() + 3600 * 1000),
                    new Event(Color.RED, currentCalender.getTimeInMillis() + (3600 * 2) * 1000),
                    new Event(Color.RED, currentCalender.getTimeInMillis() + (3600 * 3) * 1000));
            eventList.addAll(events);
        }
        return eventList;
    }

    private Map<Long, List<Event>> getMultipleEventsForEachDayAsMap(int start, int days, long timeStamp) {
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

    private void setDateTime(long timeStamp, Calendar currentCalender, int i) {
        currentCalender.setTimeInMillis(timeStamp);
        currentCalender.set(Calendar.DATE, 1);
        currentCalender.set(Calendar.HOUR_OF_DAY, 0);
        currentCalender.set(Calendar.MINUTE, 0);
        currentCalender.set(Calendar.SECOND, 0);
        currentCalender.set(Calendar.MILLISECOND, 0);
        currentCalender.add(Calendar.DATE, i);
    }

    private Date setTimeToMidnightAndGet(Calendar cal, long epoch) {
       cal.setTime(new Date(epoch));
       cal.set(Calendar.HOUR_OF_DAY, 0);
       cal.set(Calendar.MINUTE, 0);
       cal.set(Calendar.SECOND, 0);
       cal.set(Calendar.MILLISECOND, 0);
       return cal.getTime();
    }
}
