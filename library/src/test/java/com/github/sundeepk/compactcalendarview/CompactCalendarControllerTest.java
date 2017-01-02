package com.github.sundeepk.compactcalendarview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.OverScroller;

import com.github.sundeepk.compactcalendarview.domain.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.github.sundeepk.compactcalendarview.CompactCalendarHelper.getDayEventWith2EventsPerDay;
import static com.github.sundeepk.compactcalendarview.CompactCalendarHelper.getDayEventWithMultipleEventsPerDay;
import static com.github.sundeepk.compactcalendarview.CompactCalendarHelper.getEvents;
import static com.github.sundeepk.compactcalendarview.CompactCalendarHelper.getSingleEvents;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
    @Mock private EventsContainer eventsContainer;

    private static final String[] dayColumnNames = {"M", "T", "W", "T", "F", "S", "S"};

    CompactCalendarController underTest;

    @Before
    public void setUp(){
        Locale.setDefault(Locale.ENGLISH);
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

        when(velocityTracker.getXVelocity()).thenReturn(-200f);
        underTest =
                new CompactCalendarController(paint, overScroller, rect, null, null, 0, 0, 0, velocityTracker, 0, eventsContainer, Locale.getDefault(), TimeZone.getDefault());
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
        underTest.setCurrentDate(new Date(setTimeToMidnightAndGet(cal, 1423353600000L)));

        underTest.showNextMonth();

        //Sun, 01 Mar 2015 00:00:00 GMT - expected
        assertEquals(new Date(setTimeToMidnightAndGet(cal, 1425168000000L)), underTest.getFirstDayOfCurrentMonth());

        when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_UP);

        //Scroll enough to push calender to next month
        underTest.onScroll(motionEvent, motionEvent, 600, 0);
        underTest.onDraw(canvas);
        underTest.onTouch(motionEvent);

        //Wed, 01 Apr 2015 00:00:00 GMT
        assertEquals(new Date(setTimeToMidnightAndGet(cal, 1427846400000L)), underTest.getFirstDayOfCurrentMonth());
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
        underTest.setLocale(TimeZone.getTimeZone("Europe/Paris"), Locale.FRANCE);
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
        underTest.setUseWeekDayAbbreviation(true);
        underTest.setFirstDayOfWeek(Calendar.SUNDAY);
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
    public void testItDrawsEventDaysOnCalendar(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        //get 30 events in total
        int numberOfDaysWithEvents = 30;
        List<Events> events = getEvents(0, numberOfDaysWithEvents, 1433701251000L);
        when(eventsContainer.getEventsForMonthAndYear(5, 2015)).thenReturn(events);
        when(calendar.get(Calendar.MONTH)).thenReturn(5);
        when(calendar.get(Calendar.YEAR)).thenReturn(2015);

        underTest.setGrowProgress(1000); //set grow progress so that it simulates the calendar being open
        underTest.drawEvents(canvas, calendar, 0);

        //draw events for every day with an event
        verify(canvas, times(numberOfDaysWithEvents)).drawCircle(anyFloat(), anyFloat(), anyFloat(), eq(paint));
    }

    @Test
    public void testItDrawsMultipleEventDaysOnCalendar(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        //get 60 events in total
        int numberOfDaysWithEvents = 30;
        List<Events> events = getDayEventWith2EventsPerDay(0, numberOfDaysWithEvents, 1433701251000L);
        when(eventsContainer.getEventsForMonthAndYear(5, 2015)).thenReturn(events);
        when(calendar.get(Calendar.MONTH)).thenReturn(5);
        when(calendar.get(Calendar.YEAR)).thenReturn(2015);

        underTest.setGrowProgress(1000); //set grow progress so that it simulates the calendar being open
        underTest.drawEvents(canvas, calendar, 0);

        //draw 2 events per day
        verify(canvas, times(numberOfDaysWithEvents * 2)).drawCircle(anyFloat(), anyFloat(), anyFloat(), eq(paint));
    }

    @Test
    public void testItDrawsMultipleEventDaysOnCalendarWithPlusIndicator(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        //get 120 events in total but only draw 3 event indicators per a day
        int numberOfDaysWithEvents = 30;
        List<Events> events = getDayEventWithMultipleEventsPerDay(0, numberOfDaysWithEvents, 1433701251000L);
        when(eventsContainer.getEventsForMonthAndYear(5, 2015)).thenReturn(events);
        when(calendar.get(Calendar.MONTH)).thenReturn(5);
        when(calendar.get(Calendar.YEAR)).thenReturn(2015);

        underTest.setGrowProgress(1000); //set grow progress so that it simulates the calendar being open
        underTest.drawEvents(canvas, calendar, 0);

        //draw 2 events per day because we don't draw more than 3 indicators
        verify(canvas, times(numberOfDaysWithEvents * 2)).drawCircle(anyFloat(), anyFloat(), anyFloat(), eq(paint));

        //draw event indicator with lines
        // 2 calls for each plus event indicator since it takes 2 draw calls to make a plus sign
        verify(canvas, times(numberOfDaysWithEvents * 2)).drawLine(anyFloat(), anyFloat(), anyFloat(), anyFloat(), eq(paint));
    }

    @Test
    public void testItDrawsEventDaysOnCalendarForCurrentMonth(){
        Calendar todayCalendar = Calendar.getInstance();
        int numberOfDaysInMonth = todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int todayMonth = todayCalendar.get(Calendar.MONTH);
        int todayYear = todayCalendar.get(Calendar.YEAR);

        //get events for every day in the month
        List<Events> events = getEvents(0, numberOfDaysInMonth, todayCalendar.getTimeInMillis());
        when(eventsContainer.getEventsForMonthAndYear(todayMonth, todayYear)).thenReturn(events);
        when(calendar.get(Calendar.MONTH)).thenReturn(todayMonth);
        when(calendar.get(Calendar.YEAR)).thenReturn(todayYear);

        underTest.setGrowProgress(1000); //set grow progress so that it simulates the calendar being open
        underTest.drawEvents(canvas, calendar, 0);

        //draw events for every day except the current day -- selected day is also the current day
        verify(canvas, times(numberOfDaysInMonth - 1)).drawCircle(anyFloat(), anyFloat(), anyFloat(), eq(paint));
    }

    @Test
    public void testItDrawsEventDaysOnCalendarWithSelectedDay(){
        //Sun, 07 Jun 2015 18:20:51 GMT
        long selectedDayTimestamp = 1433701251000L;
        //get 30 events in total
        int numberOfDaysWithEvents = 30;
        List<Events> events = getEvents(0, numberOfDaysWithEvents, selectedDayTimestamp);
        when(eventsContainer.getEventsForMonthAndYear(5, 2015)).thenReturn(events);
        when(calendar.get(Calendar.MONTH)).thenReturn(5);
        when(calendar.get(Calendar.YEAR)).thenReturn(2015);

        underTest.setGrowProgress(1000); //set grow progress so that it simulates the calendar being open
        // Selects first day of the month
        underTest.setCurrentDate(new Date(selectedDayTimestamp));
        underTest.drawEvents(canvas, calendar, 0);

        //draw events for every day except the selected day
        verify(canvas, times(numberOfDaysWithEvents - 1)).drawCircle(anyFloat(), anyFloat(), anyFloat(), eq(paint));
    }

    @Test
    public void testItDrawsEventDaysOnCalendarForCurrentMonthWithSelectedDay(){
        Calendar todayCalendar = Calendar.getInstance();
        int numberOfDaysInMonth = todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int todayMonth = todayCalendar.get(Calendar.MONTH);
        int todayYear = todayCalendar.get(Calendar.YEAR);

        //get events for every day in the month
        List<Events> events = getEvents(0, numberOfDaysInMonth, todayCalendar.getTimeInMillis());
        when(eventsContainer.getEventsForMonthAndYear(todayMonth, todayYear)).thenReturn(events);
        when(calendar.get(Calendar.MONTH)).thenReturn(todayMonth);
        when(calendar.get(Calendar.YEAR)).thenReturn(todayYear);

        // sets either 1st day or 2nd day so that there are always 2 days selected
        int dayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH);
        if (dayOfMonth == 1) {
            todayCalendar.set(Calendar.DAY_OF_MONTH, 2);
        } else {
            todayCalendar.set(Calendar.DAY_OF_MONTH, 1);
        }
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        todayCalendar.set(Calendar.MINUTE, 0);
        todayCalendar.set(Calendar.SECOND, 0);
        todayCalendar.set(Calendar.MILLISECOND, 0);
        underTest.setCurrentDate(todayCalendar.getTime());

        underTest.setGrowProgress(1000); //set grow progress so that it simulates the calendar being open
        underTest.drawEvents(canvas, calendar, 0);

        //draw events for every day except the current day and the selected day
        verify(canvas, times(numberOfDaysInMonth - 2)).drawCircle(anyFloat(), anyFloat(), anyFloat(), eq(paint));
    }

    @Test
    public void testItAddsEvent(){
        Event event = getSingleEvents(0, 30, 1433701251000L).get(0);
        underTest.addEvent(event);
        verify(eventsContainer).addEvent(event);
        verifyNoMoreInteractions(eventsContainer);
    }

    @Test
    public void testItAddsEvents(){
        List<Event> events = getSingleEvents(0, 30, 1433701251000L);
        underTest.addEvents(events);
        verify(eventsContainer).addEvents(events);
        verifyNoMoreInteractions(eventsContainer);
    }

    @Test
    public void testItRemovesEvent(){
        Event event = getSingleEvents(0, 30, 1433701251000L).get(0);
        underTest.removeEvent(event);
        verify(eventsContainer).removeEvent(event);
        verifyNoMoreInteractions(eventsContainer);
    }

    @Test
    public void testItRemovesEvents(){
        List<Event> events = getSingleEvents(0, 30, 1433701251000L);
        underTest.removeEvents(events);
        verify(eventsContainer).removeEvents(events);
        verifyNoMoreInteractions(eventsContainer);
    }

    @Test
    public void testItGetCalendarEventsForADate(){
        underTest.getCalendarEventsFor(1433701251000L);
        verify(eventsContainer).getEventsFor(1433701251000L);
        verifyNoMoreInteractions(eventsContainer);
    }

    @Test
    public void testItRemovesCalendarEventsForADate(){
        underTest.removeEventsFor(1433701251000L);
        verify(eventsContainer).removeEventByEpochMillis(1433701251000L);
        verifyNoMoreInteractions(eventsContainer);
    }

    @Test
    public void testItRemovesAllEvents(){
        underTest.removeAllEvents();
        verify(eventsContainer).removeAllEvents();
        verifyNoMoreInteractions(eventsContainer);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testItThrowsWhenZeroIsUsedAsFirstDayOfWeek(){
        underTest.setFirstDayOfWeek(0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testItThrowsWhenValuesGreaterThanSevenIsUsedAsFirstDayOfWeek(){
        underTest.setFirstDayOfWeek(8);
    }

    @Test
    public void testItGetsDayOfWeekWhenSundayIsFirstDayOfWeek(){
        // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
        // Sunday as first day means Saturday is last day of week
        // first index corresponds to Sunday and last is Saturday
        int[] expectedDaysOfWeekOrder = {0,1,2,3,4,5,6};
        int[] actualDaysOfWeekOrder = new int[7];
        Calendar calendar = Calendar.getInstance();
        underTest.setFirstDayOfWeek(Calendar.SUNDAY);
        for (int day = 1; day <= 7 ; day++){
            calendar.set(Calendar.DAY_OF_WEEK, day);
            actualDaysOfWeekOrder[day - 1] = underTest.getDayOfWeek(calendar);
        }
        assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder);
    }

    @Test
    public void testItGetsDayOfWeekWhenMondayIsFirstDayOfWeek(){
        // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
        // Monday as first day means Sunday is last day of week
        // first index corresponds to Sunday and last is Saturday
        int[] expectedDaysOfWeekOrder = {6,0,1,2,3,4,5};
        int[] actualDaysOfWeekOrder = new int[7];
        Calendar calendar = Calendar.getInstance();
        underTest.setFirstDayOfWeek(Calendar.MONDAY);
        for (int day = 1; day <= 7 ; day++){
            calendar.set(Calendar.DAY_OF_WEEK, day);
            actualDaysOfWeekOrder[day - 1] = underTest.getDayOfWeek(calendar);
        }
        assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder);
    }

    @Test
    public void testItGetsDayOfWeekWhenTuesdayIsFirstDayOfWeek(){
        // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
        // Tuesday as first day means Monday is last day of week
        // first index corresponds to Sunday and last is Saturday
        int[] expectedDaysOfWeekOrder = {5,6,0,1,2,3,4};
        int[] actualDaysOfWeekOrder = new int[7];
        Calendar calendar = Calendar.getInstance();
        underTest.setFirstDayOfWeek(Calendar.TUESDAY);
        for (int day = 1; day <= 7 ; day++){
            calendar.set(Calendar.DAY_OF_WEEK, day);
            actualDaysOfWeekOrder[day - 1] = underTest.getDayOfWeek(calendar);
        }
        assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder);
    }

    @Test
    public void testItGetsDayOfWeekWhenWednesdayIsFirstDayOfWeek(){
        // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
        // Wednesday as first day means Tuesday is last day of week
        // first index corresponds to Sunday and last is Saturday
        int[] expectedDaysOfWeekOrder = {4,5,6,0,1,2,3};
        int[] actualDaysOfWeekOrder = new int[7];
        Calendar calendar = Calendar.getInstance();
        underTest.setFirstDayOfWeek(Calendar.WEDNESDAY);
        for (int day = 1; day <= 7 ; day++){
            calendar.set(Calendar.DAY_OF_WEEK, day);
            actualDaysOfWeekOrder[day - 1] = underTest.getDayOfWeek(calendar);
        }
        assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder);
    }

    @Test
    public void testItGetsDayOfWeekWhenThursdayIsFirstDayOfWeek(){
        // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
        // Thursday as first day means Wednesday is last day of week
        // first index corresponds to Sunday and last is Saturday
        int[] expectedDaysOfWeekOrder = {3,4,5,6,0,1,2};
        int[] actualDaysOfWeekOrder = new int[7];
        Calendar calendar = Calendar.getInstance();
        underTest.setFirstDayOfWeek(Calendar.THURSDAY);
        for (int day = 1; day <= 7 ; day++){
            calendar.set(Calendar.DAY_OF_WEEK, day);
            actualDaysOfWeekOrder[day - 1] = underTest.getDayOfWeek(calendar);
        }
        assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder);
    }

    @Test
    public void testItGetsDayOfWeekWhenFridayIsFirstDayOfWeek(){
        // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
        // Friday as first day means Wednesday is last day of week
        // first index corresponds to Sunday and last is Saturday
        int[] expectedDaysOfWeekOrder = {2,3,4,5,6,0,1};
        int[] actualDaysOfWeekOrder = new int[7];
        Calendar calendar = Calendar.getInstance();
        underTest.setFirstDayOfWeek(Calendar.FRIDAY);
        for (int day = 1; day <= 7 ; day++){
            calendar.set(Calendar.DAY_OF_WEEK, day);
            actualDaysOfWeekOrder[day - 1] = underTest.getDayOfWeek(calendar);
        }
        assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder);
    }

    @Test
    public void testItGetsDayOfWeekWhenSaturdayIsFirstDayOfWeek(){
        // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
        // Saturday as first day means Friday is last day of week
        // first index corresponds to Sunday and last is Saturday
        int[] expectedDaysOfWeekOrder = {1,2,3,4,5,6,0};
        int[] actualDaysOfWeekOrder = new int[7];
        Calendar calendar = Calendar.getInstance();
        underTest.setFirstDayOfWeek(Calendar.SATURDAY);
        for (int day = 1; day <= 7 ; day++){
            calendar.set(Calendar.DAY_OF_WEEK, day);
            actualDaysOfWeekOrder[day - 1] = underTest.getDayOfWeek(calendar);
        }
        assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder);
    }

    private long setTimeToMidnightAndGet(Calendar cal, long epoch) {
        cal.setTime(new Date(epoch));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
