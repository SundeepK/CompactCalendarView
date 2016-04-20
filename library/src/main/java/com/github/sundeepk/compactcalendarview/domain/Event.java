package com.github.sundeepk.compactcalendarview.domain;

public class Event {

    private int color;
    private long timeStamp;

    public Event(int color, long timeStamp) {
        this.color = color;
        this.timeStamp = timeStamp;
    }

    public int getColor() {
        return color;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (color != event.color) return false;
        if (timeStamp != event.timeStamp) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = color;
        result = 31 * result + (int) (timeStamp ^ (timeStamp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Event{" +
                "color=" + color +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
