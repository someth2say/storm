package org.someth2say.storm.stats;

import java.time.Duration;

import org.someth2say.storm.Category;
import org.someth2say.storm.ResponseData;

public class DurationSumStat implements Stat {

    public Duration durationsum = Duration.ZERO;

    @Override
    public synchronized void computeStep(Category bucket, ResponseData<String> responseData) {
        durationsum = durationsum.plus(responseData.getDuration());//Duration.between(responseData.startTime, responseData.endTime));
    }

    @Override
    public void computeEnd(Category bucket) {
    }

    @Override
    public Stat newInstance() {
        return new DurationSumStat();
    }

}
