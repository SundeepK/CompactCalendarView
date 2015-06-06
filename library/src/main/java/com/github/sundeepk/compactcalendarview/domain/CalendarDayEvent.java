package com.github.sundeepk.compactcalendarview.domain;

public class CalendarDayEvent {

    private final long timeInMillis;
    private final int color;

    public CalendarDayEvent(final long timeInMillis, final int color) {
        this.timeInMillis = timeInMillis;
        this.color = color;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public int getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarDayEvent event = (CalendarDayEvent) o;

        if (color != event.color) return false;
        if (timeInMillis != event.timeInMillis) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (timeInMillis ^ (timeInMillis >>> 32));
        result = 31 * result + color;
        return result;
    }

    @Override
    public String
    toString() {
        return "CalendarDayEvent{" +
                "timeInMillis=" + timeInMillis +
                ", color=" + color +
                '}';
    }
}
