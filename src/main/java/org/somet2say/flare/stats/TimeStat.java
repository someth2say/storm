package org.somet2say.flare.stats;

import java.time.Instant;

import org.somet2say.flare.Category;
import org.somet2say.flare.ResponseData;

public class TimeStat implements Stat {

    public Instant startTime = Instant.MAX;
    public Instant endTime = Instant.MIN;

    @Override
    public synchronized void computeStep(Category bucket, ResponseData<String> responseData) {
        startTime = responseData.startTime.isBefore(startTime) ? responseData.startTime : startTime;
        endTime = responseData.endTime.isAfter(endTime) ? responseData.endTime : endTime;
    }

    @Override
    public void computeEnd(Category bucket) {
    }

    @Override
    public Stat newInstance() {
        return new TimeStat();
    }

}
