package com.github.sundeepk.compactcalendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.OverScroller;

import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CompactCalendarView extends View {

    public static final int FILL_LARGE_INDICATOR = 1;
    public static final int NO_FILL_LARGE_INDICATOR = 2;
    public static final int SMALL_INDICATOR = 3;

    private final AnimationHandler animationHandler;
    private CompactCalendarController compactCalendarController;
    private GestureDetectorCompat gestureDetector;
    private boolean horizontalScrollEnabled = true;

    public interface CompactCalendarViewListener {
        public void onDayClick(Date dateClicked);
        public void onMonthScroll(Date firstDayOfNewMonth);
    }

    public interface CompactCalendarAnimationListener {
        public void onOpened();
        public void onClosed();
    }

    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            compactCalendarController.onSingleTapUp(e);
            invalidate();
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(horizontalScrollEnabled) {
                if (Math.abs(distanceX) > 0) {
                    getParent().requestDisallowInterceptTouchEvent(true);

                    compactCalendarController.onScroll(e1, e2, distanceX, distanceY);
                    invalidate();
                    return true;
                }
            }

            return false;
        }
    };

    public CompactCalendarView(Context context) {
        this(context, null);
    }

    public CompactCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompactCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        compactCalendarController = new CompactCalendarController(new Paint(), new OverScroller(getContext()),
                new Rect(), attrs, getContext(),  Color.argb(255, 233, 84, 81),
                Color.argb(255, 64, 64, 64), Color.argb(255, 219, 219, 219), VelocityTracker.obtain(),
                Color.argb(255, 100, 68, 65), new EventsContainer(Calendar.getInstance()),
                Locale.getDefault(), TimeZone.getDefault());
        gestureDetector = new GestureDetectorCompat(getContext(), gestureListener);
        animationHandler = new AnimationHandler(compactCalendarController, this);
    }

    public void setAnimationListener(CompactCalendarAnimationListener compactCalendarAnimationListener){
        animationHandler.setCompactCalendarAnimationListener(compactCalendarAnimationListener);
    }

    /*
    Use a custom locale for compact calendar and reinitialise the view.
     */
    public void setLocale(TimeZone timeZone, Locale locale){
        compactCalendarController.setLocale(timeZone, locale);
        invalidate();
    }

    /*
    Compact calendar will use the locale to determine the abbreviation to use as the day column names.
    The default is to use the default locale and to abbreviate the day names to one character.
    Setting this to true will displace the short weekday string provided by java.
     */
    public void setUseThreeLetterAbbreviation(boolean useThreeLetterAbbreviation){
        compactCalendarController.setUseWeekDayAbbreviation(useThreeLetterAbbreviation);
        invalidate();
    }

    public void setCalendarBackgroundColor(final int calenderBackgroundColor) {
        compactCalendarController.setCalenderBackgroundColor(calenderBackgroundColor);
        invalidate();
    }

    /*
    Sets the name for each day of the week. No attempt is made to adjust width or text size based on the length of each day name.
    Works best with 3-4 characters for each day.
     */
    public void setDayColumnNames(String[] dayColumnNames){
        compactCalendarController.setDayColumnNames(dayColumnNames);
    }

    public void setFirstDayOfWeek(int dayOfWeek){
        compactCalendarController.setFirstDayOfWeek(dayOfWeek);
        invalidate();
    }

    public void setCurrentSelectedDayBackgroundColor(int currentSelectedDayBackgroundColor) {
        compactCalendarController.setCurrentSelectedDayBackgroundColor(currentSelectedDayBackgroundColor);
        invalidate();
    }

    public void setCurrentDayBackgroundColor(int currentDayBackgroundColor) {
        compactCalendarController.setCurrentDayBackgroundColor(currentDayBackgroundColor);
        invalidate();
    }

    public int getHeightPerDay(){
        return compactCalendarController.getHeightPerDay();
    }

    public void setListener(CompactCalendarViewListener listener){
        compactCalendarController.setListener(listener);
    }

    public Date getFirstDayOfCurrentMonth(){
        return compactCalendarController.getFirstDayOfCurrentMonth();
    }

    public void shouldDrawIndicatorsBelowSelectedDays(boolean shouldDrawIndicatorsBelowSelectedDays){
        compactCalendarController.shouldDrawIndicatorsBelowSelectedDays(shouldDrawIndicatorsBelowSelectedDays);
    }

    public void setCurrentDate(Date dateTimeMonth){
        compactCalendarController.setCurrentDate(dateTimeMonth);
        invalidate();
    }

    public int getWeekNumberForCurrentMonth(){
        return compactCalendarController.getWeekNumberForCurrentMonth();
    }

    public void setShouldDrawDaysHeader(boolean shouldDrawDaysHeader){
        compactCalendarController.setShouldDrawDaysHeader(shouldDrawDaysHeader);
    }

    public void setCurrentSelectedDayTextColor(int currentSelectedDayTextColor) {
        compactCalendarController.setCurrentSelectedDayTextColor(currentSelectedDayTextColor);
    }

    public void setCurrentDayTextColor(int currentDayTextColor) {
        compactCalendarController.setCurrentDayTextColor(currentDayTextColor);
    }

    /**
     * see {@link #addEvent(Event, boolean)} when adding single events
     * or {@link #addEvents(java.util.List)}  when adding multiple events
     * @param event
     */
    @Deprecated
    public void addEvent(Event event){
        addEvent(event, false);
    }

    /**
     *  Adds an event to be drawn as an indicator in the calendar.
     *  If adding multiple events see {@link #addEvents(List)}} method.
     * @param event to be added to the calendar
     * @param shouldInvalidate true if the view should invalidate
     */
    public void addEvent(Event event, boolean shouldInvalidate){
        compactCalendarController.addEvent(event);
        if(shouldInvalidate){
            invalidate();
        }
    }

    /**
     * Adds multiple events to the calendar and invalidates the view once all events are added.
     */
    public void addEvents(List<Event> events){
        compactCalendarController.addEvents(events);
        invalidate();
    }

    /**
     * Fetches the events for the date passed in
     * @param date
     * @return
     */
    public List<Event> getEvents(Date date){
        return compactCalendarController.getCalendarEventsFor(date.getTime());
    }

    /**
     * Fetches the events for the epochMillis passed in
     * @param epochMillis
     * @return
     */
    public List<Event> getEvents(long epochMillis){
        return compactCalendarController.getCalendarEventsFor(epochMillis);
    }

    /**
     * Fetches the events for the month of the epochMillis passed in and returns a sorted list of events
     * @param epochMillis
     * @return
     */
    public List<Event> getEventsForMonth(long epochMillis){
        return compactCalendarController.getCalendarEventsForMonth(epochMillis);
    }

    /**
     * Fetches the events for the month of the date passed in and returns a sorted list of events
     * @param date
     * @return
     */
    public List<Event> getEventsForMonth(Date date){
        return compactCalendarController.getCalendarEventsForMonth(date.getTime());
    }

    /**
     * Remove the event associated with the Date passed in
     * @param date
     */
    public void removeEvents(Date date){
        compactCalendarController.removeEventsFor(date.getTime());
    }

    public void removeEvents(long epochMillis){
        compactCalendarController.removeEventsFor(epochMillis);
    }

    /**
     * see {@link #removeEvent(Event, boolean)} when removing single events
     * or {@link #removeEvents(java.util.List)} (java.util.List)}  when removing multiple events
     * @param event
     */
    @Deprecated
    public void removeEvent(Event event){
        removeEvent(event, false);
    }

    /**
     * Removes an event from the calendar.
     * If removing multiple events see {@link #removeEvents(List)}
     *
     * @param event event to remove from the calendar
     * @param shouldInvalidate true if the view should invalidate
     */
    public void removeEvent(Event event, boolean shouldInvalidate){
        compactCalendarController.removeEvent(event);
        if(shouldInvalidate){
            invalidate();
        }
    }

    /**
     * Removes multiple events from the calendar and invalidates the view once all events are added.
     */
    public void removeEvents(List<Event> events){
        compactCalendarController.removeEvents(events);
        invalidate();
    }

    /**
     * Clears all Events from the calendar.
     */
    public void removeAllEvents() {
        compactCalendarController.removeAllEvents();
        invalidate();
    }

    public void shouldSelectFirstDayOfMonthOnScroll(boolean shouldSelectFirstDayOfMonthOnScroll){
        compactCalendarController.setShouldSelectFirstDayOfMonthOnScroll(shouldSelectFirstDayOfMonthOnScroll);
    }

    public void setCurrentSelectedDayIndicatorStyle(final int currentSelectedDayIndicatorStyle){
        compactCalendarController.setCurrentSelectedDayIndicatorStyle(currentSelectedDayIndicatorStyle);
        invalidate();
    }

    public void setCurrentDayIndicatorStyle(final int currentDayIndicatorStyle){
        compactCalendarController.setCurrentDayIndicatorStyle(currentDayIndicatorStyle);
        invalidate();
    }

    public void setEventIndicatorStyle(final int eventIndicatorStyle){
        compactCalendarController.setEventIndicatorStyle(eventIndicatorStyle);
        invalidate();
    }

    private void checkTargetHeight() {
        if (compactCalendarController.getTargetHeight() <= 0) {
            throw new IllegalStateException("Target height must be set in xml properties in order to expand/collapse CompactCalendar.");
        }
    }

    public void displayOtherMonthDays(boolean displayOtherMonthDays){
        compactCalendarController.setDisplayOtherMonthDays(displayOtherMonthDays);
        invalidate();
    }

    public void setTargetHeight(int targetHeight){
        compactCalendarController.setTargetHeight(targetHeight);
        checkTargetHeight();
    }

    public void showCalendar(){
        checkTargetHeight();
        animationHandler.openCalendar();
    }

    public void hideCalendar(){
        checkTargetHeight();
        animationHandler.closeCalendar();
    }

    public void showCalendarWithAnimation(){
        checkTargetHeight();
        animationHandler.openCalendarWithAnimation();
    }

    public void hideCalendarWithAnimation(){
        checkTargetHeight();
        animationHandler.closeCalendarWithAnimation();
    }

    public void showNextMonth(){
        compactCalendarController.showNextMonth();
        invalidate();
    }

    public void showPreviousMonth(){
        compactCalendarController.showPreviousMonth();
        invalidate();
    }

    public boolean isAnimating(){
        return animationHandler.isAnimating();
    }

    @Override
    protected void onMeasure(int parentWidth, int parentHeight) {
        super.onMeasure(parentWidth, parentHeight);
        int width = MeasureSpec.getSize(parentWidth);
        int height = MeasureSpec.getSize(parentHeight);
        if(width > 0 && height > 0) {
            compactCalendarController.onMeasure(width, height, getPaddingRight(), getPaddingLeft());
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        compactCalendarController.onDraw(canvas);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(compactCalendarController.computeScroll()){
            invalidate();
        }
    }

    public void shouldScrollMonth(boolean enableHorizontalScroll){
        this.horizontalScrollEnabled = enableHorizontalScroll;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (horizontalScrollEnabled) {
            compactCalendarController.onTouch(event);
            invalidate();
        }

        // on touch action finished (CANCEL or UP), we re-allow the parent container to intercept touch events (scroll inside ViewPager + RecyclerView issue #82)
        if((event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) && horizontalScrollEnabled) {
            getParent().requestDisallowInterceptTouchEvent(false);
        }

        // always allow gestureDetector to detect onSingleTap and scroll events
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if (this.getVisibility() == View.GONE) {
            return false;
        }
        // Prevents ViewPager from scrolling horizontally by announcing that (issue #82)
        return this.horizontalScrollEnabled;
    }

}
