package com.github.sundeepk.compactcalendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.OverScroller;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CompactCalendarController {

    private static final int PADDING = 40;
    private Paint dayPaint = new Paint();
    private Rect rect;
    private int textHeight;
    private int textWidth;
    private static final int DAYS_IN_WEEK = 7;
    private int widthPerDay;
    private String[] dayColumnNames = {"M", "T", "W", "T", "F", "S", "S"};
    private float distanceX;
    private PointF accumulatedScrollOffset = new PointF();
    private OverScroller scroller;
    private int monthsScrolledSoFar;
    private Date currentDate = new Date();
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private Calendar calendarWithFirstDayOfMonth = Calendar.getInstance(Locale.getDefault());
    private Direction currentDirection = Direction.NONE;
    private int heightPerDay;
    private int currentDayBackgroundColor;
    private int calenderTextColor;
    private int firstDayBackgroundColor;
    private int calenderBackgroundColor = Color.WHITE;
    private int textSize = 30;
    private CompactCalendarView.CompactCalendarViewListener compactCalendarViewListener;
    private int width;
    private int height;
    private int paddingRight;
    private int paddingLeft;

    private enum Direction {
        NONE, HORIZONTAL, VERTICAL
    }

    CompactCalendarController(Paint dayPaint, OverScroller scroller, Rect rect, AttributeSet attrs, Context context, int currentDayBackgroundColor, int calenderTextColor, int firstDayBackgroundColor){
        this.dayPaint = dayPaint;
        this.scroller = scroller;
        this.rect = rect;
        loadAttributes(attrs, context);
        init();
        this.currentDayBackgroundColor = currentDayBackgroundColor;
        this.calenderTextColor = calenderTextColor;
        this.firstDayBackgroundColor = firstDayBackgroundColor;
    }

    private void loadAttributes(AttributeSet attrs, Context context) {
        if(attrs != null && context != null){
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,  R.styleable.CompactCalendarView, 0, 0);
            try{
                currentDayBackgroundColor = typedArray.getColor(R.styleable.CompactCalendarView_currentDayBackgroundColor, currentDayBackgroundColor);
                calenderTextColor = typedArray.getColor(R.styleable.CompactCalendarView_calenderTextColor, calenderTextColor);
                firstDayBackgroundColor = typedArray.getColor(R.styleable.CompactCalendarView_firstDayOfMonthBackgroundColor, firstDayBackgroundColor);
                calenderBackgroundColor = typedArray.getColor(R.styleable.CompactCalendarView_calenderBackgroundColor, calenderBackgroundColor);
                textSize = typedArray.getDimensionPixelSize(R.styleable.CompactCalendarView_textSize,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, context.getResources().getDisplayMetrics()));
            }finally{
                typedArray.recycle();
            }
        }
    }

    private void init() {
        dayPaint.setTextAlign(Paint.Align.CENTER);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        dayPaint.setTypeface(Typeface.SANS_SERIF);
        dayPaint.setTextSize(textSize);
        dayPaint.setColor(calenderTextColor);
        dayPaint.getTextBounds("31", 0, "31".length(), rect);
        textHeight = rect.height() * 3;
        textWidth = rect.width() * 2;

        currentCalender.setTime(currentDate);
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, 0);
    }

    private void setCalenderToFirstDayOfMonth(Calendar calendarWithFirstDayOfMonth, Date currentDate, int monthOffset) {
        calendarWithFirstDayOfMonth.setTime(currentDate);
        calendarWithFirstDayOfMonth.add(Calendar.MONTH, -monthsScrolledSoFar + monthOffset);
        calendarWithFirstDayOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        calendarWithFirstDayOfMonth.set(Calendar.MINUTE, 0);
        calendarWithFirstDayOfMonth.set(Calendar.SECOND, 0);
        calendarWithFirstDayOfMonth.set(Calendar.MILLISECOND, 0);
        calendarWithFirstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
    }

    void onMeasure(int width, int height, int paddingRight, int paddingLeft) {
        widthPerDay = (width  - paddingRight) / DAYS_IN_WEEK;
        heightPerDay = height / 7;
        this.width = width;
        this.height = height;
        this.paddingRight = paddingRight;
        this.paddingLeft = paddingLeft;
    }

    void setListener(CompactCalendarView.CompactCalendarViewListener listener){
        this.compactCalendarViewListener = listener;
    }

    void onDraw(Canvas canvas) {
        calculateXPositionOffset();

        drawCalenderBackground(canvas);

        drawScrollableCalender(canvas);
    }

    boolean onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (currentDirection == Direction.HORIZONTAL) {
                monthsScrolledSoFar = Math.round(accumulatedScrollOffset.x / width);
                float remainingScrollAfterFingerLifted = (accumulatedScrollOffset.x - monthsScrolledSoFar * width);
                scroller.startScroll((int) accumulatedScrollOffset.x, 0, (int) -remainingScrollAfterFingerLifted, 0);
                currentDirection = Direction.NONE;
                if(compactCalendarViewListener != null){
                    setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, 0);
                    compactCalendarViewListener.onMonthScroll(calendarWithFirstDayOfMonth.getTime());
                }
                return true;
            }
            currentDirection = Direction.NONE;
        }
        return false;
    }

    Date getFirstDayOfCurrentMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, -monthsScrolledSoFar);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    void setMonth(Date dateTimeMonth){
        currentDate = new Date(dateTimeMonth.getTime());
        currentCalender.setTime(currentDate);
        currentCalender.set(Calendar.HOUR_OF_DAY, 0);
        currentCalender.set(Calendar.MINUTE, 0);
        currentCalender.set(Calendar.SECOND, 0);
        currentCalender.set(Calendar.MILLISECOND, 0);
        monthsScrolledSoFar = 0;
        accumulatedScrollOffset.x = 0;
    }

    boolean onSingleTapConfirmed(MotionEvent e) {
        monthsScrolledSoFar = Math.round(accumulatedScrollOffset.x / width);
        int dayColumn = Math.round((e.getX() - PADDING - paddingRight) / widthPerDay);
        int dayRow = Math.round((e.getY() - PADDING) / heightPerDay);

        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, 0);

        //Start Monday as day 1 and Sunday as day 7. Not Sunday as day 1 and Monday as day 2
        int firstDayOfMonth = calendarWithFirstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1;
        firstDayOfMonth = firstDayOfMonth <= 0 ? 7 : firstDayOfMonth;

        int dayOfMonth = ((dayRow - 1) * 7 + dayColumn + 1) - firstDayOfMonth;

        calendarWithFirstDayOfMonth.add(Calendar.DATE, dayOfMonth);

        if(dayOfMonth >= 0){
            if(compactCalendarViewListener != null){
                compactCalendarViewListener.onDayClick(calendarWithFirstDayOfMonth.getTime());
            }
        }
        return true;
    }

    boolean onDown(MotionEvent e) {
        scroller.forceFinished(true);
        return true;
    }

    boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        scroller.forceFinished(true);
        return true;
    }

    boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (currentDirection == Direction.NONE) {
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                currentDirection = Direction.HORIZONTAL;
            } else {
                currentDirection = Direction.VERTICAL;
            }
        }

        this.distanceX = distanceX;
        calculateXPositionOffset();
        return true;
    }

    boolean computeScroll() {
        if (scroller.computeScrollOffset()) {
            accumulatedScrollOffset.x = scroller.getCurrX();
            return true;
        }
        return false;
    }

    private void drawScrollableCalender(Canvas canvas) {
        monthsScrolledSoFar = (int) (accumulatedScrollOffset.x / width);

        drawPreviousMonth(canvas);

        drawCurrentMonth(canvas);

        drawNextMonth(canvas);
    }

    private void drawNextMonth(Canvas canvas) {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, 1);
        drawMonth(canvas, calendarWithFirstDayOfMonth, (width * (-monthsScrolledSoFar + 1)));
    }

    private void drawCurrentMonth(Canvas canvas) {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, 0);
        drawMonth(canvas, calendarWithFirstDayOfMonth, width * -monthsScrolledSoFar);
    }

    private void drawPreviousMonth(Canvas canvas) {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -1);
        drawMonth(canvas, calendarWithFirstDayOfMonth, (width * (-monthsScrolledSoFar - 1)));
    }

    private void calculateXPositionOffset() {
        if (currentDirection == Direction.HORIZONTAL) {
            accumulatedScrollOffset.x -= distanceX;
        }
    }

    private void drawCalenderBackground(Canvas canvas) {
        dayPaint.setColor(calenderBackgroundColor);
        dayPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, height, dayPaint);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setColor(calenderTextColor);
    }

    void drawMonth(Canvas canvas, Calendar currentMonthToDrawCalender, int offset) {
        //offset by one because we want to start from Monday
        int firstDayOfMonth = currentMonthToDrawCalender.get(Calendar.DAY_OF_WEEK) - 1;
        firstDayOfMonth = firstDayOfMonth <= 0 ? 7 : firstDayOfMonth;

        //offset by one because of 0 index based calculations
        firstDayOfMonth = firstDayOfMonth - 1;
        boolean shouldDrawCurrentDayCircle = currentMonthToDrawCalender.get(Calendar.MONTH) == currentCalender.get(Calendar.MONTH);

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
            float xPosition = widthPerDay * dayColumn + PADDING + accumulatedScrollOffset.x + offset + paddingRight;
            if(dayRow == 0){
                // first row, so draw the first letter of the day
                dayPaint.setTypeface(Typeface.DEFAULT_BOLD);
                canvas.drawText(dayColumnNames[dayColumn], xPosition, PADDING, dayPaint);
                dayPaint.setTypeface(Typeface.DEFAULT);
            }else{
                int day = ((dayRow - 1) * 7 + dayColumn + 1) - firstDayOfMonth;
                float yPosition = dayRow * heightPerDay + PADDING;
                if(shouldDrawCurrentDayCircle && currentCalender.get(Calendar.DAY_OF_MONTH) == day){
                    // add some padding to height and width because it isn't quite centered
                    // TODO calculate position of circle in a more reliable way
                    drawCircle(canvas, xPosition - widthPerDay / 55, yPosition - textHeight / 6, currentDayBackgroundColor);
                }
                if(day <= currentMonthToDrawCalender.getActualMaximum(Calendar.DAY_OF_MONTH) && day > 0){
                    if(day == 1){
                        drawCircle(canvas, xPosition - widthPerDay / 55, yPosition - textHeight / 6, firstDayBackgroundColor);
                    }
                    canvas.drawText(String.valueOf(day), xPosition, yPosition, dayPaint);
                }
            }

        }
    }

    // Draw Circle on certain days to highlight them
    private void drawCircle(Canvas canvas, float x, float y, int color) {
        dayPaint.setStyle(Paint.Style.FILL);
        dayPaint.setColor(color);
        canvas.drawCircle(x, y, widthPerDay/3.5f, dayPaint);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setColor(calenderTextColor);
    }

}
