package org.somet2say.flare.stats;

import java.time.Duration;

import org.somet2say.flare.Category;
import org.somet2say.flare.ResponseData;

public class DurationStat implements Stat {

    public Duration duration = Duration.ZERO;

    @Override
    public synchronized void computeStep(Category bucket, ResponseData<String> responseData) {
        duration = duration.plus(Duration.between(responseData.startTime, responseData.endTime));
    }

    @Override
    public void computeEnd(Category bucket) {
    }

    @Override
    public Stat newInstance() {
        return new DurationStat();
    }

}
