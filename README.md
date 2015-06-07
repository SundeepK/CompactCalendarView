# CompactCalendarView
CompactCalendarView is a simple calender view which provides scrolling between months. It's based on Java's Date and Calender classes. It provides a simple api to query for dates and listeners for specific events.  For example, when the calender has scrolled to a new month or a day has been selected.

![ScreenShot](https://raw.githubusercontent.com/SundeepK/CompactCalendarView/master/images/compact-calendar-view-example.png)


# Example usage
It is possible to change the apreance of the view via a few properties. This includes the background color, text color, textsize color of the current day and the color of the first day of the month.

```xml
    <com.github.sundeepk.compactcalendarview.CompactCalendarView
            xmlns:app="http://schemas.android.com/apk/res-auto"
            android:id="@+id/compactcalendar_view"
            android:layout_width="fill_parent"
            android:paddingRight="10dp"
            android:paddingLeft="10dp"
            android:layout_height="250dp"
            app:textSize="12sp"
            app:calendarBackgroundColor="#ffe95451"
            app:calendarTextColor="#fff"
            app:firstDayOfMonthBackgroundColor="#E57373"
            app:currentDayBackgroundColor="#B71C1C" 
            />
```
```gradle
dependencies {
    compile 'com.github.sundeepk:compact-calendar-view:1.1'
}

```

TODO:
* Make drawing calendar more efficient by drawing all in one loop (currently requires 3).
* Make drawing circles on current/first day based on width/height for each cell and remove any magic numbers there.
* Benchmark code and add results on readme. 
