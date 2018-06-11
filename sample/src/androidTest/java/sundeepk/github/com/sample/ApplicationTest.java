package sundeepk.github.com.sample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.action.Tap;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.azimolabs.conditionwatcher.ConditionWatcher;
import com.azimolabs.conditionwatcher.Instruction;
import com.facebook.testing.screenshot.Screenshot;
import com.facebook.testing.screenshot.ViewHelpers;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarAnimationListener;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarViewListener;
import static com.github.sundeepk.compactcalendarview.CompactCalendarView.FILL_LARGE_INDICATOR;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {

    private static final String APPLICATION_TEST_TAG = "ApplicationTest";

    private SimpleDateFormat dateFormatForMonth;
    private CompactCalendarView compactCalendarView;

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    private View mainContent;
    private int onClosedCallCount = 0;
    private int onOpenedCallCount = 0;

    @Before
    public void setUp() throws Exception {
        Locale.setDefault(Locale.ENGLISH);
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
        compactCalendarView = (CompactCalendarView) activityRule.getActivity().findViewById(R.id.compactcalendar_view);
        mainContent = activityRule.getActivity().findViewById(R.id.parent);
        onClosedCallCount = 0;
        onOpenedCallCount = 0;
    }

    @Test
    public void testItDrawsEventsRtl(){
        Calendar currentCalender = Calendar.getInstance();
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
        currentCalender.set(Calendar.ERA, GregorianCalendar.AD);
        currentCalender.set(Calendar.YEAR, 2015);
        currentCalender.set(Calendar.MONTH, Calendar.MARCH);

        compactCalendarView.setIsRtl(true);
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        addEvents(Calendar.FEBRUARY, 2015);
        addEvents(Calendar.MARCH, 2015);
        scrollCalendarBackwardsBy(1);

        assertEquals(getEventsFor(Calendar.MARCH, 2015), compactCalendarView.getEventsForMonth(currentCalender.getTime()));

        syncToolbarDate();

        takeScreenShot();
    }

    @Test
    public void testItDrawsEventIndicatorsBelowHighlightedDayIndicators(){
        setDrawEventsBelowDayIndicators(true);
        setDate(new Date(1423094400000L));
        addEvents(Calendar.FEBRUARY, 2015);
        takeScreenShot();

    }

    @Test
    public void testItDrawsFillLargeIndicatorForEventsWhenDrawEventsBelowDayIndicatorsIsTrue() {
        // test to make sure calendar does not draw event indicators below highlighted days
        // when the style is FILL_LARGE_INDICATOR
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDrawEventsBelowDayIndicators(true);
        setDate(new Date(1423353600000L));
        addEvents(Calendar.FEBRUARY, 2015);
        onView(withId(R.id.compactcalendar_view)).perform(clickXY(60, 150));
        setIndicatorType(FILL_LARGE_INDICATOR, FILL_LARGE_INDICATOR, FILL_LARGE_INDICATOR);
        takeScreenShot();
    }

    @Test
    public void testItDrawsIndicatorsBelowCurrentSelectedDayWithLargeHeight() {
        // test to make sure calendar does not draw event indicators below highlighted days
        //Sun, 08 Feb 2015 00:00:00 GMT
        setHeight(400);
        setDrawEventsBelowDayIndicators(true);
        setDate(new Date(1423353600000L));
        addEvents(Calendar.FEBRUARY, 2015);
        onView(withId(R.id.compactcalendar_view)).perform(clickXY(60, 120));
        takeScreenShot(800);
    }

    @Test
    public void testItDisplaysDaysFromOtherMonthsForFeb(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setShouldDrawDaysFromOtherMonths(true);
        takeScreenShot();
    }

    @Test
    public void testItDisplaysDaysFromOtherMonthsForFebRtl(){
        compactCalendarView.setIsRtl(true);
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setShouldDrawDaysFromOtherMonths(true);
        takeScreenShot();
    }

    @Test
    public void testItDisplaysDaysFromOtherMonthsForAfterScrollingFromFebToMarch(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setShouldDrawDaysFromOtherMonths(true);
        scrollCalendarForwardBy(1);
        takeScreenShot();
    }

    @Test
    public void testItDisplaysDaysFromOtherMonthsForAfterScrollingFromFebToMarchRtl(){
        compactCalendarView.setIsRtl(true);
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setShouldDrawDaysFromOtherMonths(true);
        scrollCalendarBackwardsBy(1);
        takeScreenShot();
    }

    @Test
    public void testItDisplaysDaysFromOtherMonthsForAfterScrollingFromFebToJan(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setShouldDrawDaysFromOtherMonths(true);
        setDate(new Date(1423353600000L));
        getInstrumentation().waitForIdleSync();
        scrollCalendarBackwardsBy(1);
        takeScreenShot();
    }


    @Test
    public void testItDrawsSundayAsFirstDayOfMonthRtl(){
        compactCalendarView.setIsRtl(true);
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setFirstDayOfWeek(Calendar.SUNDAY);
        takeScreenShot();
    }

    @Test
    public void testItDrawsMondayAsFirstDayOfMonthRtl(){
        compactCalendarView.setIsRtl(true);
        // defaults to Monday
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        takeScreenShot();
    }

    @Test
    public void testItDrawsTuesdayAsFirstDayOfMonthRtl(){
        compactCalendarView.setIsRtl(true);
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setFirstDayOfWeek(Calendar.TUESDAY);
        takeScreenShot();
    }

    @Test
    public void testItDrawsWednesdayAsFirstDayOfMonthRtl(){
        compactCalendarView.setIsRtl(true);
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setFirstDayOfWeek(Calendar.WEDNESDAY);
        takeScreenShot();
    }

    @Test
    public void testItDrawsThursdayAsFirstDayOfMonthRtl(){
        compactCalendarView.setIsRtl(true);
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setFirstDayOfWeek(Calendar.THURSDAY);
        takeScreenShot();
    }

    @Test
    public void testItDrawsFridayAsFirstDayOfMonthRtl(){
        compactCalendarView.setIsRtl(true);
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setFirstDayOfWeek(Calendar.FRIDAY);
        takeScreenShot();
    }

    @Test
    public void testItDrawsSaturdayAsFirstDayOfMonthRtl(){
        compactCalendarView.setIsRtl(true);
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setFirstDayOfWeek(Calendar.SATURDAY);
        takeScreenShot();
    }

    @Test
    public void testItDrawsWedAsFirstDayWithFrenchLocaleRtl(){
        compactCalendarView.setIsRtl(true);
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setFirstDayOfWeek(Calendar.WEDNESDAY);
        onView(withId(R.id.set_locale)).perform(clickXY(0, 0));
        setUseThreeLetterAbbreviation(true);
        takeScreenShot();
    }


    @Test
    public void testItDrawsSundayAsFirstDayOfMonth(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setFirstDayOfWeek(Calendar.SUNDAY);
        takeScreenShot();
    }

    @Test
    public void testItDrawsMondayAsFirstDayOfMonth(){
        // defaults to Monday
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        takeScreenShot();
    }

    @Test
    public void testItDrawsTuesdayAsFirstDayOfMonth(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setFirstDayOfWeek(Calendar.TUESDAY);
        takeScreenShot();
    }

    @Test
    public void testItDrawsWednesdayAsFirstDayOfMonth(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setFirstDayOfWeek(Calendar.WEDNESDAY);
        takeScreenShot();
    }

    @Test
    public void testItDrawsThursdayAsFirstDayOfMonth(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setFirstDayOfWeek(Calendar.THURSDAY);
        takeScreenShot();
    }

    @Test
    public void testItDrawsFridayAsFirstDayOfMonth(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setFirstDayOfWeek(Calendar.FRIDAY);
        takeScreenShot();
    }

    @Test
    public void testItDrawsSaturdayAsFirstDayOfMonth(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setFirstDayOfWeek(Calendar.SATURDAY);
        takeScreenShot();
    }

    @Test
    public void testItDrawsWednesdayAsFirstDayWithFrenchLocale(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setFirstDayOfWeek(Calendar.WEDNESDAY);
        onView(withId(R.id.set_locale)).perform(clickXY(0, 0));
        setUseThreeLetterAbbreviation(true);
        takeScreenShot();
    }

    @Test
    public void testOnDayClickListenerIsCalledWhenLocaleIsFranceWithWedAsFirstDayOFWeek(){
        CompactCalendarViewListener listener = mock(CompactCalendarViewListener.class);
        compactCalendarView.setListener(listener);

        Locale locale = Locale.FRANCE;
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Paris");
        Calendar instance = Calendar.getInstance(timeZone, locale);
        // Thu, 05 Feb 2015 12:00:00 GMT - then set to midnight
        instance.setTimeInMillis(1423137600000L);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);

        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        setFirstDayOfWeek(Calendar.WEDNESDAY);
        onView(withId(R.id.set_locale)).perform(clickXY(0, 0));
        onView(withId(R.id.compactcalendar_view)).perform(clickXY(60, 100));

        //Thr, 05 Feb 2015 00:00:00 GMT - expected
        verify(listener).onDayClick(instance.getTime());
        verifyNoMoreInteractions(listener);
        takeScreenShot();
    }

    @Test
    public void testOnDayClickListenerIsCalled(){
        CompactCalendarViewListener listener = mock(CompactCalendarViewListener.class);
        compactCalendarView.setListener(listener);

        Calendar instance = Calendar.getInstance();
        // Thu, 03 Feb 2015 12:00:00 GMT - then set to midnight
        instance.setTimeInMillis(1422921600000L);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);

        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        onView(withId(R.id.compactcalendar_view)).perform(clickXY(60, 100));

        //Thr, 03 Feb 2015 00:00:00 GMT - expected
        verify(listener).onDayClick(instance.getTime());
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testOnDayClickListenerIsCalledInRtl(){
        CompactCalendarViewListener listener = mock(CompactCalendarViewListener.class);
        compactCalendarView.setListener(listener);
        compactCalendarView.setIsRtl(true);

        Calendar instance = Calendar.getInstance();
        // Thu, 07 Feb 2015 12:00:00 GMT - then set to midnight
        instance.setTimeInMillis(1423267200000L);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);

        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        onView(withId(R.id.compactcalendar_view)).perform(clickXY(60, 100));

        //Thr, 07 Feb 2015 00:00:00 GMT - expected
        verify(listener).onDayClick(instance.getTime());
        verifyNoMoreInteractions(listener);
    }

    // Using mocks for listener causes espresso to throw an error because the callback is called from within animation handler.
    // Maybe a problem with espresso, for now manually check count.
    @Test
    public void testOpenedAndClosedListerCalledForExposeAnimationCalendar() throws Throwable {
        // calendar is opened by default.
        CompactCalendarAnimationListener listener = new CompactCalendarAnimationListener() {
            @Override
            public void onOpened() {
                onOpenedCallCount++;
            }

            @Override
            public void onClosed() {
                onClosedCallCount++;
            }
        };
        compactCalendarView.setAnimationListener(listener);

        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        onView(withId(R.id.show_with_animation_calendar)).perform(click());
        onView(withId(R.id.show_with_animation_calendar)).perform(click());

        waitForAnimationFinish();

        assertEquals(onClosedCallCount, 1);
        assertEquals(onOpenedCallCount, 1);
    }

    // Using mocks for listener causes espresso to throw an error because the callback is called from within animation handler.
    // Maybe a problem with espresso, for now manually check count.
    @Test
    public void testOpenedAndClosedListerCalledForCalendar() throws Throwable {
        // calendar is opened by default.
        CompactCalendarAnimationListener listener = new CompactCalendarAnimationListener() {
            @Override
            public void onOpened() {
                onOpenedCallCount = onOpenedCallCount + 1;
            }

            @Override
            public void onClosed() {
                onClosedCallCount++;
            }
        };
        compactCalendarView.setAnimationListener(listener);

        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        onView(withId(R.id.slide_calendar)).perform(click());
        onView(withId(R.id.slide_calendar)).perform(click());

        waitForAnimationFinish();

        assertEquals(onClosedCallCount, 1);
        assertEquals(onOpenedCallCount, 1);
    }

    private void waitForAnimationFinish() throws Exception {
        ConditionWatcher.waitForCondition(new Instruction() {
            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public boolean checkCondition() {
                return !compactCalendarView.isAnimating();
            }
        });
    }

    @Test
    public void testItDoesNotThrowNullPointerWhenNoAnimationListenerIsSet() throws Throwable {
        //Sun, 08 Feb 2015 00:00:00 GMT
        compactCalendarView.setAnimationListener(null);
        setDate(new Date(1423353600000L));
        onView(withId(R.id.show_with_animation_calendar)).perform(click());
        onView(withId(R.id.slide_calendar)).perform(click());
    }

    @Test
    public void testItDrawsDifferentColorsForCurrentSelectedDay(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        compactCalendarView.setCurrentDayTextColor(Color.BLACK);
        compactCalendarView.setCurrentSelectedDayTextColor(Color.BLUE);
        takeScreenShot();
    }

    @Test
    public void testWhenShouldSelectFirstDayOfMonthOnScrollIsFalseItDoesNotSelectFIrstDayOfMonth()  {
        compactCalendarView.shouldSelectFirstDayOfMonthOnScroll(false);
        setDate(new Date(1423353600000L));
        scrollCalendarForwardBy(1);
        takeScreenShot();
    }

    // Nasty hack to get the toolbar to update the current month
    // TODO sample code should be refactored to do this
    private void syncToolbarDate(){
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ActionBar toolbar = activityRule.getActivity().getSupportActionBar();
                toolbar.setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
            }
        });
    }

    private void setFirstDayOfWeek(final int dayOfWeek) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                compactCalendarView.setFirstDayOfWeek(dayOfWeek);
            }
        });
    }

    private void setUseThreeLetterAbbreviation(final boolean useThreeLetterAbbreviation) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                compactCalendarView.setUseThreeLetterAbbreviation(useThreeLetterAbbreviation);
            }
        });
    }

    private void setShouldDrawDaysFromOtherMonths(final boolean shouldDrawEventsBelowDayIndicators) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                compactCalendarView.displayOtherMonthDays(shouldDrawEventsBelowDayIndicators);
            }
        });
    }

    private void setDrawEventsBelowDayIndicators(final boolean shouldDrawEventsBelowDayIndicators) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                compactCalendarView.shouldDrawIndicatorsBelowSelectedDays(shouldDrawEventsBelowDayIndicators);
            }
        });
    }

    private void setIndicatorType(final int currentSelectedDayStyle, final int eventStyle, final int currentDayStyle) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                compactCalendarView.setCurrentSelectedDayIndicatorStyle(currentSelectedDayStyle);
                compactCalendarView.setEventIndicatorStyle(eventStyle);
                compactCalendarView.setCurrentDayIndicatorStyle(currentDayStyle);
            }
        });
    }

    private void capture(final String name) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                compactCalendarView.requestLayout();
                ViewHelpers.setupView(mainContent)
                        .setExactHeightPx(mainContent.getHeight())
                        .setExactWidthPx(mainContent.getWidth())
                        .layout();
                safeSleep(200);
                Screenshot.snap(mainContent)
                        .setName(name)
                        .record();
            }
        });
    }

    private void setDate(final Date date) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                compactCalendarView.setCurrentDate(date);
                ActionBar toolbar = activityRule.getActivity().getSupportActionBar();
                toolbar.setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
            }
        });
    }

    private void shouldSelectFirstDayOfMonthOnScroll(final boolean shouldSelectFirstDay) {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                compactCalendarView.shouldSelectFirstDayOfMonthOnScroll(shouldSelectFirstDay);
                ActionBar toolbar = activityRule.getActivity().getSupportActionBar();
                toolbar.setTitle(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
            }
        });
    }

    public ViewAction clickXY(final float x, final float y){
        final DisplayMetrics dm = activityRule.getActivity().getResources().getDisplayMetrics() ;
        final float spX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, x, dm);
        final float spY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, y, dm);
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + spX;
                        final float screenY = screenPos[1] + spY;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }

    public ViewAction scroll(final int startX, final int startY, final int endX, final int endY){
        final DisplayMetrics dm = activityRule.getActivity().getResources().getDisplayMetrics() ;
        final float spStartX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, startX, dm);
        final float spStartY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, startY, dm);
        final float spEndX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, endX, dm);
        final float spEndY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, endY, dm);
        return new GeneralSwipeAction(
                Swipe.FAST,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + spStartX;
                        final float screenY = screenPos[1] + spStartY;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + spEndX;
                        final float screenY = screenPos[1] + spEndY;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
    }

    private void addEvents(final int month, final int year) {
        Context context = compactCalendarView.getContext();
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                compactCalendarView.addEvents(getEventsFor(month, year));
            }
        });
    }

    private List<Event> getEventsFor(final int month, final int year){
        Calendar currentCalender = Calendar.getInstance();
        currentCalender.setTime(new Date());
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = currentCalender.getTime();
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            currentCalender.setTime(firstDayOfMonth);
            if (month > -1) {
                currentCalender.set(Calendar.MONTH, month);
            }
            if (year > -1) {
                currentCalender.set(Calendar.ERA, GregorianCalendar.AD);
                currentCalender.set(Calendar.YEAR, year);
            }
            currentCalender.add(Calendar.DATE, i);
            setToMidnight(currentCalender);
            long timeInMillis = currentCalender.getTimeInMillis();
            events.addAll(getEvents(timeInMillis, i));
        }
        return events;
    }

    private List<Event> getEvents(long timeInMillis, int day) {
        if (day < 2) {
            return Arrays.asList(new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)));
        } else if ( day > 2 && day <= 4) {
            return Arrays.asList(
                    new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)),
                    new Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + new Date(timeInMillis)));
        } else {
            return Arrays.asList(
                    new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis) ),
                    new Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + new Date(timeInMillis)),
                    new Event(Color.argb(255, 70, 68, 65), timeInMillis, "Event 3 at " + new Date(timeInMillis)));
        }
    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public void setHeight(final float height) {
        final Context context = compactCalendarView.getContext();
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int newHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, context.getResources().getDisplayMetrics());
                compactCalendarView.getLayoutParams().height = newHeight;
                compactCalendarView.setTargetHeight(newHeight);
                compactCalendarView.requestLayout();
                compactCalendarView.invalidate();
            }
        });
    }

    private void scrollCalendarForwardBy(int months) {
        for (int i =0; i < months; i++) {
            onView(withId(R.id.compactcalendar_view)).perform(scroll(100, 100, -200, 0));
            safeSleep();
        }
    }

    private void scrollCalendarBackwardsBy(int months) {
        for (int i =0; i < months; i++) {
            onView(withId(R.id.compactcalendar_view)).perform(scroll(100, 10, 300, 0));
            safeSleep();
        }
    }

    private void safeSleep() {
        safeSleep(500);
    }

    private void safeSleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            Log.e(APPLICATION_TEST_TAG, "Error occurred while sleeping.", e);
        }
    }

    private void takeScreenShot(final int height) {
        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewHelpers.setupView(mainContent)
                        .setExactHeightDp(height)
                        .setExactWidthPx(mainContent.getWidth())
                        .layout();
            }
        });

        Screenshot.snap(mainContent)
                .record();
    }

    private void takeScreenShot() {
        takeScreenShot(600);
    }
}