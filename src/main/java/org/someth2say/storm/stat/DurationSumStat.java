package org.someth2say.storm.stat;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonValue;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public class DurationSumStat extends Stat {

    @JsonValue
    public Duration durationsum = Duration.ZERO;

    @Override
    public synchronized void computeStep(Category bucket, ResponseData responseData) {
        durationsum = durationsum.plus(responseData.getDuration());
    }

    @Override
    public void computeEnd(Category bucket) {
    }

}
