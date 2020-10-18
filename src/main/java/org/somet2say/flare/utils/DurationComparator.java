package org.somet2say.flare.utils;

import java.time.Duration;
import java.util.Comparator;

public class DurationComparator implements Comparator<Duration> {

    @Override
    public int compare(Duration o1, Duration o2) {
        int cmp = Long.compare(o1.getSeconds(), o2.getSeconds());
        if (cmp != 0) {
            return cmp;
        }
        return o1.getNano() - o2.getNano();
    }
}
