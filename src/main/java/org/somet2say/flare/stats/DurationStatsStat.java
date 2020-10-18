package org.somet2say.flare.stats;

import java.time.Duration;

import org.somet2say.flare.Category;
import org.somet2say.flare.ResponseData;

public class DurationStatsStat implements Stat {

    public Duration minDuration = Duration.ofSeconds(Long.MAX_VALUE);
    public Duration maxDuration = Duration.ZERO;

    @Override
    public synchronized void computeStep(Category bucket, ResponseData<String> responseData) {
        Duration responseDuration = Duration.between(responseData.startTime, responseData.endTime);
        minDuration = minDuration.compareTo(responseDuration)>0?responseDuration:minDuration;
        maxDuration = maxDuration.compareTo(responseDuration)<0?responseDuration:maxDuration;
    }

    @Override
    public void computeEnd(Category bucket) {
    }

    @Override
    public Stat newInstance() {
        return new DurationStatsStat();
    }

}
