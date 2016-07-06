package com.github.sundeepk.compactcalendarview.comparators;

import com.github.sundeepk.compactcalendarview.domain.Events;

import java.util.Comparator;

public class EventsComparator implements Comparator<Events> {

    @Override
    public int compare(Events lhs, Events rhs) {
        return lhs.getTimeInMillis() < rhs.getTimeInMillis() ? -1 : lhs.getTimeInMillis() == rhs.getTimeInMillis() ? 0 : 1;
    }
}
