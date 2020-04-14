package com.github.sundeepk.compactcalendarview.comparators

import com.github.sundeepk.compactcalendarview.domain.Event
import java.util.*

class EventComparator : Comparator<Event> {
    override fun compare(lhs: Event, rhs: Event): Int {
        return if (lhs.timeInMillis < rhs.timeInMillis) -1 else if (lhs.timeInMillis == rhs.timeInMillis) 0 else 1
    }
}
