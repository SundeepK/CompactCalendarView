package com.github.sundeepk.compactcalendarview

import com.github.sundeepk.compactcalendarview.domain.Event

class Events(val timeInMillis: Long, val events: MutableList<Event>) {

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val event = o as Events
        if (timeInMillis != event.timeInMillis) return false
        return events == event.events
    }

    override fun hashCode(): Int {
        var result = events.hashCode() ?: 0
        result = 31 * result + (timeInMillis xor (timeInMillis ushr 32)).toInt()
        return result
    }

    override fun toString(): String {
        return "Events{" +
                "events=" + events +
                ", timeInMillis=" + timeInMillis +
                '}'
    }
}
