package sundeepk.github.com.sample

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.RelativeLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarAnimationListener
import com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarViewListener
import com.github.sundeepk.compactcalendarview.domain.Event
import java.text.SimpleDateFormat
import java.util.*

class CompactCalendarTab : Fragment() {

    val TAG = "MainActivity"
    private var currentCalender = Calendar.getInstance(Locale.getDefault())
    private var dateFormatForDisplaying = SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault())
    private var dateFormatForMonth = SimpleDateFormat("MMM - yyyy", Locale.getDefault())
    private var shouldShow = false
    private lateinit var compactCalendarView: CompactCalendarView
    private lateinit var toolbar: ActionBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mainTabView = inflater.inflate(R.layout.main_tab, container, false)
        val mutableBookings: MutableList<String?> = ArrayList()
        val bookingsListView = mainTabView.findViewById<ListView>(R.id.bookings_listview)
        val showPreviousMonthBut = mainTabView.findViewById<Button>(R.id.prev_button)
        val showNextMonthBut = mainTabView.findViewById<Button>(R.id.next_button)
        val slideCalendarBut = mainTabView.findViewById<Button>(R.id.slide_calendar)
        val showCalendarWithAnimationBut = mainTabView.findViewById<Button>(R.id.show_with_animation_calendar)
        val setLocaleBut = mainTabView.findViewById<Button>(R.id.set_locale)
        val removeAllEventsBut = mainTabView.findViewById<Button>(R.id.remove_all_events)
        val adapter: ArrayAdapter<*> = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, mutableBookings)
        bookingsListView.adapter = adapter
        compactCalendarView = mainTabView.findViewById(R.id.compactcalendar_view)
        // below allows you to configure color for the current day in the month
// compactCalendarView.setCurrentDayBackgroundColor(getResources().getColor(R.color.black));
// below allows you to configure colors for the current day the user has selected
// compactCalendarView.setCurrentSelectedDayBackgroundColor(getResources().getColor(R.color.dark_red));
        compactCalendarView.setUseThreeLetterAbbreviation(false)
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY)
        compactCalendarView.setIsRtl(false)
        compactCalendarView.displayOtherMonthDays(false)
        //compactCalendarView.setIsRtl(true);
        loadEvents()
        loadEventsForYear(2017)
        compactCalendarView.invalidate()
        logEventsByMonth(compactCalendarView)
        // below line will display Sunday as the first day of the week
        // compactCalendarView.setShouldShowMondayAsFirstDay(false);
        // disable scrolling calendar
        // compactCalendarView.shouldScrollMonth(false);
        // show days from other months as greyed out days
        // compactCalendarView.displayOtherMonthDays(true);
        // show Sunday as first day of month
        // compactCalendarView.setShouldShowMondayAsFirstDay(false);
        //set initial title
        toolbar = (activity as AppCompatActivity?)!!.supportActionBar!!
        toolbar.title = dateFormatForMonth.format(compactCalendarView.firstDayOfCurrentMonth)
        //set title on calendar scroll
        compactCalendarView.setListener(object : CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                toolbar.title = dateFormatForMonth.format(dateClicked)
                val bookingsFromMap = compactCalendarView.getEvents(dateClicked!!)
                Log.d(TAG, "inside onclick " + dateFormatForDisplaying.format(dateClicked))
                if (bookingsFromMap != null) {
                    Log.d(TAG, bookingsFromMap.toString())
                    mutableBookings.clear()
                    for (booking in bookingsFromMap) {
                        mutableBookings.add(booking!!.data as String?)
                    }
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                toolbar.title = dateFormatForMonth.format(firstDayOfNewMonth)
            }
        })
        showPreviousMonthBut.setOnClickListener { compactCalendarView.scrollLeft() }
        showNextMonthBut.setOnClickListener { compactCalendarView.scrollRight() }
        val showCalendarOnClickLis = getCalendarShowLis()
        slideCalendarBut.setOnClickListener(showCalendarOnClickLis)
        val exposeCalendarListener = getCalendarExposeLis()
        showCalendarWithAnimationBut.setOnClickListener(exposeCalendarListener)
        compactCalendarView.setAnimationListener(object : CompactCalendarAnimationListener {
            override fun onOpened() {}
            override fun onClosed() {}
        })
        setLocaleBut.setOnClickListener {
            val locale = Locale.FRANCE
            dateFormatForDisplaying = SimpleDateFormat("dd-M-yyyy hh:mm:ss a", locale)
            val timeZone = TimeZone.getTimeZone("Europe/Paris")
            dateFormatForDisplaying.timeZone = timeZone
            dateFormatForMonth.timeZone = timeZone
            compactCalendarView.setLocale(timeZone, locale)
            compactCalendarView.setUseThreeLetterAbbreviation(false)
            loadEvents()
            loadEventsForYear(2017)
            logEventsByMonth(compactCalendarView)
        }
        removeAllEventsBut.setOnClickListener { compactCalendarView.removeAllEvents() }
        // uncomment below to show indicators above small indicator events
        // compactCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);
        // uncomment below to open onCreate
        //openCalendarOnCreate(v);
        return mainTabView
    }

    fun getCalendarShowLis(): View.OnClickListener {
        return View.OnClickListener {
            if (!compactCalendarView.isAnimating) {
                if (shouldShow) {
                    compactCalendarView.showCalendar()
                } else {
                    compactCalendarView.hideCalendar()
                }
                shouldShow = !shouldShow
            }
        }
    }

    fun getCalendarExposeLis(): View.OnClickListener {
        return View.OnClickListener {
            if (!compactCalendarView.isAnimating) {
                if (shouldShow) {
                    compactCalendarView.showCalendarWithAnimation()
                } else {
                    compactCalendarView.hideCalendarWithAnimation()
                }
                shouldShow = !shouldShow
            }
        }
    }

    fun openCalendarOnCreate(v: View) {
        val layout = v.findViewById<RelativeLayout>(R.id.main_content)
        val vto = layout.viewTreeObserver
        vto.addOnGlobalLayoutListener {
            //layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            compactCalendarView.showCalendarWithAnimation()
        }
    }

    override fun onResume() {
        super.onResume()
        toolbar.title = dateFormatForMonth.format(compactCalendarView.firstDayOfCurrentMonth)
    }

    fun loadEvents() {
        addEvents(-1, -1)
        addEvents(Calendar.DECEMBER, -1)
        addEvents(Calendar.AUGUST, -1)
    }

    fun loadEventsForYear(year: Int) {
        addEvents(Calendar.DECEMBER, year)
        addEvents(Calendar.AUGUST, year)
    }

    fun logEventsByMonth(compactCalendarView: CompactCalendarView) {
        currentCalender.setTime(Date())
        currentCalender.set(Calendar.DAY_OF_MONTH, 1)
        currentCalender.set(Calendar.MONTH, Calendar.AUGUST)
        val dates = ArrayList<String>()
        for (e in compactCalendarView.getEventsForMonth(Date())) {
            dates.add(dateFormatForDisplaying.format(e.timeInMillis))
        }
        Log.d(TAG, "Events for Aug with simple date formatter: $dates")
        Log.d(TAG, "Events for Aug month using default local and timezone: " + compactCalendarView.getEventsForMonth(currentCalender.time))
    }

    fun addEvents(month: Int, year: Int) {
        currentCalender.time = Date()
        currentCalender.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = currentCalender.time
        for (i in 0..6) {
            currentCalender.time = firstDayOfMonth
            if (month > -1) {
                currentCalender.set(Calendar.MONTH, month)
            }
            if (year > -1) {
                currentCalender.set(Calendar.ERA, GregorianCalendar.AD)
                currentCalender.set(Calendar.YEAR, year)
            }
            currentCalender.add(Calendar.DATE, i)
            setToMidnight(currentCalender)
            val timeInMillis = currentCalender.timeInMillis

            val events = getEvents(timeInMillis, i)

            compactCalendarView.addEvents(events)
        }
    }

    fun getEvents(timeInMillis: Long, day: Int): List<Event> {
        return when {
            day < 2 -> {
                listOf(Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + Date(timeInMillis)))
            }
            day in 3..4 -> {
                listOf(
                        Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + Date(timeInMillis)),
                        Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + Date(timeInMillis)))
            }
            else -> {
                listOf(
                        Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + Date(timeInMillis)),
                        Event(Color.argb(255, 100, 68, 65), timeInMillis, "Event 2 at " + Date(timeInMillis)),
                        Event(Color.argb(255, 70, 68, 65), timeInMillis, "Event 3 at " + Date(timeInMillis)))
            }
        }
    }

    fun setToMidnight(calendar: Calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
    }
}