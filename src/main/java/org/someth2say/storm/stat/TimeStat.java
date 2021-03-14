package org.someth2say.storm.stat;

import java.time.Instant;
import java.util.Map;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public class TimeStat implements Stat {

    public Instant startTime = Instant.MAX;
    public Instant endTime = Instant.MIN;

    @Override
    public Map<Object, Object> getStatResults() {
       return Map.of("time.start", this.startTime,
       "time.end",this.endTime, "time.span", java.time.Duration.between(this.startTime, this.endTime));
    }
    
    @Override
    public synchronized void computeStep(Category bucket, ResponseData responseData) {
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
