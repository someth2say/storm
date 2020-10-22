package org.someth2say.storm.utils;

import java.time.Instant;
import java.util.Comparator;

public class InstantComparator implements Comparator<Instant> {

    public static InstantComparator INSTANCE = new InstantComparator();

    @Override
    public int compare(Instant o1, Instant o2) {
        int cmp = Long.compare(o1.getEpochSecond(), o2.getEpochSecond());
        if (cmp != 0) {
            return cmp;
        }
        return o1.getNano() - o2.getNano();    
    }

}
