# CompactCalendarView
CompactCalendarView is a simple calendar view which provides scrolling between months. It's based on Java's Date and Calendar classes. It provides a simple api to query for dates and listeners for specific events.  For example, when the calendar has scrolled to a new month or a day has been selected.
Still under active developmemt.
![ScreenShot](https://github.com/SundeepK/CompactCalendarView/blob/master/images/compact-calendar-view-example.png)

# Locale specific settings
It's possible to set the locale so that weekday column names are automatically set by the calendar.
```java
        CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        compactCalendarView.drawSmallIndicatorForEvents(true);
        compactCalendarView.setLocale(Locale.CHINESE);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
```
![ScreenShot](https://github.com/SundeepK/CompactCalendarView/blob/master/images/chinese-locale-daynames.png)

# Open/Close animations
The library supports opening/closing with or without animations. This is currently in beta (in a branch and will be avaible in gradle soon as a beta).

![ScreenShot](https://github.com/SundeepK/CompactCalendarView/blob/master/images/compact_calendar_animation.gif)

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
        app:compactCalendarTextSize="12sp"
        app:compactCalendarBackgroundColor="#ffe95451"
        app:compactCalendarTextColor="#fff"
        app:compactCalendarCurrentSelectedDayBackgroundColor="#E57373"
        app:compactCalendarCurrentDayBackgroundColor="#B71C1C"
        app:compactCalendarSelectedDayTextColor="#ff8000"
        app:compactCalendarCurrentDayTextColor="#0000ff"
        />

```
```gradle
dependencies {
    compile 'com.github.sundeepk:compact-calendar-view:1.8.2'
}

//smooth scrolling functionality
dependencies {
    compile 'com.github.sundeepk:compact-calendar-view:1.8.2-beta-smooth-scrolling'
}

//smooth scrolling functionality with open/close calendar
dependencies {
    compile 'com.github.sundeepk:compact-calendar-view:1.8.2-beta-smooth-scrolling-with-animations'
}


```
#Contributing  
Please raise an issue of the requirement so that a disscussion can take before any code is written.

TODO:
* Make drawing calendar more efficient by drawing all in one loop (currently requires 3).
* Make drawing circles on current/first day based on width/height for each cell and remove any magic numbers there.
* Benchmark code and add results on readme. 
 
```
The MIT License (MIT)

Copyright (c) [2015] [Sundeepk]

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
