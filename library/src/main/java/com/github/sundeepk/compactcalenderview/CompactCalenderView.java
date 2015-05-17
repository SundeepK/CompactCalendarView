package com.github.sundeepk.compactcalenderview;

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

import java.util.Date;

public class CompactCalenderView extends View {

    private CompactCalenderController compactCalenderController;
    private GestureDetectorCompat gestureDetector;

    public interface CompactCalenderViewListener{
        public void onDayClick(Date dateClicked);
        public void onMonthScroll(Date firstDayOfNewMonth);
    }

    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            compactCalenderController.onSingleTapConfirmed(e);
            invalidate();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return compactCalenderController.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            compactCalenderController.onFling(e1, e2, velocityX, velocityY);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            compactCalenderController.onScroll(e1, e2, distanceX, distanceY);
            invalidate();
            return true;
        }
    };

    public CompactCalenderView(Context context) {
        this(context, null);
    }

    public CompactCalenderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompactCalenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        compactCalenderController = new CompactCalenderController(new Paint(), new OverScroller(getContext()),
                new Rect(), attrs, getContext(),  Color.argb(255, 233, 84, 81), Color.argb(255, 64, 64, 64), Color.argb(255, 219, 219, 219));
        gestureDetector = new GestureDetectorCompat(getContext(), gestureListener);
    }

    public void setListener(CompactCalenderViewListener listener){
        compactCalenderController.setListener(listener);
    }

    public Date getFirstDayOfCurrentMonth(){
        return compactCalenderController.getFirstDayOfCurrentMonth();
    }

    public void setMonth(Date dateTimeMonth){
        compactCalenderController.setMonth(dateTimeMonth);
        invalidate();
    }

    @Override
    protected void onMeasure(int parentWidth, int parentHeight) {
        super.onMeasure(parentWidth, parentHeight);
        compactCalenderController.onMeasure(getWidth(), getHeight(), getPaddingRight(), getPaddingLeft());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        compactCalenderController.onDraw(canvas);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(compactCalenderController.computeScroll()){
            invalidate();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if(compactCalenderController.onTouch(event)){
            invalidate();
            return true;
        }
        return gestureDetector.onTouchEvent(event);
    }

}
