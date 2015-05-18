# CompactCalendarView
CompactCalendarView is a simple calender view which provides scrolling between months. It's based on Java's Date and Calender classes. It provides a simple api to query for dates and listeners for specific events.  For example, when the calender has scrolled to a new month or a day has been selected.

# Example usage
It is possible to change the apreance of the view via a few properties. This includes the background color, text color, textsize color of the current day and the color of the first day of the month.

```xml
<com.github.sundeepk.CompactCalendarView
            xmlns:app="http://schemas.android.com/apk/res-auto"
            android:id="@+id/compactcalender_view"
            android:layout_width="fill_parent"
            android:paddingRight="10dp"
            android:paddingLeft="10dp"
            android:layout_height="250dp"
            app:textSize="12sp"
            app:calenderBackgroundColor="#ffe95451"
            app:calenderTextColor="#fff"
            app:firstDayOfMonthBackgroundColor="#E57373"
            app:currentDayBackgroundColor="#B71C1C" 
            />
```
