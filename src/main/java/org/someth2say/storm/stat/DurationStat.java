package org.someth2say.storm.stat;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public class DurationStat implements Stat {

    public Duration minDuration = Duration.ZERO;
    public Duration maxDuration = Duration.ZERO;
    public Duration mean = Duration.ZERO;
    public double variance;
    public double stdev;

    @Override
    public Map<Object, Object> getStatResults() {
       return Map.of("duration.min", this.minDuration,
       "duration.max",this.maxDuration,
       "duration.mean",this.mean,
       "duration.variance", this.variance,
       "duration.stdev",this.stdev);
    }
    
    @Override
    public synchronized void computeStep(Category bucket, ResponseData responseData) {
        Duration responseDuration = responseData.getDuration();
        minDuration = (minDuration == Duration.ZERO || minDuration.compareTo(responseDuration) > 0) ? responseDuration : minDuration;
        maxDuration = maxDuration.compareTo(responseDuration) < 0 ? responseDuration : maxDuration;
    }

    @Override
    public void computeEnd(Category bucket) {
        Collection<ResponseData> responses = bucket.responseDatas;
        int numResponses = responses.size();
        if (numResponses > 0) {
            List<Duration> durations = responses.stream().map(ResponseData::getDuration)
                    .collect(Collectors.toList());
            mean = durations.stream().reduce(Duration.ZERO, (d1, d2) -> d1.plus(d2)).dividedBy(numResponses);
            variance = durations.stream().mapToLong(d -> d.toMillis()).map(m -> m - mean.toMillis()).map(m -> m * m)
                    .sum(); // /1000;
            stdev = Math.sqrt(variance / numResponses*1.0);
        }
    }

    @Override
    public Stat newInstance() {
        return new DurationStat();
    }

}
