package com.github.sundeepk.compactcalendarview.domain

class Event {
    var color: Int
        private set
    var timeInMillis: Long
        private set
    var data: Any? = null
        private set

    constructor(color: Int, timeInMillis: Long) {
        this.color = color
        this.timeInMillis = timeInMillis
    }

    constructor(color: Int, timeInMillis: Long, data: Any?) {
        this.color = color
        this.timeInMillis = timeInMillis
        this.data = data
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val event = o as Event
        if (color != event.color) return false
        if (timeInMillis != event.timeInMillis) return false
        return !if (data != null) data != event.data else event.data != null
    }

    override fun hashCode(): Int {
        var result = color
        result = 31 * result + (timeInMillis xor (timeInMillis ushr 32)).toInt()
        result = 31 * result + if (data != null) data.hashCode() else 0
        return result
    }

    override fun toString(): String {
        return "Event{" +
                "color=" + color +
                ", timeInMillis=" + timeInMillis +
                ", data=" + data +
                '}'
    }
}
