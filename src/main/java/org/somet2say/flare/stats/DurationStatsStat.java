package org.somet2say.flare.stats;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.somet2say.flare.Category;
import org.somet2say.flare.ResponseData;

public class DurationStatsStat implements Stat {

    public Duration minDuration = Duration.ZERO;
    public Duration maxDuration = Duration.ZERO;
    public Duration mean = Duration.ZERO;
    public double variance;
    public double stdev;

    @Override
    public synchronized void computeStep(Category bucket, ResponseData<String> responseData) {
        Duration responseDuration = responseData.getDuration();
        minDuration = (minDuration == Duration.ZERO || minDuration.compareTo(responseDuration) > 0) ? responseDuration
                : minDuration;
        maxDuration = maxDuration.compareTo(responseDuration) < 0 ? responseDuration : maxDuration;
    }

    @Override
    public void computeEnd(Category bucket) {
        Collection<ResponseData<String>> responses = bucket.responseDatas;
        int numResponses = responses.size();
        if (numResponses > 0) {
            List<Duration> durations = responses.stream().map(ResponseData<String>::getDuration)
                    .collect(Collectors.toList());
            mean = durations.stream().reduce(Duration.ZERO, (d1, d2) -> d1.plus(d2)).dividedBy(numResponses);
            variance = durations.stream().mapToLong(d -> d.toMillis()).map(m -> m - mean.toMillis()).map(m -> m * m)
                    .sum();
            stdev = Math.sqrt(variance / numResponses);
        }

    }

    @Override
    public Stat newInstance() {
        return new DurationStatsStat();
    }

}
