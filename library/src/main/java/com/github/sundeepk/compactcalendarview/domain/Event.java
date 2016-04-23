package com.github.sundeepk.compactcalendarview.domain;

import android.support.annotation.Nullable;

public class Event {

    private int color;
    private long timeStamp;
    private Object data;

    public Event(int color, long timeStamp) {
        this.color = color;
        this.timeStamp = timeStamp;
    }

    public Event(int color, long timeStamp, Object data) {
        this.color = color;
        this.timeStamp = timeStamp;
        this.data = data;
    }

    public int getColor() {
        return color;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Nullable
    public Object getData() {
        return data;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (color != event.color) return false;
        if (timeStamp != event.timeStamp) return false;
        if (data != null ? !data.equals(event.data) : event.data != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = color;
        result = 31 * result + (int) (timeStamp ^ (timeStamp >>> 32));
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "color=" + color +
                ", timeStamp=" + timeStamp +
                ", data=" + data +
                '}';
    }
}
