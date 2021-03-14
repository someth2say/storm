package org.someth2say.storm.stat;

import java.time.Duration;
import java.util.Map;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public class DurationSumStat implements Stat {

    public Duration durationsum = Duration.ZERO;

    @Override
    public Map<Object, Object> getStatResults() {
       return Map.of("duration", this.durationsum);
    }
    @Override
    public synchronized void computeStep(Category bucket, ResponseData responseData) {
        durationsum = durationsum.plus(responseData.getDuration());
    }

    @Override
    public void computeEnd(Category bucket) {
    }

    @Override
    public Stat newInstance() {
        return new DurationSumStat();
    }

}
