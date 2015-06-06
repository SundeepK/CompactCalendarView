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
import android.view.View;
import android.widget.OverScroller;

import com.github.sundeepk.compactcalendarview.domain.CalendarDayEvent;

import java.util.Date;

public class CompactCalendarView extends View {

    private CompactCalendarController compactCalendarController;
    private GestureDetectorCompat gestureDetector;

    public interface CompactCalendarViewListener {
        public void onDayClick(Date dateClicked);
        public void onMonthScroll(Date firstDayOfNewMonth);
    }

    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            compactCalendarController.onSingleTapConfirmed(e);
            invalidate();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return compactCalendarController.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            compactCalendarController.onFling(e1, e2, velocityX, velocityY);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            compactCalendarController.onScroll(e1, e2, distanceX, distanceY);
            invalidate();
            return true;
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
                new Rect(), attrs, getContext(),  Color.argb(255, 233, 84, 81), Color.argb(255, 64, 64, 64), Color.argb(255, 219, 219, 219));
        gestureDetector = new GestureDetectorCompat(getContext(), gestureListener);
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

    public void setMonth(Date dateTimeMonth){
        compactCalendarController.setMonth(dateTimeMonth);
        invalidate();
    }

    public int getWeekNumberForCurrentMonth(){
        return compactCalendarController.getWeekNumberForCurrentMonth();
    }

    public void setShouldDrawDaysHeader(boolean shouldDrawDaysHeader){
        compactCalendarController.setShouldDrawDaysHeader(shouldDrawDaysHeader);
    }

   public void addEvent(CalendarDayEvent event){
        compactCalendarController.addEvent(event);
   }

   public void removeEvent(CalendarDayEvent event){
       compactCalendarController.removeEvent(event);
   }

    @Override
    protected void onMeasure(int parentWidth, int parentHeight) {
        super.onMeasure(parentWidth, parentHeight);
        if(getWidth() > 0 && getHeight() > 0) {
            compactCalendarController.onMeasure(getWidth(), getHeight(), getPaddingRight(), getPaddingLeft());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
       // compactCalendarController.onMeasure(getWidth(), getHeight(), getPaddingRight(), getPaddingLeft());
        compactCalendarController.onDraw(canvas);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(compactCalendarController.computeScroll()){
            invalidate();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if(compactCalendarController.onTouch(event)){
            invalidate();
            return true;
        }
        return gestureDetector.onTouchEvent(event);
    }

}
