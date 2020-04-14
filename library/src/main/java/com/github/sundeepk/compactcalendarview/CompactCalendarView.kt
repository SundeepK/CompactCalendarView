package com.github.sundeepk.compactcalendarview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.OverScroller
import androidx.core.view.GestureDetectorCompat
import com.github.sundeepk.compactcalendarview.domain.Event
import java.util.*
import kotlin.math.abs

class CompactCalendarView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private val animationHandler: AnimationHandler
    private val compactCalendarController: CompactCalendarController = CompactCalendarController(Paint(), OverScroller(getContext()),
            Rect(), attrs, getContext(), Color.argb(255, 233, 84, 81),
            Color.argb(255, 64, 64, 64), Color.argb(255, 219, 219, 219), VelocityTracker.obtain(),
            Color.argb(255, 100, 68, 65), EventsContainer(Calendar.getInstance()),
            Locale.getDefault(), TimeZone.getDefault())
    private val gestureDetector: GestureDetectorCompat
    private var horizontalScrollEnabled = true

    interface CompactCalendarViewListener {
        fun onDayClick(dateClicked: Date?)
        fun onMonthScroll(firstDayOfNewMonth: Date?)
    }

    interface CompactCalendarAnimationListener {
        fun onOpened()
        fun onClosed()
    }

    private val gestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent) {}
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            compactCalendarController.onSingleTapUp(e)
            invalidate()
            return super.onSingleTapUp(e)
        }

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if (horizontalScrollEnabled) {
                if (abs(distanceX) > 0) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    compactCalendarController.onScroll(e1, e2, distanceX, distanceY)
                    invalidate()
                    return true
                }
            }
            return false
        }
    }

    fun setAnimationListener(compactCalendarAnimationListener: CompactCalendarAnimationListener?) {
        animationHandler.setCompactCalendarAnimationListener(compactCalendarAnimationListener)
    }

    /*
    Use a custom locale for compact calendar and reinitialise the view.
     */
    fun setLocale(timeZone: TimeZone?, locale: Locale?) {
        compactCalendarController.setLocale(timeZone, locale)
        invalidate()
    }

    /*
    Compact calendar will use the locale to determine the abbreviation to use as the day column names.
    The default is to use the default locale and to abbreviate the day names to one character.
    Setting this to true will displace the short weekday string provided by java.
     */
    fun setUseThreeLetterAbbreviation(useThreeLetterAbbreviation: Boolean) {
        compactCalendarController.setUseWeekDayAbbreviation(useThreeLetterAbbreviation)
        invalidate()
    }

    fun setCalendarBackgroundColor(calenderBackgroundColor: Int) {
        compactCalendarController.setCalenderBackgroundColor(calenderBackgroundColor)
        invalidate()
    }

    /*
    Sets the name for each day of the week. No attempt is made to adjust width or text size based on the length of each day name.
    Works best with 3-4 characters for each day.
     */
    fun setDayColumnNames(dayColumnNames: Array<String?>?) {
        compactCalendarController.setDayColumnNames(dayColumnNames)
    }

    fun setFirstDayOfWeek(dayOfWeek: Int) {
        compactCalendarController.setFirstDayOfWeek(dayOfWeek)
        invalidate()
    }

    fun setCurrentSelectedDayBackgroundColor(currentSelectedDayBackgroundColor: Int) {
        compactCalendarController.setCurrentSelectedDayBackgroundColor(currentSelectedDayBackgroundColor)
        invalidate()
    }

    fun setCurrentDayBackgroundColor(currentDayBackgroundColor: Int) {
        compactCalendarController.setCurrentDayBackgroundColor(currentDayBackgroundColor)
        invalidate()
    }

    val heightPerDay: Int
        get() = compactCalendarController.heightPerDay

    fun setListener(listener: CompactCalendarViewListener?) {
        compactCalendarController.setListener(listener)
    }

    val firstDayOfCurrentMonth: Date
        get() = compactCalendarController.firstDayOfCurrentMonth

    fun shouldDrawIndicatorsBelowSelectedDays(shouldDrawIndicatorsBelowSelectedDays: Boolean) {
        compactCalendarController.shouldDrawIndicatorsBelowSelectedDays(shouldDrawIndicatorsBelowSelectedDays)
    }

    fun setCurrentDate(dateTimeMonth: Date) {
        compactCalendarController.setCurrentDate(dateTimeMonth)
        invalidate()
    }

    val weekNumberForCurrentMonth: Int
        get() = compactCalendarController.weekNumberForCurrentMonth

    fun setShouldDrawDaysHeader(shouldDrawDaysHeader: Boolean) {
        compactCalendarController.setShouldDrawDaysHeader(shouldDrawDaysHeader)
    }

    fun setCurrentSelectedDayTextColor(currentSelectedDayTextColor: Int) {
        compactCalendarController.setCurrentSelectedDayTextColor(currentSelectedDayTextColor)
    }

    fun setCurrentDayTextColor(currentDayTextColor: Int) {
        compactCalendarController.setCurrentDayTextColor(currentDayTextColor)
    }
    /**
     * Adds an event to be drawn as an indicator in the calendar.
     * If adding multiple events see [.addEvents]} method.
     * @param event to be added to the calendar
     * @param shouldInvalidate true if the view should invalidate
     *
     * see [.addEvent] when adding single events to control if calendar should redraw
     * or [.addEvents]  when adding multiple events
     * @param event
     */
    @JvmOverloads
    fun addEvent(event: Event, shouldInvalidate: Boolean = true) {
        compactCalendarController.addEvent(event)
        if (shouldInvalidate) {
            invalidate()
        }
    }

    /**
     * Adds multiple events to the calendar and invalidates the view once all events are added.
     */
    fun addEvents(events: List<Event>) {
        compactCalendarController.addEvents(events)
        invalidate()
    }

    /**
     * Fetches the events for the date passed in
     * @param date
     * @return
     */
    fun getEvents(date: Date): List<Event?>? {
        return compactCalendarController.getCalendarEventsFor(date.time)
    }

    /**
     * Fetches the events for the epochMillis passed in
     * @param epochMillis
     * @return
     */
    fun getEvents(epochMillis: Long): List<Event?>? {
        return compactCalendarController.getCalendarEventsFor(epochMillis)
    }

    /**
     * Fetches the events for the month of the epochMillis passed in and returns a sorted list of events
     * @param epochMillis
     * @return
     */
    fun getEventsForMonth(epochMillis: Long): List<Event?>? {
        return compactCalendarController.getCalendarEventsForMonth(epochMillis)
    }

    /**
     * Fetches the events for the month of the date passed in and returns a sorted list of events
     * @param date
     * @return
     */
    fun getEventsForMonth(date: Date): List<Event> {
        return compactCalendarController.getCalendarEventsForMonth(date.time)
    }

    /**
     * Remove the event associated with the Date passed in
     * @param date
     */
    fun removeEvents(date: Date) {
        compactCalendarController.removeEventsFor(date.time)
    }

    fun removeEvents(epochMillis: Long) {
        compactCalendarController.removeEventsFor(epochMillis)
    }
    /**
     * Removes an event from the calendar.
     * If removing multiple events see [.removeEvents]
     *
     * @param event event to remove from the calendar
     * @param shouldInvalidate true if the view should invalidate
     *
     * see [.removeEvent] when removing single events to control if calendar should redraw
     * or [.removeEvents] (java.util.List)}  when removing multiple events
     * @param event
     */
    @JvmOverloads
    fun removeEvent(event: Event, shouldInvalidate: Boolean = true) {
        compactCalendarController.removeEvent(event)
        if (shouldInvalidate) {
            invalidate()
        }
    }

    /**
     * Removes multiple events from the calendar and invalidates the view once all events are added.
     */
    fun removeEvents(events: List<Event>) {
        compactCalendarController.removeEvents(events)
        invalidate()
    }

    /**
     * Clears all Events from the calendar.
     */
    fun removeAllEvents() {
        compactCalendarController.removeAllEvents()
        invalidate()
    }

    fun setIsRtl(isRtl: Boolean) {
        compactCalendarController.setIsRtl(isRtl)
    }

    fun shouldSelectFirstDayOfMonthOnScroll(shouldSelectFirstDayOfMonthOnScroll: Boolean) {
        compactCalendarController.setShouldSelectFirstDayOfMonthOnScroll(shouldSelectFirstDayOfMonthOnScroll)
    }

    fun setCurrentSelectedDayIndicatorStyle(currentSelectedDayIndicatorStyle: Int) {
        compactCalendarController.setCurrentSelectedDayIndicatorStyle(currentSelectedDayIndicatorStyle)
        invalidate()
    }

    fun setCurrentDayIndicatorStyle(currentDayIndicatorStyle: Int) {
        compactCalendarController.setCurrentDayIndicatorStyle(currentDayIndicatorStyle)
        invalidate()
    }

    fun setEventIndicatorStyle(eventIndicatorStyle: Int) {
        compactCalendarController.setEventIndicatorStyle(eventIndicatorStyle)
        invalidate()
    }

    private fun checkTargetHeight() {
        check(compactCalendarController.targetHeight > 0) { "Target height must be set in xml properties in order to expand/collapse CompactCalendar." }
    }

    fun displayOtherMonthDays(displayOtherMonthDays: Boolean) {
        compactCalendarController.setDisplayOtherMonthDays(displayOtherMonthDays)
        invalidate()
    }

    fun setTargetHeight(targetHeight: Int) {
        compactCalendarController.targetHeight = targetHeight
        checkTargetHeight()
    }

    fun showCalendar() {
        checkTargetHeight()
        animationHandler.openCalendar()
    }

    fun hideCalendar() {
        checkTargetHeight()
        animationHandler.closeCalendar()
    }

    fun showCalendarWithAnimation() {
        checkTargetHeight()
        animationHandler.openCalendarWithAnimation()
    }

    fun hideCalendarWithAnimation() {
        checkTargetHeight()
        animationHandler.closeCalendarWithAnimation()
    }

    /**
     * Moves the calendar to the right. This will show the next month when [.setIsRtl]
     * is set to false. If in rtl mode, it will show the previous month.
     */
    fun scrollRight() {
        compactCalendarController.scrollRight()
        invalidate()
    }

    /**
     * Moves the calendar to the left. This will show the previous month when [.setIsRtl]
     * is set to false. If in rtl mode, it will show the next month.
     */
    fun scrollLeft() {
        compactCalendarController.scrollLeft()
        invalidate()
    }

    val isAnimating: Boolean
        get() = animationHandler.isAnimating

    override fun onMeasure(parentWidth: Int, parentHeight: Int) {
        super.onMeasure(parentWidth, parentHeight)
        val width = MeasureSpec.getSize(parentWidth)
        val height = MeasureSpec.getSize(parentHeight)
        if (width > 0 && height > 0) {
            compactCalendarController.onMeasure(width, height, paddingRight, paddingLeft)
        }
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        compactCalendarController.onDraw(canvas)
    }

    override fun computeScroll() {
        super.computeScroll()
        if (compactCalendarController.computeScroll()) {
            invalidate()
        }
    }

    fun shouldScrollMonth(enableHorizontalScroll: Boolean) {
        horizontalScrollEnabled = enableHorizontalScroll
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (horizontalScrollEnabled) {
            compactCalendarController.onTouch(event)
            invalidate()
        }
        // on touch action finished (CANCEL or UP), we re-allow the parent container to intercept touch events (scroll inside ViewPager + RecyclerView issue #82)
        if ((event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP) && horizontalScrollEnabled) {
            parent.requestDisallowInterceptTouchEvent(false)
        }
        // always allow gestureDetector to detect onSingleTap and scroll events
        return gestureDetector.onTouchEvent(event)
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        return if (this.visibility == GONE) {
            false
        } else horizontalScrollEnabled
        // Prevents ViewPager from scrolling horizontally by announcing that (issue #82)
    }

    companion object {
        const val FILL_LARGE_INDICATOR = 1
        const val NO_FILL_LARGE_INDICATOR = 2
        const val SMALL_INDICATOR = 3
    }

    init {
        gestureDetector = GestureDetectorCompat(getContext(), gestureListener)
        animationHandler = AnimationHandler(compactCalendarController, this)
    }
}
