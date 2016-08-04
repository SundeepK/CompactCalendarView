package sundeepk.github.com.sample;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static android.support.test.espresso.Espresso.onView;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ApplicationTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private CompactCalendarView.CompactCalendarViewListener listener;
    private CompactCalendarView compactCalendarView;
    private MainActivity activity;

    public ApplicationTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
        listener = mock(CompactCalendarView.CompactCalendarViewListener.class);
        compactCalendarView = (CompactCalendarView) activity.findViewById(R.id.compactcalendar_view);
        compactCalendarView.setListener(listener);
    }

    @Test
    public void testOnMonthScrollListenerIsCalled(){
        compactCalendarView.shouldScrollMonth(false);

        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        onView(ViewMatchers.withId(R.id.compactcalendar_view)).perform(scroll(100, 100, -100, 0));

        verifyNoMoreInteractions(listener);

        compactCalendarView.shouldScrollMonth(true);
    }

    @Test
    public void testItDoesNotScrollWhenScrollingIsDisabled(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        onView(ViewMatchers.withId(R.id.compactcalendar_view)).perform(scroll(100, 100, -100, 0));

        //Sun, 01 Mar 2015 00:00:00 GMT - expected
        verify(listener).onMonthScroll(new Date(1425168000000L));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testOnDayClickListenerIsCalled(){
        //Sun, 08 Feb 2015 00:00:00 GMT
        setDate(new Date(1423353600000L));
        onView(ViewMatchers.withId(R.id.compactcalendar_view)).perform(clickXY(60, 100));

        //Tue, 03 Feb 2015 00:00:00 GMT - expected
        verify(listener).onDayClick(new Date(1422921600000L));
        verifyNoMoreInteractions(listener);
    }

    private void setDate(final Date date) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                compactCalendarView.setCurrentDate(date);
            }
        });
    }

    public ViewAction clickXY(final float x, final float y){
        final DisplayMetrics dm = activity.getResources().getDisplayMetrics() ;
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
        final DisplayMetrics dm = activity.getResources().getDisplayMetrics() ;
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

}