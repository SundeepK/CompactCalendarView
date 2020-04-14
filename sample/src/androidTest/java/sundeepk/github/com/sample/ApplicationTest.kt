package sundeepk.github.com.sample

import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.azimolabs.conditionwatcher.ConditionWatcher
import com.azimolabs.conditionwatcher.Instruction
import com.facebook.testing.screenshot.Screenshot
import com.facebook.testing.screenshot.ViewHelpers
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarAnimationListener
import com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarViewListener
import com.github.sundeepk.compactcalendarview.domain.Event
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.text.SimpleDateFormat
import java.util.*

@RunWith(AndroidJUnit4::class)
class ApplicationTest {
    private var dateFormatForMonth: SimpleDateFormat? = null
    private var compactCalendarView: CompactCalendarView? = null
    @Rule
    var activityRule = ActivityTestRule(MainActivity::class.java)
    private var mainContent: View? = null
    private var onClosedCallCount = 0
    private var onOpenedCallCount = 0
    @Before
    @Throws(Exception::class)
    fun setUp() {
        Locale.setDefault(Locale.ENGLISH)
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))
        dateFormatForMonth = SimpleDateFormat("MMM - yyyy", Locale.getDefault())
        compactCalendarView = activityRule.activity.findViewById<View>(R.id.compactcalendar_view) as CompactCalendarView
        mainContent = activityRule.activity.findViewById(R.id.parent)
        onClosedCallCount = 0
        onOpenedCallCount = 0
    }

    @Test
    fun testItDrawsEventsRtl() {
        val currentCalender = Calendar.getInstance()
        currentCalender[Calendar.DAY_OF_MONTH] = 1
        currentCalender[Calendar.ERA] = GregorianCalendar.AD
        currentCalender[Calendar.YEAR] = 2015
        currentCalender[Calendar.MONTH] = Calendar.MARCH
        compactCalendarView!!.setIsRtl(true)
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        addEvents(Calendar.FEBRUARY, 2015)
        addEvents(Calendar.MARCH, 2015)
        scrollCalendarBackwardsBy(1)
        Assert.assertEquals(getEventsFor(Calendar.MARCH, 2015), compactCalendarView!!.getEventsForMonth(currentCalender.time))
        syncToolbarDate()
        takeScreenShot()
    }

    @Test
    fun testItDrawsEventIndicatorsBelowHighlightedDayIndicators() {
        setDrawEventsBelowDayIndicators(true)
        setDate(Date(1423094400000L))
        addEvents(Calendar.FEBRUARY, 2015)
        takeScreenShot()
    }

    @Test
    fun testItDrawsFillLargeIndicatorForEventsWhenDrawEventsBelowDayIndicatorsIsTrue() { // test to make sure calendar does not draw event indicators below highlighted days
// when the style is FILL_LARGE_INDICATOR
//Sun, 08 Feb 2015 00:00:00 GMT
        setDrawEventsBelowDayIndicators(true)
        setDate(Date(1423353600000L))
        addEvents(Calendar.FEBRUARY, 2015)
        Espresso.onView(ViewMatchers.withId(R.id.compactcalendar_view)).perform(clickXY(60f, 150f))
        setIndicatorType(CompactCalendarView.FILL_LARGE_INDICATOR, CompactCalendarView.FILL_LARGE_INDICATOR, CompactCalendarView.FILL_LARGE_INDICATOR)
        takeScreenShot()
    }

    @Test
    fun testItDrawsIndicatorsBelowCurrentSelectedDayWithLargeHeight() { // test to make sure calendar does not draw event indicators below highlighted days
//Sun, 08 Feb 2015 00:00:00 GMT
        setHeight(400f)
        setDrawEventsBelowDayIndicators(true)
        setDate(Date(1423353600000L))
        addEvents(Calendar.FEBRUARY, 2015)
        Espresso.onView(ViewMatchers.withId(R.id.compactcalendar_view)).perform(clickXY(60f, 120f))
        takeScreenShot(800)
    }

    @Test
    fun testItDisplaysDaysFromOtherMonthsForFeb() { //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setShouldDrawDaysFromOtherMonths(true)
        takeScreenShot()
    }

    @Test
    fun testItDisplaysDaysFromOtherMonthsForFebRtl() {
        compactCalendarView!!.setIsRtl(true)
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setShouldDrawDaysFromOtherMonths(true)
        takeScreenShot()
    }

    @Test
    fun testItDisplaysDaysFromOtherMonthsForAfterScrollingFromFebToMarch() { //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setShouldDrawDaysFromOtherMonths(true)
        scrollCalendarForwardBy(1)
        takeScreenShot()
    }

    @Test
    fun testItDisplaysDaysFromOtherMonthsForAfterScrollingFromFebToMarchRtl() {
        compactCalendarView!!.setIsRtl(true)
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setShouldDrawDaysFromOtherMonths(true)
        scrollCalendarBackwardsBy(1)
        takeScreenShot()
    }

    @Test
    fun testItDisplaysDaysFromOtherMonthsForAfterScrollingFromFebToJan() { //Sun, 08 Feb 2015 00:00:00 GMT
        setShouldDrawDaysFromOtherMonths(true)
        setDate(Date(1423353600000L))
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        scrollCalendarBackwardsBy(1)
        takeScreenShot()
    }

    @Test
    fun testItDrawsSundayAsFirstDayOfMonthRtl() {
        compactCalendarView!!.setIsRtl(true)
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setFirstDayOfWeek(Calendar.SUNDAY)
        takeScreenShot()
    }

    @Test
    fun testItDrawsMondayAsFirstDayOfMonthRtl() {
        compactCalendarView!!.setIsRtl(true)
        // defaults to Monday
//Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        takeScreenShot()
    }

    @Test
    fun testItDrawsTuesdayAsFirstDayOfMonthRtl() {
        compactCalendarView!!.setIsRtl(true)
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setFirstDayOfWeek(Calendar.TUESDAY)
        takeScreenShot()
    }

    @Test
    fun testItDrawsWednesdayAsFirstDayOfMonthRtl() {
        compactCalendarView!!.setIsRtl(true)
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setFirstDayOfWeek(Calendar.WEDNESDAY)
        takeScreenShot()
    }

    @Test
    fun testItDrawsThursdayAsFirstDayOfMonthRtl() {
        compactCalendarView!!.setIsRtl(true)
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setFirstDayOfWeek(Calendar.THURSDAY)
        takeScreenShot()
    }

    @Test
    fun testItDrawsFridayAsFirstDayOfMonthRtl() {
        compactCalendarView!!.setIsRtl(true)
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setFirstDayOfWeek(Calendar.FRIDAY)
        takeScreenShot()
    }

    @Test
    fun testItDrawsSaturdayAsFirstDayOfMonthRtl() {
        compactCalendarView!!.setIsRtl(true)
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setFirstDayOfWeek(Calendar.SATURDAY)
        takeScreenShot()
    }

    @Test
    fun testItDrawsWedAsFirstDayWithFrenchLocaleRtl() {
        compactCalendarView!!.setIsRtl(true)
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setFirstDayOfWeek(Calendar.WEDNESDAY)
        Espresso.onView(ViewMatchers.withId(R.id.set_locale)).perform(clickXY(0f, 0f))
        setUseThreeLetterAbbreviation(true)
        takeScreenShot()
    }

    @Test
    fun testItDrawsSundayAsFirstDayOfMonth() { //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setFirstDayOfWeek(Calendar.SUNDAY)
        takeScreenShot()
    }

    @Test
    fun testItDrawsMondayAsFirstDayOfMonth() { // defaults to Monday
//Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        takeScreenShot()
    }

    @Test
    fun testItDrawsTuesdayAsFirstDayOfMonth() { //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setFirstDayOfWeek(Calendar.TUESDAY)
        takeScreenShot()
    }

    @Test
    fun testItDrawsWednesdayAsFirstDayOfMonth() { //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setFirstDayOfWeek(Calendar.WEDNESDAY)
        takeScreenShot()
    }

    @Test
    fun testItDrawsThursdayAsFirstDayOfMonth() { //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setFirstDayOfWeek(Calendar.THURSDAY)
        takeScreenShot()
    }

    @Test
    fun testItDrawsFridayAsFirstDayOfMonth() { //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setFirstDayOfWeek(Calendar.FRIDAY)
        takeScreenShot()
    }

    @Test
    fun testItDrawsSaturdayAsFirstDayOfMonth() { //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setFirstDayOfWeek(Calendar.SATURDAY)
        takeScreenShot()
    }

    @Test
    fun testItDrawsWednesdayAsFirstDayWithFrenchLocale() { //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setFirstDayOfWeek(Calendar.WEDNESDAY)
        Espresso.onView(ViewMatchers.withId(R.id.set_locale)).perform(clickXY(0f, 0f))
        setUseThreeLetterAbbreviation(true)
        takeScreenShot()
    }

    @Test
    fun testOnDayClickListenerIsCalledWhenLocaleIsFranceWithWedAsFirstDayOFWeek() {
        val listener = Mockito.mock(CompactCalendarViewListener::class.java)
        compactCalendarView!!.setListener(listener)
        val locale = Locale.FRANCE
        val timeZone = TimeZone.getTimeZone("Europe/Paris")
        val instance = Calendar.getInstance(timeZone, locale)
        // Thu, 05 Feb 2015 12:00:00 GMT - then set to midnight
        instance.timeInMillis = 1423137600000L
        instance[Calendar.HOUR_OF_DAY] = 0
        instance[Calendar.MINUTE] = 0
        instance[Calendar.SECOND] = 0
        instance[Calendar.MILLISECOND] = 0
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        setFirstDayOfWeek(Calendar.WEDNESDAY)
        Espresso.onView(ViewMatchers.withId(R.id.set_locale)).perform(clickXY(0f, 0f))
        Espresso.onView(ViewMatchers.withId(R.id.compactcalendar_view)).perform(clickXY(60f, 100f))
        //Thr, 05 Feb 2015 00:00:00 GMT - expected
        Mockito.verify(listener).onDayClick(instance.time)
        Mockito.verifyNoMoreInteractions(listener)
        takeScreenShot()
    }

    @Test
    fun testOnDayClickListenerIsCalled() {
        val listener = Mockito.mock(CompactCalendarViewListener::class.java)
        compactCalendarView!!.setListener(listener)
        val instance = Calendar.getInstance()
        // Thu, 03 Feb 2015 12:00:00 GMT - then set to midnight
        instance.timeInMillis = 1422921600000L
        instance[Calendar.HOUR_OF_DAY] = 0
        instance[Calendar.MINUTE] = 0
        instance[Calendar.SECOND] = 0
        instance[Calendar.MILLISECOND] = 0
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        Espresso.onView(ViewMatchers.withId(R.id.compactcalendar_view)).perform(clickXY(60f, 100f))
        //Thr, 03 Feb 2015 00:00:00 GMT - expected
        Mockito.verify(listener).onDayClick(instance.time)
        Mockito.verifyNoMoreInteractions(listener)
    }

    @Test
    fun testOnDayClickListenerIsCalledInRtl() {
        val listener = Mockito.mock(CompactCalendarViewListener::class.java)
        compactCalendarView!!.setListener(listener)
        compactCalendarView!!.setIsRtl(true)
        val instance = Calendar.getInstance()
        // Thu, 07 Feb 2015 12:00:00 GMT - then set to midnight
        instance.timeInMillis = 1423267200000L
        instance[Calendar.HOUR_OF_DAY] = 0
        instance[Calendar.MINUTE] = 0
        instance[Calendar.SECOND] = 0
        instance[Calendar.MILLISECOND] = 0
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        Espresso.onView(ViewMatchers.withId(R.id.compactcalendar_view)).perform(clickXY(60f, 100f))
        //Thr, 07 Feb 2015 00:00:00 GMT - expected
        Mockito.verify(listener).onDayClick(instance.time)
        Mockito.verifyNoMoreInteractions(listener)
    }

    // Using mocks for listener causes espresso to throw an error because the callback is called from within animation handler.
// Maybe a problem with espresso, for now manually check count.
    @Test
    @Throws(Throwable::class)
    fun testOpenedAndClosedListerCalledForExposeAnimationCalendar() { // calendar is opened by default.
        val listener: CompactCalendarAnimationListener = object : CompactCalendarAnimationListener {
            override fun onOpened() {
                onOpenedCallCount++
            }

            override fun onClosed() {
                onClosedCallCount++
            }
        }
        compactCalendarView!!.setAnimationListener(listener)
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        Espresso.onView(ViewMatchers.withId(R.id.show_with_animation_calendar)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.show_with_animation_calendar)).perform(ViewActions.click())
        waitForAnimationFinish()
        Assert.assertEquals(onClosedCallCount.toLong(), 1)
        Assert.assertEquals(onOpenedCallCount.toLong(), 1)
    }

    // Using mocks for listener causes espresso to throw an error because the callback is called from within animation handler.
// Maybe a problem with espresso, for now manually check count.
    @Test
    @Throws(Throwable::class)
    fun testOpenedAndClosedListerCalledForCalendar() { // calendar is opened by default.
        val listener: CompactCalendarAnimationListener = object : CompactCalendarAnimationListener {
            override fun onOpened() {
                onOpenedCallCount = onOpenedCallCount + 1
            }

            override fun onClosed() {
                onClosedCallCount++
            }
        }
        compactCalendarView!!.setAnimationListener(listener)
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        Espresso.onView(ViewMatchers.withId(R.id.slide_calendar)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.slide_calendar)).perform(ViewActions.click())
        waitForAnimationFinish()
        Assert.assertEquals(onClosedCallCount.toLong(), 1)
        Assert.assertEquals(onOpenedCallCount.toLong(), 1)
    }

    @Throws(Exception::class)
    private fun waitForAnimationFinish() {
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String? {
                return null
            }

            override fun checkCondition(): Boolean {
                return !compactCalendarView!!.isAnimating
            }
        })
    }

    @Test
    @Throws(Throwable::class)
    fun testItDoesNotThrowNullPointerWhenNoAnimationListenerIsSet() { //Sun, 08 Feb 2015 00:00:00 GMT
        compactCalendarView!!.setAnimationListener(null)
        setDate(Date(1423353600000L))
        Espresso.onView(ViewMatchers.withId(R.id.show_with_animation_calendar)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.slide_calendar)).perform(ViewActions.click())
    }

    @Test
    fun testItDrawsDifferentColorsForCurrentSelectedDay() { //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(Date(1423353600000L))
        compactCalendarView!!.setCurrentDayTextColor(Color.BLACK)
        compactCalendarView!!.setCurrentSelectedDayTextColor(Color.BLUE)
        takeScreenShot()
    }

    @Test
    fun testWhenShouldSelectFirstDayOfMonthOnScrollIsFalseItDoesNotSelectFIrstDayOfMonth() {
        compactCalendarView!!.shouldSelectFirstDayOfMonthOnScroll(false)
        setDate(Date(1423353600000L))
        scrollCalendarForwardBy(1)
        takeScreenShot()
    }

    // Nasty hack to get the toolbar to update the current month
// TODO sample code should be refactored to do this
    private fun syncToolbarDate() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val toolbar = activityRule.activity.supportActionBar
            toolbar!!.title = dateFormatForMonth!!.format(compactCalendarView!!.firstDayOfCurrentMonth)
        }
    }

    private fun setFirstDayOfWeek(dayOfWeek: Int) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync { compactCalendarView!!.setFirstDayOfWeek(dayOfWeek) }
    }

    private fun setUseThreeLetterAbbreviation(useThreeLetterAbbreviation: Boolean) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync { compactCalendarView!!.setUseThreeLetterAbbreviation(useThreeLetterAbbreviation) }
    }

    private fun setShouldDrawDaysFromOtherMonths(shouldDrawEventsBelowDayIndicators: Boolean) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync { compactCalendarView!!.displayOtherMonthDays(shouldDrawEventsBelowDayIndicators) }
    }

    private fun setDrawEventsBelowDayIndicators(shouldDrawEventsBelowDayIndicators: Boolean) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync { compactCalendarView!!.shouldDrawIndicatorsBelowSelectedDays(shouldDrawEventsBelowDayIndicators) }
    }

    private fun setIndicatorType(currentSelectedDayStyle: Int, eventStyle: Int, currentDayStyle: Int) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            compactCalendarView!!.setCurrentSelectedDayIndicatorStyle(currentSelectedDayStyle)
            compactCalendarView!!.setEventIndicatorStyle(eventStyle)
            compactCalendarView!!.setCurrentDayIndicatorStyle(currentDayStyle)
        }
    }

    private fun capture(name: String) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            compactCalendarView!!.requestLayout()
            ViewHelpers.setupView(mainContent)
                    .setExactHeightPx(mainContent!!.height)
                    .setExactWidthPx(mainContent!!.width)
                    .layout()
            safeSleep(200)
            Screenshot.snap(mainContent)
                    .setName(name)
                    .record()
        }
    }

    private fun setDate(date: Date) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            compactCalendarView!!.setCurrentDate(date)
            val toolbar = activityRule.activity.supportActionBar
            toolbar!!.title = dateFormatForMonth!!.format(compactCalendarView!!.firstDayOfCurrentMonth)
        }
    }

    private fun shouldSelectFirstDayOfMonthOnScroll(shouldSelectFirstDay: Boolean) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            compactCalendarView!!.shouldSelectFirstDayOfMonthOnScroll(shouldSelectFirstDay)
            val toolbar = activityRule.activity.supportActionBar
            toolbar!!.title = dateFormatForMonth!!.format(compactCalendarView!!.firstDayOfCurrentMonth)
        }
    }

    fun clickXY(x: Float, y: Float): ViewAction {
        val dm = activityRule.activity.resources.displayMetrics
        val spX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, x, dm)
        val spY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, y, dm)
        return GeneralClickAction(
                Tap.SINGLE,
                CoordinatesProvider { view ->
                    val screenPos = IntArray(2)
                    view.getLocationOnScreen(screenPos)
                    val screenX = screenPos[0] + spX
                    val screenY = screenPos[1] + spY
                    floatArrayOf(screenX, screenY)
                },
                Press.FINGER)
    }

    fun scroll(startX: Int, startY: Int, endX: Int, endY: Int): ViewAction {
        val dm = activityRule.activity.resources.displayMetrics
        val spStartX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, startX.toFloat(), dm)
        val spStartY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, startY.toFloat(), dm)
        val spEndX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, endX.toFloat(), dm)
        val spEndY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, endY.toFloat(), dm)
        return GeneralSwipeAction(
                Swipe.FAST,
                CoordinatesProvider { view ->
                    val screenPos = IntArray(2)
                    view.getLocationOnScreen(screenPos)
                    val screenX = screenPos[0] + spStartX
                    val screenY = screenPos[1] + spStartY
                    floatArrayOf(screenX, screenY)
                },
                CoordinatesProvider { view ->
                    val screenPos = IntArray(2)
                    view.getLocationOnScreen(screenPos)
                    val screenX = screenPos[0] + spEndX
                    val screenY = screenPos[1] + spEndY
                    floatArrayOf(screenX, screenY)
                },
                Press.FINGER)
    }

    private fun addEvents(month: Int, year: Int) {
        val context = compactCalendarView!!.context
        (context as Activity).runOnUiThread { compactCalendarView!!.addEvents(getEventsFor(month, year)) }
    }

    private fun getEventsFor(month: Int, year: Int): List<Event> {
        val currentCalender = Calendar.getInstance()
        currentCalender.time = Date()
        currentCalender[Calendar.DAY_OF_MONTH] = 1
        val firstDayOfMonth = currentCalender.time
        val events: MutableList<Event> = ArrayList()
        for (i in 0..5) {
            currentCalender.time = firstDayOfMonth
            if (month > -1) {
                currentCalender[Calendar.MONTH] = month
            }
            if (year > -1) {
                currentCalender[Calendar.ERA] = GregorianCalendar.AD
                currentCalender[Calendar.YEAR] = year
            }
            currentCalender.add(Calendar.DATE, i)
            setToMidnight(currentCalender)
            val timeInMillis = currentCalender.timeInMillis
            events.addAll(getEvents(timeInMillis, i))
        }
        return events
    }

    private fun getEvents(timeInMillis: Long, day: Int): List<Event> {
        return if (day < 2) {
            Arrays.asList(Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + Date(timeInMillis)))
        } else if (day > 2 && day <= 4) {
            Arrays.asList(
                    Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + Date(timeInMillis)),
                    Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + Date(timeInMillis)))
        } else {
            Arrays.asList(
                    Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + Date(timeInMillis)),
                    Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + Date(timeInMillis)),
                    Event(Color.argb(255, 70, 68, 65), timeInMillis, "Event 3 at " + Date(timeInMillis)))
        }
    }

    private fun setToMidnight(calendar: Calendar) {
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
    }

    fun setHeight(height: Float) {
        val context = compactCalendarView!!.context
        (context as Activity).runOnUiThread {
            val newHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, context.getResources().displayMetrics).toInt()
            compactCalendarView!!.layoutParams.height = newHeight
            compactCalendarView!!.setTargetHeight(newHeight)
            compactCalendarView!!.requestLayout()
            compactCalendarView!!.invalidate()
        }
    }

    private fun scrollCalendarForwardBy(months: Int) {
        for (i in 0 until months) {
            Espresso.onView(ViewMatchers.withId(R.id.compactcalendar_view)).perform(scroll(100, 100, -200, 0))
            safeSleep()
        }
    }

    private fun scrollCalendarBackwardsBy(months: Int) {
        for (i in 0 until months) {
            Espresso.onView(ViewMatchers.withId(R.id.compactcalendar_view)).perform(scroll(100, 10, 300, 0))
            safeSleep()
        }
    }

    private fun safeSleep(i: Int = 500) {
        try {
            Thread.sleep(i.toLong())
        } catch (e: InterruptedException) {
            Log.e(APPLICATION_TEST_TAG, "Error occurred while sleeping.", e)
        }
    }

    private fun takeScreenShot(height: Int = 600) {
        activityRule.activity.runOnUiThread {
            ViewHelpers.setupView(mainContent)
                    .setExactHeightDp(height)
                    .setExactWidthPx(mainContent!!.width)
                    .layout()
        }
        Screenshot.snap(mainContent)
                .record()
    }

    companion object {
        private const val APPLICATION_TEST_TAG = "ApplicationTest"
    }
}