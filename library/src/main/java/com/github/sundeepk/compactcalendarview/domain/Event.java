package com.github.sundeepk.compactcalendarview.domain;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

public class Event {

    private int color;
    private long timeInMillis;
    private Object data;
    private Bitmap icon;

    public Event(int color, long timeInMillis) {
        this.color = color;
        this.timeInMillis = timeInMillis;
    }

    public Event(int color, long timeInMillis, Object data, Bitmap icon) {
        this.color = color;
        this.timeInMillis = timeInMillis;
        this.data = data;
        this.icon = icon;
    }

    public int getColor() {
        return color;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    @Nullable
    public Bitmap getIcon() {
        return icon;
    }

    @Nullable
    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Event{" +
                "color=" + color +
                ", timeInMillis=" + timeInMillis +
                ", data=" + data +
                ", icon=" + icon +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (color != event.color) return false;
        if (timeInMillis != event.timeInMillis) return false;
        if (data != null ? !data.equals(event.data) : event.data != null) return false;
        return icon != null ? icon.equals(event.icon) : event.icon == null;

    }

    @Override
    public int hashCode() {
        int result = color;
        result = 31 * result + (int) (timeInMillis ^ (timeInMillis >>> 32));
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (icon != null ? icon.hashCode() : 0);
        return result;
    }

}
