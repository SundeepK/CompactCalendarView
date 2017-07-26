# CompactCalendarView
CompactCalendarView is a simple calendar view which provides scrolling between months. It's based on Java's Date and Calendar classes. It provides a simple api to query for dates and listeners for specific events.  For example, when the calendar has scrolled to a new month or a day has been selected.
Still under active development.

<img src="https://github.com/SundeepK/CompactCalendarView/blob/master/images/compact-calendar-view-example-multi-events.png" width="500">

# Contributing  
Please raise an issue of the requirement so that a discussion can take before any code is written, even if you intend to raise a pull request.

# Open/Close animations
The library supports opening/closing with or without animations. 

![ScreenShot](https://github.com/SundeepK/CompactCalendarView/blob/master/images/compact_calendar_animation.gif)

# Example usage
It is possible to change the appearance of the view via a few properties. This includes the background color, text color, textsize color of the current day and the color of the first day of the month.

```xml
    <com.github.sundeepk.compactcalendarview.CompactCalendarView
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/compactcalendar_view"
        android:layout_width="fill_parent"
        android:paddingRight="10dp"
        android:paddingLeft="10dp"
        android:layout_height="250dp"
        app:compactCalendarTargetHeight="250dp"
        app:compactCalendarTextSize="12sp"
        app:compactCalendarBackgroundColor="#ffe95451"
        app:compactCalendarTextColor="#fff"
        app:compactCalendarCurrentSelectedDayBackgroundColor="#E57373"
        app:compactCalendarCurrentDayBackgroundColor="#B71C1C"
        app:compactCalendarMultiEventIndicatorColor="#fff"
        />

```

Please see Sample app for full example.

```java
    // ... code omitted for brevity         
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        // Set first day of week to Monday, defaults to Monday so calling setFirstDayOfWeek is not necessary
        // Use constants provided by Java Calendar class
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
       
        // Add event 1 on Sun, 07 Jun 2015 18:20:51 GMT
        Event ev1 = new Event(Color.GREEN, 1433701251000L, "Some extra data that I want to store.");
        compactCalendar.addEvent(ev1);

        // Added event 2 GMT: Sun, 07 Jun 2015 19:10:51 GMT
        Event ev2 = new Event(Color.GREEN, 1433704251000L);
        compactCalendar.addEvent(ev2);

        // Query for events on Sun, 07 Jun 2015 GMT. 
        // Time is not relevant when querying for events, since events are returned by day. 
        // So you can pass in any arbitary DateTime and you will receive all events for that day.
        List<Event> events = compactCalendar.getEvents(1433701251000L); // can also take a Date object
        
        // events has size 2 with the 2 events inserted previously
        Log.d(TAG, "Events: " + events);

        // define a listener to receive callbacks when certain events happen.
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                Log.d(TAG, "Day was clicked: " + dateClicked + " with events " + events);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Log.d(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
            }
        });
    }

```

You can modify indicators using a preset of styles, below is an example, but few other combinations are also possible:

![ScreenShot](https://github.com/SundeepK/CompactCalendarView/blob/master/images/compact-calendar-customised-indicators.png)

Note that the calendar makes no attempt to de-duplicate events for the same exact DateTime. This is something that you must handle your self if it is important to your use case.

# Locale specific settings
It's possible to set the locale so that weekday column names are automatically set by the calendar.
```java
        CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendarView.setLocale(Locale.CHINESE);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
```

<img src="https://github.com/SundeepK/CompactCalendarView/blob/master/images/chinese-locale-daynames.png" width="400">

```gradle
dependencies {
    compile 'com.github.sundeepk:compact-calendar-view:2.0.2.2'
}
```

RTL support beta for right-to-left languages
```gradle
dependencies {
    compile 'com.github.sundeepk:compact-calendar-view:2.0.3-beta'
}
```

```
The MIT License (MIT)

Copyright (c) [2017] [Sundeepk]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
