package com.github.sundeepk.compactcalendarview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.widget.OverScroller
import com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarViewListener
import com.github.sundeepk.compactcalendarview.domain.Event
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

class CompactCalendarController(dayPaint: Paint, scroller: OverScroller, textSizeRect: Rect, attrs: AttributeSet?,
                                         context: Context?, currentDayBackgroundColor: Int, calenderTextColor: Int,
                                         currentSelectedDayBackgroundColor: Int, velocityTracker: VelocityTracker?,
                                         multiEventIndicatorColor: Int, eventsContainer: EventsContainer,
                                         locale: Locale, timeZone: TimeZone) {

    private var eventIndicatorStyle: Int = CompactCalendarView.SMALL_INDICATOR
    private var currentDayIndicatorStyle: Int = CompactCalendarView.FILL_LARGE_INDICATOR
    private var currentSelectedDayIndicatorStyle: Int = CompactCalendarView.FILL_LARGE_INDICATOR
    private var paddingWidth = 40
    private var paddingHeight = 40
    private var textHeight = 0
    private var textWidth = 0
    private var widthPerDay = 0
    private var monthsScrolledSoFar = 0
    var heightPerDay = 0
        private set
    private var textSize = 30
    var width = 0
        private set
    private var height = 0
    private var paddingRight = 0
    private var paddingLeft = 0
    private var maximumVelocity = 0
    private var densityAdjustedSnapVelocity = 0
    private var distanceThresholdForAutoScroll = 0
    var targetHeight = 0
    private var animationStatus = 0
    private var firstDayOfWeekToDraw = Calendar.MONDAY
    private var xIndicatorOffset = 0f
    private var multiDayIndicatorStrokeWidth = 0f
    var dayIndicatorRadius = 0f
        private set
    private var smallIndicatorRadius = 0f
    var growFactor = 0f
        private set
    var screenDensity = 1f
        private set
    var growFactorIndicator = 0f
    private var distanceX = 0f
    private var lastAutoScrollFromFling: Long = 0
    private var useThreeLetterAbbreviation = false
    private var isSmoothScrolling = false
    private var isScrolling = false
    private var shouldDrawDaysHeader = true
    private var shouldDrawIndicatorsBelowSelectedDays = false
    private var displayOtherMonthDays = false
    private var shouldSelectFirstDayOfMonthOnScroll = true
    private var isRtl = false
    private var listener: CompactCalendarViewListener? = null
    private var velocityTracker: VelocityTracker? = null
    private var currentDirection = Direction.NONE
    private var currentDate = Date()
    private var locale: Locale
    private lateinit var currentCalender: Calendar
    private lateinit var todayCalender: Calendar
    private lateinit var calendarWithFirstDayOfMonth: Calendar
    private lateinit var eventsCalendar: Calendar
    private var eventsContainer: EventsContainer
    private val accumulatedScrollOffset = PointF()
    private val scroller: OverScroller
    private var dayPaint = Paint()
    private val background = Paint()
    private val textSizeRect: Rect
    private var dayColumnNames: Array<String?>? = null
    // colors
    private var multiEventIndicatorColor: Int
    private var currentDayBackgroundColor: Int
    private var currentDayTextColor = 0
    private var calenderTextColor: Int
    private var currentSelectedDayBackgroundColor: Int
    private var currentSelectedDayTextColor = 0
    private var calenderBackgroundColor = Color.WHITE
    private var otherMonthDaysTextColor: Int
    private var timeZone: TimeZone

    companion object {
        const val IDLE = 0
        const val EXPOSE_CALENDAR_ANIMATION = 1
        const val EXPAND_COLLAPSE_CALENDAR = 2
        const val ANIMATE_INDICATORS = 3
        private const val VELOCITY_UNIT_PIXELS_PER_SECOND = 1000
        private const val LAST_FLING_THRESHOLD_MILLIS = 300
        private const val DAYS_IN_WEEK = 7
        private const val SNAP_VELOCITY_DIP_PER_SECOND = 400f
        private const val ANIMATION_SCREEN_SET_DURATION_MILLIS = 700f
    }

    init {
        this.dayPaint = dayPaint
        this.scroller = scroller
        this.textSizeRect = textSizeRect
        this.currentDayBackgroundColor = currentDayBackgroundColor
        this.calenderTextColor = calenderTextColor
        this.currentSelectedDayBackgroundColor = currentSelectedDayBackgroundColor
        otherMonthDaysTextColor = calenderTextColor
        this.velocityTracker = velocityTracker
        this.multiEventIndicatorColor = multiEventIndicatorColor
        this.eventsContainer = eventsContainer
        this.locale = locale
        this.timeZone = timeZone
        displayOtherMonthDays = false
        loadAttributes(attrs, context)
        init(context)
    }

    /**
     * Only used in onDrawCurrentMonth to temporarily calculate previous month days
     */
    private lateinit var tempPreviousMonthCalendar: Calendar

    private enum class Direction {
        NONE, HORIZONTAL, VERTICAL
    }

    private fun loadAttributes(attrs: AttributeSet?, context: Context?) {
        if (attrs != null && context != null) {
            val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CompactCalendarView, 0, 0)
            try {
                currentDayBackgroundColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarCurrentDayBackgroundColor, currentDayBackgroundColor)
                calenderTextColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarTextColor, calenderTextColor)
                currentDayTextColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarCurrentDayTextColor, calenderTextColor)
                otherMonthDaysTextColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarOtherMonthDaysTextColor, otherMonthDaysTextColor)
                currentSelectedDayBackgroundColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarCurrentSelectedDayBackgroundColor, currentSelectedDayBackgroundColor)
                currentSelectedDayTextColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarCurrentSelectedDayTextColor, calenderTextColor)
                calenderBackgroundColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarBackgroundColor, calenderBackgroundColor)
                multiEventIndicatorColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarMultiEventIndicatorColor, multiEventIndicatorColor)
                textSize = typedArray.getDimensionPixelSize(R.styleable.CompactCalendarView_compactCalendarTextSize,
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat(), context.resources.displayMetrics).toInt())
                targetHeight = typedArray.getDimensionPixelSize(R.styleable.CompactCalendarView_compactCalendarTargetHeight,
                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, targetHeight.toFloat(), context.resources.displayMetrics).toInt())
                eventIndicatorStyle = typedArray.getInt(R.styleable.CompactCalendarView_compactCalendarEventIndicatorStyle, CompactCalendarView.SMALL_INDICATOR)
                currentDayIndicatorStyle = typedArray.getInt(R.styleable.CompactCalendarView_compactCalendarCurrentDayIndicatorStyle, CompactCalendarView.FILL_LARGE_INDICATOR)
                currentSelectedDayIndicatorStyle = typedArray.getInt(R.styleable.CompactCalendarView_compactCalendarCurrentSelectedDayIndicatorStyle, CompactCalendarView.FILL_LARGE_INDICATOR)
                displayOtherMonthDays = typedArray.getBoolean(R.styleable.CompactCalendarView_compactCalendarDisplayOtherMonthDays, displayOtherMonthDays)
                shouldSelectFirstDayOfMonthOnScroll = typedArray.getBoolean(R.styleable.CompactCalendarView_compactCalendarShouldSelectFirstDayOfMonthOnScroll, shouldSelectFirstDayOfMonthOnScroll)
            } finally {
                typedArray.recycle()
            }
        }
    }

    private fun init(context: Context?) {
        currentCalender = Calendar.getInstance(timeZone, locale)
        todayCalender = Calendar.getInstance(timeZone, locale)
        calendarWithFirstDayOfMonth = Calendar.getInstance(timeZone, locale)
        eventsCalendar = Calendar.getInstance(timeZone, locale)
        tempPreviousMonthCalendar = Calendar.getInstance(timeZone, locale)
        // make setMinimalDaysInFirstWeek same across android versions
        eventsCalendar.minimalDaysInFirstWeek = 1
        calendarWithFirstDayOfMonth.minimalDaysInFirstWeek = 1
        todayCalender.minimalDaysInFirstWeek = 1
        currentCalender.minimalDaysInFirstWeek = 1
        tempPreviousMonthCalendar.minimalDaysInFirstWeek = 1
        setFirstDayOfWeek(firstDayOfWeekToDraw)
        setUseWeekDayAbbreviation(false)
        dayPaint.textAlign = Paint.Align.CENTER
        dayPaint.style = Paint.Style.STROKE
        dayPaint.flags = Paint.ANTI_ALIAS_FLAG
        dayPaint.typeface = Typeface.SANS_SERIF
        dayPaint.textSize = textSize.toFloat()
        dayPaint.color = calenderTextColor
        dayPaint.getTextBounds("31", 0, "31".length, textSizeRect)
        textHeight = textSizeRect.height() * 3
        textWidth = textSizeRect.width() * 2
        todayCalender.time = Date()
        setToMidnight(todayCalender)
        currentCalender.time = currentDate
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, 0)
        initScreenDensityRelatedValues(context)
        xIndicatorOffset = 3.5f * screenDensity
        //scale small indicator by screen density
        smallIndicatorRadius = 2.5f * screenDensity
        //just set a default growFactor to draw full calendar when initialised
        growFactor = Int.MAX_VALUE.toFloat()
    }

    private fun initScreenDensityRelatedValues(context: Context?) {
        if (context != null) {
            screenDensity = context.resources.displayMetrics.density
            val configuration = ViewConfiguration
                    .get(context)
            densityAdjustedSnapVelocity = (screenDensity * SNAP_VELOCITY_DIP_PER_SECOND).toInt()
            maximumVelocity = configuration.scaledMaximumFlingVelocity
            val dm = context.resources.displayMetrics
            multiDayIndicatorStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, dm)
        }
    }

    private fun setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth: Calendar?, currentDate: Date, scrollOffset: Int, monthOffset: Int) {
        setMonthOffset(calendarWithFirstDayOfMonth, currentDate, scrollOffset, monthOffset)
        calendarWithFirstDayOfMonth!![Calendar.DAY_OF_MONTH] = 1
    }

    private fun setMonthOffset(calendarWithFirstDayOfMonth: Calendar?, currentDate: Date, scrollOffset: Int, monthOffset: Int) {
        calendarWithFirstDayOfMonth!!.time = currentDate
        calendarWithFirstDayOfMonth.add(Calendar.MONTH, scrollOffset + monthOffset)
        calendarWithFirstDayOfMonth[Calendar.HOUR_OF_DAY] = 0
        calendarWithFirstDayOfMonth[Calendar.MINUTE] = 0
        calendarWithFirstDayOfMonth[Calendar.SECOND] = 0
        calendarWithFirstDayOfMonth[Calendar.MILLISECOND] = 0
    }

    fun setIsRtl(isRtl: Boolean) {
        this.isRtl = isRtl
    }

    fun setShouldSelectFirstDayOfMonthOnScroll(shouldSelectFirstDayOfMonthOnScroll: Boolean) {
        this.shouldSelectFirstDayOfMonthOnScroll = shouldSelectFirstDayOfMonthOnScroll
    }

    fun setDisplayOtherMonthDays(displayOtherMonthDays: Boolean) {
        this.displayOtherMonthDays = displayOtherMonthDays
    }

    fun shouldDrawIndicatorsBelowSelectedDays(shouldDrawIndicatorsBelowSelectedDays: Boolean) {
        this.shouldDrawIndicatorsBelowSelectedDays = shouldDrawIndicatorsBelowSelectedDays
    }

    fun setCurrentDayIndicatorStyle(currentDayIndicatorStyle: Int) {
        this.currentDayIndicatorStyle = currentDayIndicatorStyle
    }

    fun setEventIndicatorStyle(eventIndicatorStyle: Int) {
        this.eventIndicatorStyle = eventIndicatorStyle
    }

    fun setCurrentSelectedDayIndicatorStyle(currentSelectedDayIndicatorStyle: Int) {
        this.currentSelectedDayIndicatorStyle = currentSelectedDayIndicatorStyle
    }

    fun setAnimationStatus(animationStatus: Int) {
        this.animationStatus = animationStatus
    }

    fun setListener(listener: CompactCalendarViewListener?) {
        this.listener = listener
    }

    fun removeAllEvents() {
        eventsContainer.removeAllEvents()
    }

    fun setFirstDayOfWeek(day: Int) {
        require(!(day < 1 || day > 7)) { "Day must be an int between 1 and 7 or DAY_OF_WEEK from Java Calendar class. For more information please see Calendar.DAY_OF_WEEK." }
        firstDayOfWeekToDraw = day
        setUseWeekDayAbbreviation(useThreeLetterAbbreviation)
        eventsCalendar.firstDayOfWeek = day
        calendarWithFirstDayOfMonth.firstDayOfWeek = day
        todayCalender.firstDayOfWeek = day
        currentCalender.firstDayOfWeek = day
        tempPreviousMonthCalendar.firstDayOfWeek = day
    }

    fun setCurrentSelectedDayBackgroundColor(currentSelectedDayBackgroundColor: Int) {
        this.currentSelectedDayBackgroundColor = currentSelectedDayBackgroundColor
    }

    fun setCurrentSelectedDayTextColor(currentSelectedDayTextColor: Int) {
        this.currentSelectedDayTextColor = currentSelectedDayTextColor
    }

    fun setCalenderBackgroundColor(calenderBackgroundColor: Int) {
        this.calenderBackgroundColor = calenderBackgroundColor
    }

    fun setCurrentDayBackgroundColor(currentDayBackgroundColor: Int) {
        this.currentDayBackgroundColor = currentDayBackgroundColor
    }

    fun setCurrentDayTextColor(currentDayTextColor: Int) {
        this.currentDayTextColor = currentDayTextColor
    }

    fun scrollRight() {
        if (isRtl) {
            scrollPrev()
        } else {
            scrollNext()
        }
    }

    fun scrollLeft() {
        if (isRtl) {
            scrollNext()
        } else {
            scrollPrev()
        }
    }

    private fun scrollNext() {
        monthsScrolledSoFar--
        accumulatedScrollOffset.x = monthsScrolledSoFar * width.toFloat()
        if (shouldSelectFirstDayOfMonthOnScroll) {
            setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentCalender.time, 0, 1)
            setCurrentDate(calendarWithFirstDayOfMonth.time)
        }
        performMonthScrollCallback()
    }

    private fun scrollPrev() {
        monthsScrolledSoFar += 1
        accumulatedScrollOffset.x = monthsScrolledSoFar * width.toFloat()
        if (shouldSelectFirstDayOfMonthOnScroll) {
            setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentCalender.time, 0, -1)
            setCurrentDate(calendarWithFirstDayOfMonth.time)
        }
        performMonthScrollCallback()
    }

    fun setLocale(timeZone: TimeZone?, locale: Locale?) {
        requireNotNull(locale) { "Locale cannot be null." }
        requireNotNull(timeZone) { "TimeZone cannot be null." }
        this.locale = locale
        this.timeZone = timeZone
        eventsContainer = EventsContainer(Calendar.getInstance(this.timeZone, this.locale))
        // passing null will not re-init density related values - and that's ok
        init(null)
    }

    fun setUseWeekDayAbbreviation(useThreeLetterAbbreviation: Boolean) {
        this.useThreeLetterAbbreviation = useThreeLetterAbbreviation
        dayColumnNames = WeekUtils.getWeekdayNames(locale, firstDayOfWeekToDraw, this.useThreeLetterAbbreviation)
    }

    fun setDayColumnNames(dayColumnNames: Array<String?>?) {
        require(!(dayColumnNames == null || dayColumnNames.size != 7)) { "Column names cannot be null and must contain a value for each day of the week" }
        this.dayColumnNames = dayColumnNames
    }

    fun setShouldDrawDaysHeader(shouldDrawDaysHeader: Boolean) {
        this.shouldDrawDaysHeader = shouldDrawDaysHeader
    }

    fun onMeasure(width: Int, height: Int, paddingRight: Int, paddingLeft: Int) {
        widthPerDay = width / DAYS_IN_WEEK
        heightPerDay = if (targetHeight > 0) targetHeight / 7 else height / 7
        this.width = width
        distanceThresholdForAutoScroll = (width * 0.50).toInt()
        this.height = height
        this.paddingRight = paddingRight
        this.paddingLeft = paddingLeft
        //makes easier to find radius
        dayIndicatorRadius = interpolatedBigCircleIndicator
        // scale the selected day indicators slightly so that event indicators can be drawn below
        dayIndicatorRadius = if (shouldDrawIndicatorsBelowSelectedDays && eventIndicatorStyle == CompactCalendarView.SMALL_INDICATOR) dayIndicatorRadius * 0.85f else dayIndicatorRadius
    }// pick a point which is almost half way through heightPerDay and textSizeRect// take into account indicator offset

    //assume square around each day of width and height = heightPerDay and get diagonal line length
//interpolate height and radius
//https://en.wikipedia.org/wiki/Linear_interpolation
    private val interpolatedBigCircleIndicator: Float
        get() {
            val x0 = textSizeRect.height().toFloat()
            val x1 = heightPerDay.toFloat() // take into account indicator offset
            val x = (x1 + textSizeRect.height()) / 2f // pick a point which is almost half way through heightPerDay and textSizeRect
            val y1 = 0.5 * sqrt(x1 * x1 + (x1 * x1).toDouble())
            val y0 = 0.5 * sqrt(x0 * x0 + (x0 * x0).toDouble())
            return (y0 + (y1 - y0) * ((x - x0) / (x1 - x0))).toFloat()
        }

    fun onDraw(canvas: Canvas) {
        paddingWidth = widthPerDay / 2
        paddingHeight = heightPerDay / 2
        calculateXPositionOffset()
        when (animationStatus) {
            EXPOSE_CALENDAR_ANIMATION -> {
                drawCalendarWhileAnimating(canvas)
            }
            ANIMATE_INDICATORS -> {
                drawCalendarWhileAnimatingIndicators(canvas)
            }
            else -> {
                drawCalenderBackground(canvas)
                drawScrollableCalender(canvas)
            }
        }
    }

    private fun drawCalendarWhileAnimatingIndicators(canvas: Canvas) {
        dayPaint.color = calenderBackgroundColor
        dayPaint.style = Paint.Style.FILL
        canvas.drawCircle(0f, 0f, growFactor, dayPaint)
        dayPaint.style = Paint.Style.STROKE
        dayPaint.color = Color.WHITE
        drawScrollableCalender(canvas)
    }

    private fun drawCalendarWhileAnimating(canvas: Canvas) {
        background.color = calenderBackgroundColor
        background.style = Paint.Style.FILL
        canvas.drawCircle(0f, 0f, growFactor, background)
        dayPaint.style = Paint.Style.STROKE
        dayPaint.color = Color.WHITE
        drawScrollableCalender(canvas)
    }

    fun onSingleTapUp(e: MotionEvent) { // Don't handle single tap when calendar is scrolling and is not stationary
        if (isScrolling()) {
            return
        }
        val dayColumn = ((paddingLeft + e.x - paddingWidth - paddingRight) / widthPerDay).roundToInt()
        val dayRow = ((e.y - paddingHeight) / heightPerDay).roundToInt()
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, monthsScrolledSoFar(), 0)
        val firstDayOfMonth = getDayOfWeek(calendarWithFirstDayOfMonth)
        var dayOfMonth = (dayRow - 1) * 7 - firstDayOfMonth
        dayOfMonth += if (isRtl) {
            6 - dayColumn
        } else {
            dayColumn
        }
        if (dayOfMonth < calendarWithFirstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
                && dayOfMonth >= 0) {
            calendarWithFirstDayOfMonth.add(Calendar.DATE, dayOfMonth)
            currentCalender.timeInMillis = calendarWithFirstDayOfMonth.timeInMillis
            performOnDayClickCallback(currentCalender.time)
        }
    }

    // Add a little leeway buy checking if amount scrolled is almost same as expected scroll
// as it maybe off by a few pixels
    private fun isScrolling(): Boolean {
        val scrolledX = abs(accumulatedScrollOffset.x)
        val expectedScrollX = abs(width * monthsScrolledSoFar)
        return scrolledX < expectedScrollX - 5 || scrolledX > expectedScrollX + 5
    }

    private fun performOnDayClickCallback(date: Date) {
        if (listener != null) {
            listener!!.onDayClick(date)
        }
    }

    fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean { //ignore scrolling callback if already smooth scrolling
        if (isSmoothScrolling) {
            return true
        }
        if (currentDirection == Direction.NONE) {
            currentDirection = if (abs(distanceX) > abs(distanceY)) {
                Direction.HORIZONTAL
            } else {
                Direction.VERTICAL
            }
        }
        isScrolling = true
        this.distanceX = distanceX
        return true
    }

    fun onTouch(event: MotionEvent): Boolean {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
        velocityTracker!!.addMovement(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!scroller.isFinished) {
                    scroller.abortAnimation()
                }
                isSmoothScrolling = false
            }
            MotionEvent.ACTION_MOVE -> {
                velocityTracker!!.addMovement(event)
                velocityTracker!!.computeCurrentVelocity(500)
            }
            MotionEvent.ACTION_UP -> {
                handleHorizontalScrolling()
                velocityTracker!!.recycle()
                velocityTracker!!.clear()
                velocityTracker = null
                isScrolling = false
            }
        }
        return false
    }

    private fun snapBackScroller() {
        val remainingScrollAfterFingerLifted1 = accumulatedScrollOffset.x - monthsScrolledSoFar * width
        scroller.startScroll(accumulatedScrollOffset.x.toInt(), 0, (-remainingScrollAfterFingerLifted1).toInt(), 0)
    }

    private fun handleHorizontalScrolling() {
        val velocityX = computeVelocity()
        handleSmoothScrolling(velocityX)
        currentDirection = Direction.NONE
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, monthsScrolledSoFar(), 0)
        if (calendarWithFirstDayOfMonth[Calendar.MONTH] != currentCalender[Calendar.MONTH] && shouldSelectFirstDayOfMonthOnScroll) {
            setCalenderToFirstDayOfMonth(currentCalender, currentDate, monthsScrolledSoFar(), 0)
        }
    }

    private fun computeVelocity(): Int {
        velocityTracker!!.computeCurrentVelocity(VELOCITY_UNIT_PIXELS_PER_SECOND, maximumVelocity.toFloat())
        return velocityTracker!!.xVelocity.toInt()
    }

    private fun handleSmoothScrolling(velocityX: Int) {
        val distanceScrolled = (accumulatedScrollOffset.x - width * monthsScrolledSoFar).toInt()
        val isEnoughTimeElapsedSinceLastSmoothScroll = System.currentTimeMillis() - lastAutoScrollFromFling > LAST_FLING_THRESHOLD_MILLIS
        if (velocityX > densityAdjustedSnapVelocity && isEnoughTimeElapsedSinceLastSmoothScroll) {
            scrollPreviousMonth()
        } else if (velocityX < -densityAdjustedSnapVelocity && isEnoughTimeElapsedSinceLastSmoothScroll) {
            scrollNextMonth()
        } else if (isScrolling && distanceScrolled > distanceThresholdForAutoScroll) {
            scrollPreviousMonth()
        } else if (isScrolling && distanceScrolled < -distanceThresholdForAutoScroll) {
            scrollNextMonth()
        } else {
            isSmoothScrolling = false
            snapBackScroller()
        }
    }

    private fun scrollNextMonth() {
        lastAutoScrollFromFling = System.currentTimeMillis()
        monthsScrolledSoFar--
        performScroll()
        isSmoothScrolling = true
        performMonthScrollCallback()
    }

    private fun scrollPreviousMonth() {
        lastAutoScrollFromFling = System.currentTimeMillis()
        monthsScrolledSoFar++
        performScroll()
        isSmoothScrolling = true
        performMonthScrollCallback()
    }

    private fun performMonthScrollCallback() {
        if (listener != null) {
            listener!!.onMonthScroll(firstDayOfCurrentMonth)
        }
    }

    private fun performScroll() {
        val targetScroll = monthsScrolledSoFar * width
        val remainingScrollAfterFingerLifted = targetScroll - accumulatedScrollOffset.x
        scroller.startScroll(accumulatedScrollOffset.x.toInt(), 0, remainingScrollAfterFingerLifted.toInt(), 0,
                (abs(remainingScrollAfterFingerLifted.toInt()) / width.toFloat() * ANIMATION_SCREEN_SET_DURATION_MILLIS).toInt())
    }

    val weekNumberForCurrentMonth: Int
        get() {
            val calendar = Calendar.getInstance(timeZone, locale)
            calendar.time = currentDate
            return calendar[Calendar.WEEK_OF_MONTH]
        }

    val firstDayOfCurrentMonth: Date
        get() {
            val calendar = Calendar.getInstance(timeZone, locale)
            calendar.time = currentDate
            calendar.add(Calendar.MONTH, monthsScrolledSoFar())
            calendar[Calendar.DAY_OF_MONTH] = 1
            setToMidnight(calendar)
            return calendar.time
        }

    fun setCurrentDate(dateTimeMonth: Date) {
        distanceX = 0f
        monthsScrolledSoFar = 0
        accumulatedScrollOffset.x = 0f
        scroller.startScroll(0, 0, 0, 0)
        currentDate = Date(dateTimeMonth.time)
        currentCalender.time = currentDate
        todayCalender = Calendar.getInstance(timeZone, locale)
        setToMidnight(currentCalender)
    }

    private fun setToMidnight(calendar: Calendar?) {
        calendar!![Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
    }

    fun addEvent(event: Event) {
        eventsContainer.addEvent(event)
    }

    fun addEvents(events: List<Event>) {
        eventsContainer.addEvents(events)
    }

    fun getCalendarEventsFor(epochMillis: Long): List<Event?>? {
        return eventsContainer.getEventsFor(epochMillis)
    }

    fun getCalendarEventsForMonth(epochMillis: Long): List<Event> {
        return eventsContainer.getEventsForMonth(epochMillis)
    }

    fun removeEventsFor(epochMillis: Long) {
        eventsContainer.removeEventByEpochMillis(epochMillis)
    }

    fun removeEvent(event: Event) {
        eventsContainer.removeEvent(event)
    }

    fun removeEvents(events: List<Event>) {
        eventsContainer.removeEvents(events)
    }

    fun setGrowProgress(grow: Float) {
        growFactor = grow
    }

    fun onDown(e: MotionEvent?): Boolean {
        scroller.forceFinished(true)
        return true
    }

    fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        scroller.forceFinished(true)
        return true
    }

    fun computeScroll(): Boolean {
        if (scroller.computeScrollOffset()) {
            accumulatedScrollOffset.x = scroller.currX.toFloat()
            return true
        }
        return false
    }

    private fun drawScrollableCalender(canvas: Canvas) {
        if (isRtl) {
            drawNextMonth(canvas, -1)
            drawCurrentMonth(canvas)
            drawPreviousMonth(canvas, 1)
        } else {
            drawPreviousMonth(canvas, -1)
            drawCurrentMonth(canvas)
            drawNextMonth(canvas, 1)
        }
    }

    private fun drawNextMonth(canvas: Canvas, offset: Int) {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, offset)
        drawMonth(canvas, calendarWithFirstDayOfMonth, width * (-monthsScrolledSoFar + 1))
    }

    private fun drawCurrentMonth(canvas: Canvas) {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, monthsScrolledSoFar(), 0)
        drawMonth(canvas, calendarWithFirstDayOfMonth, width * -monthsScrolledSoFar)
    }

    private fun monthsScrolledSoFar(): Int {
        return if (isRtl) monthsScrolledSoFar else -monthsScrolledSoFar
    }

    private fun drawPreviousMonth(canvas: Canvas, offset: Int) {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -monthsScrolledSoFar, offset)
        drawMonth(canvas, calendarWithFirstDayOfMonth, width * (-monthsScrolledSoFar - 1))
    }

    private fun calculateXPositionOffset() {
        if (currentDirection == Direction.HORIZONTAL) {
            accumulatedScrollOffset.x -= distanceX
        }
    }

    private fun drawCalenderBackground(canvas: Canvas) {
        dayPaint.color = calenderBackgroundColor
        dayPaint.style = Paint.Style.FILL
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), dayPaint)
        dayPaint.style = Paint.Style.STROKE
        dayPaint.color = calenderTextColor
    }

    fun drawEvents(canvas: Canvas, currentMonthToDrawCalender: Calendar?, offset: Int) {
        val currentMonth = currentMonthToDrawCalender!![Calendar.MONTH]
        val uniqEvents = eventsContainer.getEventsForMonthAndYear(currentMonth, currentMonthToDrawCalender[Calendar.YEAR])
        val shouldDrawCurrentDayCircle = currentMonth == todayCalender[Calendar.MONTH]
        val shouldDrawSelectedDayCircle = currentMonth == currentCalender[Calendar.MONTH]
        val todayDayOfMonth = todayCalender[Calendar.DAY_OF_MONTH]
        val currentYear = todayCalender[Calendar.YEAR]
        val selectedDayOfMonth = currentCalender[Calendar.DAY_OF_MONTH]
        val indicatorOffset = dayIndicatorRadius / 2
        if (uniqEvents != null) {
            for (i in uniqEvents.indices) {
                val events = uniqEvents[i]
                val timeMillis = events.timeInMillis
                eventsCalendar.timeInMillis = timeMillis
                var dayOfWeek = getDayOfWeek(eventsCalendar)
                if (isRtl) {
                    dayOfWeek = 6 - dayOfWeek
                }
                val weekNumberForMonth = eventsCalendar[Calendar.WEEK_OF_MONTH]
                val xPosition = widthPerDay * dayOfWeek + paddingWidth + paddingLeft + accumulatedScrollOffset.x + offset - paddingRight
                var yPosition = weekNumberForMonth * heightPerDay + paddingHeight.toFloat()
                if ((animationStatus == EXPOSE_CALENDAR_ANIMATION || animationStatus == ANIMATE_INDICATORS) && xPosition >= growFactor || yPosition >= growFactor) { // only draw small event indicators if enough of the calendar is exposed
                    continue
                } else if (animationStatus == EXPAND_COLLAPSE_CALENDAR && yPosition >= growFactor) { // expanding animation, just draw event indicators if enough of the calendar is visible
                    continue
                } else if (animationStatus == EXPOSE_CALENDAR_ANIMATION && (eventIndicatorStyle == CompactCalendarView.FILL_LARGE_INDICATOR || eventIndicatorStyle == CompactCalendarView.NO_FILL_LARGE_INDICATOR)) { // Don't draw large indicators during expose animation, until animation is done
                    continue
                }
                val eventsList = events.events
                val dayOfMonth = eventsCalendar[Calendar.DAY_OF_MONTH]
                val eventYear = eventsCalendar[Calendar.YEAR]
                val isSameDayAsCurrentDay = shouldDrawCurrentDayCircle && todayDayOfMonth == dayOfMonth && eventYear == currentYear
                val isCurrentSelectedDay = shouldDrawSelectedDayCircle && selectedDayOfMonth == dayOfMonth
                if (shouldDrawIndicatorsBelowSelectedDays || !shouldDrawIndicatorsBelowSelectedDays && !isSameDayAsCurrentDay && !isCurrentSelectedDay || animationStatus == EXPOSE_CALENDAR_ANIMATION) {
                    if (eventIndicatorStyle == CompactCalendarView.FILL_LARGE_INDICATOR || eventIndicatorStyle == CompactCalendarView.NO_FILL_LARGE_INDICATOR) {
                        if (eventsList.isNotEmpty()) {
                            val event = eventsList[0]
                            drawEventIndicatorCircle(canvas, xPosition, yPosition, event.color)
                        }
                    } else {
                        yPosition += indicatorOffset
                        // offset event indicators to draw below selected day indicators
// this makes sure that they do no overlap
                        if (shouldDrawIndicatorsBelowSelectedDays && (isSameDayAsCurrentDay || isCurrentSelectedDay)) {
                            yPosition += indicatorOffset
                        }
                        when {
                            eventsList.size >= 3 -> {
                                drawEventsWithPlus(canvas, xPosition, yPosition, eventsList)
                            }
                            eventsList.size == 2 -> {
                                drawTwoEvents(canvas, xPosition, yPosition, eventsList)
                            }
                            eventsList.size == 1 -> {
                                drawSingleEvent(canvas, xPosition, yPosition, eventsList)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun drawSingleEvent(canvas: Canvas, xPosition: Float, yPosition: Float, eventsList: List<Event?>?) {
        val event = eventsList!![0]
        drawEventIndicatorCircle(canvas, xPosition, yPosition, event!!.color)
    }

    private fun drawTwoEvents(canvas: Canvas, xPosition: Float, yPosition: Float, eventsList: List<Event?>?) { //draw fist event just left of center
        drawEventIndicatorCircle(canvas, xPosition + xIndicatorOffset * -1, yPosition, eventsList!![0]!!.color)
        //draw second event just right of center
        drawEventIndicatorCircle(canvas, xPosition + xIndicatorOffset * 1, yPosition, eventsList[1]!!.color)
    }

    //draw 2 eventsByMonthAndYearMap followed by plus indicator to show there are more than 2 eventsByMonthAndYearMap
    private fun drawEventsWithPlus(canvas: Canvas, xPosition: Float, yPosition: Float, eventsList: List<Event?>?) { // k = size() - 1, but since we don't want to draw more than 2 indicators, we just stop after 2 iterations so we can just hard k = -2 instead
// we can use the below loop to draw arbitrary eventsByMonthAndYearMap based on the current screen size, for example, larger screens should be able to
// display more than 2 evens before displaying plus indicator, but don't draw more than 3 indicators for now
        var j = 0
        var k = -2
        while (j < 3) {
            val event = eventsList!![j]
            val xStartPosition = xPosition + xIndicatorOffset * k
            if (j == 2) {
                dayPaint.color = multiEventIndicatorColor
                dayPaint.strokeWidth = multiDayIndicatorStrokeWidth
                canvas.drawLine(xStartPosition - smallIndicatorRadius, yPosition, xStartPosition + smallIndicatorRadius, yPosition, dayPaint)
                canvas.drawLine(xStartPosition, yPosition - smallIndicatorRadius, xStartPosition, yPosition + smallIndicatorRadius, dayPaint)
                dayPaint.strokeWidth = 0f
            } else {
                drawEventIndicatorCircle(canvas, xStartPosition, yPosition, event!!.color)
            }
            j++
            k += 2
        }
    }

    // zero based indexes used internally so instead of returning range of 1-7 like calendar class
// it returns 0-6 where 0 is Sunday instead of 1
    fun getDayOfWeek(calendar: Calendar?): Int {
        var dayOfWeek = calendar!![Calendar.DAY_OF_WEEK] - firstDayOfWeekToDraw
        dayOfWeek = if (dayOfWeek < 0) 7 + dayOfWeek else dayOfWeek
        return dayOfWeek
    }

    fun drawMonth(canvas: Canvas, monthToDrawCalender: Calendar?, offset: Int) {
        drawEvents(canvas, monthToDrawCalender, offset)
        //offset by one because we want to start from Monday
        val firstDayOfMonth = getDayOfWeek(monthToDrawCalender)
        val isSameMonthAsToday = monthToDrawCalender!![Calendar.MONTH] == todayCalender[Calendar.MONTH]
        val isSameYearAsToday = monthToDrawCalender[Calendar.YEAR] == todayCalender[Calendar.YEAR]
        val isSameMonthAsCurrentCalendar = monthToDrawCalender[Calendar.MONTH] == currentCalender[Calendar.MONTH] &&
                monthToDrawCalender[Calendar.YEAR] == currentCalender[Calendar.YEAR]
        val todayDayOfMonth = todayCalender[Calendar.DAY_OF_MONTH]
        val isAnimatingWithExpose = animationStatus == EXPOSE_CALENDAR_ANIMATION
        val maximumMonthDay = monthToDrawCalender.getActualMaximum(Calendar.DAY_OF_MONTH)
        tempPreviousMonthCalendar.timeInMillis = monthToDrawCalender.timeInMillis
        tempPreviousMonthCalendar.add(Calendar.MONTH, -1)
        val maximumPreviousMonthDay = tempPreviousMonthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        var dayColumn = 0
        var colDirection = if (isRtl) 6 else 0
        var dayRow = 0
        while (dayColumn <= 6) {
            if (dayRow == 7) {
                if (isRtl) {
                    colDirection--
                } else {
                    colDirection++
                }
                dayRow = 0
                if (dayColumn <= 6) {
                    dayColumn++
                }
            }
            if (dayColumn == dayColumnNames!!.size) {
                break
            }
            val xPosition = widthPerDay * dayColumn + paddingWidth + paddingLeft + accumulatedScrollOffset.x + offset - paddingRight
            val yPosition = dayRow * heightPerDay + paddingHeight.toFloat()
            if (xPosition >= growFactor && (isAnimatingWithExpose || animationStatus == ANIMATE_INDICATORS) || yPosition >= growFactor) { // don't draw days if animating expose or indicators
                dayRow++
                continue
            }
            if (dayRow == 0) { // first row, so draw the first letter of the day
                if (shouldDrawDaysHeader) {
                    dayPaint.color = calenderTextColor
                    dayPaint.typeface = Typeface.DEFAULT_BOLD
                    dayPaint.style = Paint.Style.FILL
                    dayPaint.color = calenderTextColor
                    canvas.drawText(dayColumnNames!![colDirection]!!, xPosition, paddingHeight.toFloat(), dayPaint)
                    dayPaint.typeface = Typeface.DEFAULT
                }
            } else {
                val day = (dayRow - 1) * 7 + colDirection + 1 - firstDayOfMonth
                var defaultCalenderTextColorToUse = calenderTextColor
                if (currentCalender[Calendar.DAY_OF_MONTH] == day && isSameMonthAsCurrentCalendar && !isAnimatingWithExpose) {
                    drawDayCircleIndicator(currentSelectedDayIndicatorStyle, canvas, xPosition, yPosition, currentSelectedDayBackgroundColor)
                    defaultCalenderTextColorToUse = currentSelectedDayTextColor
                } else if (isSameYearAsToday && isSameMonthAsToday && todayDayOfMonth == day && !isAnimatingWithExpose) { // TODO calculate position of circle in a more reliable way
                    drawDayCircleIndicator(currentDayIndicatorStyle, canvas, xPosition, yPosition, currentDayBackgroundColor)
                    defaultCalenderTextColorToUse = currentDayTextColor
                }
                if (day <= 0) {
                    if (displayOtherMonthDays) { // Display day month before
                        dayPaint.style = Paint.Style.FILL
                        dayPaint.color = otherMonthDaysTextColor
                        canvas.drawText((maximumPreviousMonthDay + day).toString(), xPosition, yPosition, dayPaint)
                    }
                } else if (day > maximumMonthDay) {
                    if (displayOtherMonthDays) { // Display day month after
                        dayPaint.style = Paint.Style.FILL
                        dayPaint.color = otherMonthDaysTextColor
                        canvas.drawText((day - maximumMonthDay).toString(), xPosition, yPosition, dayPaint)
                    }
                } else {
                    dayPaint.style = Paint.Style.FILL
                    dayPaint.color = defaultCalenderTextColorToUse
                    canvas.drawText(day.toString(), xPosition, yPosition, dayPaint)
                }
            }
            dayRow++
        }
    }

    private fun drawDayCircleIndicator(indicatorStyle: Int, canvas: Canvas, x: Float, y: Float, color: Int, circleScale: Float = 1f) {
        val strokeWidth = dayPaint.strokeWidth
        if (indicatorStyle == CompactCalendarView.NO_FILL_LARGE_INDICATOR) {
            dayPaint.strokeWidth = 2 * screenDensity
            dayPaint.style = Paint.Style.STROKE
        } else {
            dayPaint.style = Paint.Style.FILL
        }
        drawCircle(canvas, x, y, color, circleScale)
        dayPaint.strokeWidth = strokeWidth
        dayPaint.style = Paint.Style.FILL
    }

    // Draw Circle on certain days to highlight them
    private fun drawCircle(canvas: Canvas, x: Float, y: Float, color: Int, circleScale: Float) {
        dayPaint.color = color
        if (animationStatus == ANIMATE_INDICATORS) {
            val maxRadius = circleScale * dayIndicatorRadius * 1.4f
            drawCircle(canvas, if (growFactorIndicator > maxRadius) maxRadius else growFactorIndicator, x, y - textHeight / 6)
        } else {
            drawCircle(canvas, circleScale * dayIndicatorRadius, x, y - textHeight / 6)
        }
    }

    private fun drawEventIndicatorCircle(canvas: Canvas, x: Float, y: Float, color: Int) {
        dayPaint.color = color
        when (eventIndicatorStyle) {
            CompactCalendarView.SMALL_INDICATOR -> {
                dayPaint.style = Paint.Style.FILL
                drawCircle(canvas, smallIndicatorRadius, x, y)
            }
            CompactCalendarView.NO_FILL_LARGE_INDICATOR -> {
                dayPaint.style = Paint.Style.STROKE
                drawDayCircleIndicator(CompactCalendarView.NO_FILL_LARGE_INDICATOR, canvas, x, y, color)
            }
            CompactCalendarView.FILL_LARGE_INDICATOR -> {
                drawDayCircleIndicator(CompactCalendarView.FILL_LARGE_INDICATOR, canvas, x, y, color)
            }
        }
    }

    private fun drawCircle(canvas: Canvas, radius: Float, x: Float, y: Float) {
        canvas.drawCircle(x, y, radius, dayPaint)
    }
}
