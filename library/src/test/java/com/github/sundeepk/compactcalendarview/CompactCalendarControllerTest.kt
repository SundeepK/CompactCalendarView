package com.github.sundeepk.compactcalendarview

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.VelocityTracker
import android.widget.OverScroller
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.text.DateFormatSymbols
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class CompactCalendarControllerTest {
    @Mock
    private val paint: Paint? = null
    @Mock
    private val overScroller: OverScroller? = null
    @Mock
    private val canvas: Canvas? = null
    @Mock
    private val rect: Rect? = null
    @Mock
    private val calendar: Calendar? = null
    @Mock
    private val motionEvent: MotionEvent? = null
    @Mock
    private val velocityTracker: VelocityTracker? = null
    @Mock
    private val eventsContainer: EventsContainer? = null
    private var underTest: CompactCalendarController? = null
    @Before
    fun setUp() {
        Locale.setDefault(Locale.ENGLISH)
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
        Mockito.`when`(velocityTracker!!.xVelocity).thenReturn(-200f)
        underTest = CompactCalendarController(paint!!, overScroller!!, rect!!, null, null, 0, 0, 0, velocityTracker, 0, eventsContainer!!, Locale.getDefault(), TimeZone.getDefault())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testItThrowsWhenDayColumnsIsNotLengthSeven() {
        val dayNames = arrayOf<String?>("Mon", "Tue", "Wed", "Thur", "Fri")
        underTest!!.setDayColumnNames(dayNames)
    }

    @Test
    fun testManualScrollAndGestureScrollPlayNicelyTogether() { //Set width of view so that scrolling will return a correct value
        underTest!!.onMeasure(720, 1080, 0, 0)
        val cal = Calendar.getInstance()
        //Sun, 08 Feb 2015 00:00:00 GMT
        underTest!!.setCurrentDate(Date(setTimeToMidnightAndGet(cal, 1423353600000L)))
        underTest!!.scrollRight()
        //Sun, 01 Mar 2015 00:00:00 GMT - expected
        Assert.assertEquals(Date(setTimeToMidnightAndGet(cal, 1425168000000L)), underTest!!.firstDayOfCurrentMonth)
        Mockito.`when`(motionEvent!!.action).thenReturn(MotionEvent.ACTION_UP)
        //Scroll enough to push calender to next month
        underTest!!.onScroll(motionEvent, motionEvent, 600f, 0f)
        underTest!!.onDraw(canvas!!)
        underTest!!.onTouch(motionEvent)
        //Wed, 01 Apr 2015 00:00:00 GMT
        Assert.assertEquals(Date(setTimeToMidnightAndGet(cal, 1427846400000L)), underTest!!.firstDayOfCurrentMonth)
    }

    @Test
    fun testItScrollsToNextMonth() { //Sun, 08 Feb 2015 00:00:00 GMT
        underTest!!.setCurrentDate(Date(1423353600000L))
        underTest!!.scrollRight()
        val actualDate = underTest!!.firstDayOfCurrentMonth
        //Sun, 01 Mar 2015 00:00:00 GMT - expected
        Assert.assertEquals(Date(1425168000000L), actualDate)
    }

    @Test
    fun testItScrollsToPreviousMonth() { //Sun, 08 Feb 2015 00:00:00 GMT
        underTest!!.setCurrentDate(Date(1423353600000L))
        underTest!!.scrollLeft()
        val actualDate = underTest!!.firstDayOfCurrentMonth
        // Thu, 01 Jan 2015 00:00:00 GMT - expected
        Assert.assertEquals(Date(1420070400000L), actualDate)
    }

    @Test
    fun testItScrollsToNextMonthWhenRtl() { //Sun, 08 Feb 2015 00:00:00 GMT
        underTest!!.setCurrentDate(Date(1423353600000L))
        underTest!!.setIsRtl(true)
        underTest!!.scrollRight()
        val actualDate = underTest!!.firstDayOfCurrentMonth
        // Thu, 01 Jan 2015 00:00:00 GMT - expected
        Assert.assertEquals(Date(1420070400000L), actualDate)
    }

    @Test
    fun testItScrollsToPreviousMonthWhenRtl() { //Sun, 08 Feb 2015 00:00:00 GMT
        underTest!!.setCurrentDate(Date(1423353600000L))
        underTest!!.setIsRtl(true)
        underTest!!.scrollLeft()
        val actualDate = underTest!!.firstDayOfCurrentMonth
        //Sun, 01 Mar 2015 00:00:00 GMT - expected
        Assert.assertEquals(Date(1425168000000L), actualDate)
    }

    @Test
    fun testItSetsDayColumns() { //simulate Feb month
        Mockito.`when`(calendar!![Calendar.DAY_OF_WEEK]).thenReturn(1)
        Mockito.`when`(calendar[Calendar.MONTH]).thenReturn(1)
        Mockito.`when`(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).thenReturn(28)
        val dayNames = arrayOf<String?>("Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun")
        underTest!!.setGrowProgress(1000f) //set grow progress so that it simulates the calendar being open
        underTest!!.setDayColumnNames(dayNames)
        underTest!!.drawMonth(canvas!!, calendar, 0)
        val inOrder = Mockito.inOrder(canvas)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("Mon"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("Tue"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("Wed"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("Thur"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("Fri"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("Sat"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("Sun"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
    }

    @Test
    fun testListenerIsCalledOnMonthScroll() { //Sun, 01 Mar 2015 00:00:00 GMT
        val expectedDateOnScroll = Date(1425168000000L)
        Mockito.`when`(motionEvent!!.action).thenReturn(MotionEvent.ACTION_UP)
        //Set width of view so that scrolling will return a correct value
        underTest!!.onMeasure(720, 1080, 0, 0)
        //Sun, 08 Feb 2015 00:00:00 GMT
        underTest!!.setCurrentDate(Date(1423353600000L))
        //Scroll enough to push calender to next month
        underTest!!.onScroll(motionEvent, motionEvent, 600f, 0f)
        underTest!!.onDraw(canvas!!)
        underTest!!.onTouch(motionEvent)
        Assert.assertEquals(expectedDateOnScroll, underTest!!.firstDayOfCurrentMonth)
    }

    @Test
    fun testItAbbreviatesDayNames() { //simulate Feb month
        Mockito.`when`(calendar!![Calendar.DAY_OF_WEEK]).thenReturn(1)
        Mockito.`when`(calendar[Calendar.MONTH]).thenReturn(1)
        Mockito.`when`(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).thenReturn(28)
        underTest!!.setGrowProgress(1000f) //set grow progress so that it simulates the calendar being open
        underTest!!.setLocale(TimeZone.getTimeZone("Europe/Paris"), Locale.FRANCE)
        Mockito.reset(canvas) //reset because invalidate is called
        underTest!!.setUseWeekDayAbbreviation(true)
        Mockito.reset(canvas) //reset because invalidate is called
        underTest!!.drawMonth(canvas!!, calendar, 0)
        val dateFormatSymbols = DateFormatSymbols(Locale.FRANCE)
        val dayNames = dateFormatSymbols.shortWeekdays
        val inOrder = Mockito.inOrder(canvas)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq(dayNames[2]), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq(dayNames[3]), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq(dayNames[4]), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq(dayNames[5]), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq(dayNames[6]), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq(dayNames[7]), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq(dayNames[1]), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
    }

    @Test
    fun testItReturnsFirstDayOfMonthAfterDateHasBeenSet() { //Sun, 01 Feb 2015 00:00:00 GMT
        val expectedDate = Date(1422748800000L)
        //Sun, 08 Feb 2015 00:00:00 GMT
        underTest!!.setCurrentDate(Date(1423353600000L))
        val actualDate = underTest!!.firstDayOfCurrentMonth
        Assert.assertEquals(expectedDate, actualDate)
    }

    @Test
    fun testItReturnsFirstDayOfMonth() {
        val currentCalender = Calendar.getInstance()
        currentCalender[Calendar.DAY_OF_MONTH] = 1
        currentCalender[Calendar.HOUR_OF_DAY] = 0
        currentCalender[Calendar.MINUTE] = 0
        currentCalender[Calendar.SECOND] = 0
        currentCalender[Calendar.MILLISECOND] = 0
        val expectFirstDayOfMonth = currentCalender.time
        val actualDate = underTest!!.firstDayOfCurrentMonth
        Assert.assertEquals(expectFirstDayOfMonth, actualDate)
    }

    @Test
    fun testItDrawsSundayAsFirstDay() { //simulate Feb month
        Mockito.`when`(calendar!![Calendar.DAY_OF_WEEK]).thenReturn(1)
        Mockito.`when`(calendar[Calendar.MONTH]).thenReturn(1)
        Mockito.`when`(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).thenReturn(28)
        underTest!!.setGrowProgress(1000f) //set grow progress so that it simulates the calendar being open
        underTest!!.setUseWeekDayAbbreviation(true)
        underTest!!.setFirstDayOfWeek(Calendar.SUNDAY)
        underTest!!.drawMonth(canvas!!, calendar, 0)
        val inOrder = Mockito.inOrder(canvas)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("Sun"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("Mon"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("Tue"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("Wed"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("Thu"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("Fri"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("Sat"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
    }

    @Test
    fun testItDrawsFirstLetterOfEachDay() { //simulate Feb month
        Mockito.`when`(calendar!![Calendar.DAY_OF_WEEK]).thenReturn(1)
        Mockito.`when`(calendar[Calendar.MONTH]).thenReturn(1)
        Mockito.`when`(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).thenReturn(28)
        underTest!!.setGrowProgress(1000f) //set grow progress so that it simulates the calendar being open
        underTest!!.drawMonth(canvas!!, calendar, 0)
        val inOrder = Mockito.inOrder(canvas)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("M"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("T"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("W"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("T"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("F"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("S"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        inOrder.verify(canvas)!!.drawText(ArgumentMatchers.eq("S"), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
    }

    @Test
    fun testItDrawsDaysOnCalender() { //simulate Feb month
        underTest!!.setGrowProgress(1000f) //set grow progress so that it simulates the calendar being open
        Mockito.`when`(calendar!![Calendar.DAY_OF_WEEK]).thenReturn(1)
        Mockito.`when`(calendar[Calendar.MONTH]).thenReturn(1)
        Mockito.`when`(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).thenReturn(28)
        underTest!!.drawMonth(canvas!!, calendar, 0)
        var dayColumn = 0
        var dayRow = 0
        while (dayColumn <= 6) {
            if (dayRow == 7) {
                dayRow = 0
                if (dayColumn <= 6) {
                    dayColumn++
                }
            }
            if (dayColumn == dayColumnNames.size) {
                break
            }
            if (dayColumn == 0) {
                Mockito.verify(canvas)!!.drawText(ArgumentMatchers.eq(dayColumnNames[dayColumn]), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
            } else {
                val day = (dayRow - 1) * 7 + dayColumn + 1 - 6
                if (day in 1..28) {
                    Mockito.verify(canvas)!!.drawText(ArgumentMatchers.eq(day.toString()), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
                }
            }
            dayRow++
        }
    }

    @Test
    fun testItDrawsEventDaysOnCalendar() { //Sun, 07 Jun 2015 18:20:51 GMT
//get 30 events in total
        val numberOfDaysWithEvents = 30
        val events = CompactCalendarHelper.getEvents(0, numberOfDaysWithEvents, 1433701251000L)
        Mockito.`when`<List<Events?>?>(eventsContainer!!.getEventsForMonthAndYear(5, 2015)).thenReturn(events)
        Mockito.`when`(calendar!![Calendar.MONTH]).thenReturn(5)
        Mockito.`when`(calendar[Calendar.YEAR]).thenReturn(2015)
        underTest!!.shouldDrawIndicatorsBelowSelectedDays(true) // always draw events, even on current day
        underTest!!.setGrowProgress(1000f) //set grow progress so that it simulates the calendar being open
        underTest!!.drawEvents(canvas!!, calendar, 0)
        //draw events for every day with an event
        Mockito.verify(canvas, Mockito.times(numberOfDaysWithEvents))!!.drawCircle(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
    }

    @Test
    fun testItDrawsMultipleEventDaysOnCalendar() { //Sun, 07 Jun 2015 18:20:51 GMT
//get 60 events in total
        val numberOfDaysWithEvents = 30
        val events = CompactCalendarHelper.getDayEventWith2EventsPerDay(0, numberOfDaysWithEvents, 1433701251000L)
        Mockito.`when`<List<Events?>?>(eventsContainer!!.getEventsForMonthAndYear(5, 2015)).thenReturn(events)
        Mockito.`when`(calendar!![Calendar.MONTH]).thenReturn(5)
        Mockito.`when`(calendar[Calendar.YEAR]).thenReturn(2015)
        underTest!!.shouldDrawIndicatorsBelowSelectedDays(true) // always draw events, even on current day
        underTest!!.setGrowProgress(1000f) //set grow progress so that it simulates the calendar being open
        underTest!!.drawEvents(canvas!!, calendar, 0)
        //draw 2 events per day
        Mockito.verify(canvas, Mockito.times(numberOfDaysWithEvents * 2))!!.drawCircle(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
    }

    @Test
    fun testItDrawsMultipleEventDaysOnCalendarWithPlusIndicator() { //Sun, 07 Jun 2015 18:20:51 GMT
//get 120 events in total but only draw 3 event indicators per a day
        val numberOfDaysWithEvents = 30
        val events = CompactCalendarHelper.getDayEventWithMultipleEventsPerDay(0, numberOfDaysWithEvents, 1433701251000L)
        Mockito.`when`<List<Events?>?>(eventsContainer!!.getEventsForMonthAndYear(5, 2015)).thenReturn(events)
        Mockito.`when`(calendar!![Calendar.MONTH]).thenReturn(5)
        Mockito.`when`(calendar[Calendar.YEAR]).thenReturn(2015)
        underTest!!.shouldDrawIndicatorsBelowSelectedDays(true) // always draw events, even on current day
        underTest!!.setGrowProgress(1000f) //set grow progress so that it simulates the calendar being open
        underTest!!.drawEvents(canvas!!, calendar, 0)
        //draw 2 events per day because we don't draw more than 3 indicators
        Mockito.verify(canvas, Mockito.times(numberOfDaysWithEvents * 2))!!.drawCircle(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
        //draw event indicator with lines
// 2 calls for each plus event indicator since it takes 2 draw calls to make a plus sign
        Mockito.verify(canvas, Mockito.times(numberOfDaysWithEvents * 2))!!.drawLine(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
    }

    @Test
    fun testItDrawsEventDaysOnCalendarForCurrentMonth() {
        val todayCalendar = Calendar.getInstance()
        val numberOfDaysInMonth = todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val todayMonth = todayCalendar[Calendar.MONTH]
        val todayYear = todayCalendar[Calendar.YEAR]
        //get events for every day in the month
        val events = CompactCalendarHelper.getEvents(0, numberOfDaysInMonth, todayCalendar.timeInMillis)
        Mockito.`when`<List<Events?>?>(eventsContainer!!.getEventsForMonthAndYear(todayMonth, todayYear)).thenReturn(events)
        Mockito.`when`(calendar!![Calendar.MONTH]).thenReturn(todayMonth)
        Mockito.`when`(calendar[Calendar.YEAR]).thenReturn(todayYear)
        underTest!!.setGrowProgress(1000f) //set grow progress so that it simulates the calendar being open
        underTest!!.drawEvents(canvas!!, calendar, 0)
        //draw events for every day except the current day -- selected day is also the current day
        Mockito.verify(canvas, Mockito.times(numberOfDaysInMonth - 1))!!.drawCircle(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
    }

    @Test
    fun testItDrawsEventDaysOnCalendarWithSelectedDay() { //Sun, 07 Jun 2015 18:20:51 GMT
        val selectedDayTimestamp = 1433701251000L
        //get 30 events in total
        val numberOfDaysWithEvents = 30
        val events = CompactCalendarHelper.getEvents(0, numberOfDaysWithEvents, selectedDayTimestamp)
        Mockito.`when`<List<Events?>?>(eventsContainer!!.getEventsForMonthAndYear(5, 2015)).thenReturn(events)
        Mockito.`when`(calendar!![Calendar.MONTH]).thenReturn(5)
        Mockito.`when`(calendar[Calendar.YEAR]).thenReturn(2015)
        underTest!!.setGrowProgress(1000f) //set grow progress so that it simulates the calendar being open
        // Selects first day of the month
        underTest!!.setCurrentDate(Date(selectedDayTimestamp))
        underTest!!.drawEvents(canvas!!, calendar, 0)
        //draw events for every day except the selected day
        Mockito.verify(canvas, Mockito.times(numberOfDaysWithEvents - 1))!!.drawCircle(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
    }

    @Test
    fun testItDrawsEventDaysOnCalendarForCurrentMonthWithSelectedDay() {
        val todayCalendar = Calendar.getInstance()
        val numberOfDaysInMonth = todayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val todayMonth = todayCalendar[Calendar.MONTH]
        val todayYear = todayCalendar[Calendar.YEAR]
        //get events for every day in the month
        val events = CompactCalendarHelper.getEvents(0, numberOfDaysInMonth, todayCalendar.timeInMillis)
        Mockito.`when`<List<Events?>?>(eventsContainer!!.getEventsForMonthAndYear(todayMonth, todayYear)).thenReturn(events)
        Mockito.`when`(calendar!![Calendar.MONTH]).thenReturn(todayMonth)
        Mockito.`when`(calendar[Calendar.YEAR]).thenReturn(todayYear)
        // sets either 1st day or 2nd day so that there are always 2 days selected
        val dayOfMonth = todayCalendar[Calendar.DAY_OF_MONTH]
        if (dayOfMonth == 1) {
            todayCalendar[Calendar.DAY_OF_MONTH] = 2
        } else {
            todayCalendar[Calendar.DAY_OF_MONTH] = 1
        }
        todayCalendar[Calendar.HOUR_OF_DAY] = 0
        todayCalendar[Calendar.MINUTE] = 0
        todayCalendar[Calendar.SECOND] = 0
        todayCalendar[Calendar.MILLISECOND] = 0
        underTest!!.setCurrentDate(todayCalendar.time)
        underTest!!.setGrowProgress(1000f) //set grow progress so that it simulates the calendar being open
        underTest!!.drawEvents(canvas!!, calendar, 0)
        //draw events for every day except the current day and the selected day
        Mockito.verify(canvas, Mockito.times(numberOfDaysInMonth - 2))!!.drawCircle(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(paint)!!)
    }

    @Test
    fun testItAddsEvent() {
        val event = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1433701251000L)[0]
        underTest!!.addEvent(event)
        Mockito.verify(eventsContainer)!!.addEvent(event)
        Mockito.verifyNoMoreInteractions(eventsContainer)
    }

    @Test
    fun testItAddsEvents() {
        val events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1433701251000L)
        underTest!!.addEvents(events)
        Mockito.verify(eventsContainer)!!.addEvents(events)
        Mockito.verifyNoMoreInteractions(eventsContainer)
    }

    @Test
    fun testItRemovesEvent() {
        val event = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1433701251000L)[0]
        underTest!!.removeEvent(event)
        Mockito.verify(eventsContainer)!!.removeEvent(event)
        Mockito.verifyNoMoreInteractions(eventsContainer)
    }

    @Test
    fun testItRemovesEvents() {
        val events = CompactCalendarHelper.getOneEventPerDayForMonth(0, 30, 1433701251000L)
        underTest!!.removeEvents(events)
        Mockito.verify(eventsContainer)!!.removeEvents(events)
        Mockito.verifyNoMoreInteractions(eventsContainer)
    }

    @Test
    fun testItGetCalendarEventsForADate() {
        underTest!!.getCalendarEventsFor(1433701251000L)
        Mockito.verify(eventsContainer)!!.getEventsFor(1433701251000L)
        Mockito.verifyNoMoreInteractions(eventsContainer)
    }

    @Test
    fun testItRemovesCalendarEventsForADate() {
        underTest!!.removeEventsFor(1433701251000L)
        Mockito.verify(eventsContainer)!!.removeEventByEpochMillis(1433701251000L)
        Mockito.verifyNoMoreInteractions(eventsContainer)
    }

    @Test
    fun testItRemovesAllEvents() {
        underTest!!.removeAllEvents()
        Mockito.verify(eventsContainer)!!.removeAllEvents()
        Mockito.verifyNoMoreInteractions(eventsContainer)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testItThrowsWhenZeroIsUsedAsFirstDayOfWeek() {
        underTest!!.setFirstDayOfWeek(0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testItThrowsWhenValuesGreaterThanSevenIsUsedAsFirstDayOfWeek() {
        underTest!!.setFirstDayOfWeek(8)
    }

    @Test
    fun testItGetsDayOfWeekWhenSundayIsFirstDayOfWeek() { // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
// Sunday as first day means Saturday is last day of week
// first index corresponds to Sunday and last is Saturday
        val expectedDaysOfWeekOrder = intArrayOf(0, 1, 2, 3, 4, 5, 6)
        val actualDaysOfWeekOrder = IntArray(7)
        val calendar = Calendar.getInstance()
        underTest!!.setFirstDayOfWeek(Calendar.SUNDAY)
        for (day in 1..7) {
            calendar[Calendar.DAY_OF_WEEK] = day
            actualDaysOfWeekOrder[day - 1] = underTest!!.getDayOfWeek(calendar)
        }
        Assert.assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder)
    }

    @Test
    fun testItGetsDayOfWeekWhenMondayIsFirstDayOfWeek() { // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
// Monday as first day means Sunday is last day of week
// first index corresponds to Sunday and last is Saturday
        val expectedDaysOfWeekOrder = intArrayOf(6, 0, 1, 2, 3, 4, 5)
        val actualDaysOfWeekOrder = IntArray(7)
        val calendar = Calendar.getInstance()
        underTest!!.setFirstDayOfWeek(Calendar.MONDAY)
        for (day in 1..7) {
            calendar[Calendar.DAY_OF_WEEK] = day
            actualDaysOfWeekOrder[day - 1] = underTest!!.getDayOfWeek(calendar)
        }
        Assert.assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder)
    }

    @Test
    fun testItGetsDayOfWeekWhenTuesdayIsFirstDayOfWeek() { // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
// Tuesday as first day means Monday is last day of week
// first index corresponds to Sunday and last is Saturday
        val expectedDaysOfWeekOrder = intArrayOf(5, 6, 0, 1, 2, 3, 4)
        val actualDaysOfWeekOrder = IntArray(7)
        val calendar = Calendar.getInstance()
        underTest!!.setFirstDayOfWeek(Calendar.TUESDAY)
        for (day in 1..7) {
            calendar[Calendar.DAY_OF_WEEK] = day
            actualDaysOfWeekOrder[day - 1] = underTest!!.getDayOfWeek(calendar)
        }
        Assert.assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder)
    }

    @Test
    fun testItGetsDayOfWeekWhenWednesdayIsFirstDayOfWeek() { // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
// Wednesday as first day means Tuesday is last day of week
// first index corresponds to Sunday and last is Saturday
        val expectedDaysOfWeekOrder = intArrayOf(4, 5, 6, 0, 1, 2, 3)
        val actualDaysOfWeekOrder = IntArray(7)
        val calendar = Calendar.getInstance()
        underTest!!.setFirstDayOfWeek(Calendar.WEDNESDAY)
        for (day in 1..7) {
            calendar[Calendar.DAY_OF_WEEK] = day
            actualDaysOfWeekOrder[day - 1] = underTest!!.getDayOfWeek(calendar)
        }
        Assert.assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder)
    }

    @Test
    fun testItGetsDayOfWeekWhenThursdayIsFirstDayOfWeek() { // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
// Thursday as first day means Wednesday is last day of week
// first index corresponds to Sunday and last is Saturday
        val expectedDaysOfWeekOrder = intArrayOf(3, 4, 5, 6, 0, 1, 2)
        val actualDaysOfWeekOrder = IntArray(7)
        val calendar = Calendar.getInstance()
        underTest!!.setFirstDayOfWeek(Calendar.THURSDAY)
        for (day in 1..7) {
            calendar[Calendar.DAY_OF_WEEK] = day
            actualDaysOfWeekOrder[day - 1] = underTest!!.getDayOfWeek(calendar)
        }
        Assert.assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder)
    }

    @Test
    fun testItGetsDayOfWeekWhenFridayIsFirstDayOfWeek() { // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
// Friday as first day means Wednesday is last day of week
// first index corresponds to Sunday and last is Saturday
        val expectedDaysOfWeekOrder = intArrayOf(2, 3, 4, 5, 6, 0, 1)
        val actualDaysOfWeekOrder = IntArray(7)
        val calendar = Calendar.getInstance()
        underTest!!.setFirstDayOfWeek(Calendar.FRIDAY)
        for (day in 1..7) {
            calendar[Calendar.DAY_OF_WEEK] = day
            actualDaysOfWeekOrder[day - 1] = underTest!!.getDayOfWeek(calendar)
        }
        Assert.assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder)
    }

    @Test
    fun testItGetsDayOfWeekWhenSaturdayIsFirstDayOfWeek() { // zero based indexes used internally so instead of returning range of 1-7 it returns 0-6
// Saturday as first day means Friday is last day of week
// first index corresponds to Sunday and last is Saturday
        val expectedDaysOfWeekOrder = intArrayOf(1, 2, 3, 4, 5, 6, 0)
        val actualDaysOfWeekOrder = IntArray(7)
        val calendar = Calendar.getInstance()
        underTest!!.setFirstDayOfWeek(Calendar.SATURDAY)
        for (day in 1..7) {
            calendar[Calendar.DAY_OF_WEEK] = day
            actualDaysOfWeekOrder[day - 1] = underTest!!.getDayOfWeek(calendar)
        }
        Assert.assertArrayEquals(expectedDaysOfWeekOrder, actualDaysOfWeekOrder)
    }

    private fun setTimeToMidnightAndGet(cal: Calendar, epoch: Long): Long {
        cal.time = Date(epoch)
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MILLISECOND] = 0
        return cal.timeInMillis
    }

    companion object {
        private val dayColumnNames = arrayOf("M", "T", "W", "T", "F", "S", "S")
    }
}
