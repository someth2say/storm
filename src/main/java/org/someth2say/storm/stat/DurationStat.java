package org.someth2say.storm.stat;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public class DurationStat extends Stat {

    public Long minDuration = Long.MAX_VALUE;
    public Long maxDuration = Long.MIN_VALUE;
    public Long mean;
    public double variance;
    public double stdev;
    public double confidence;

    @Override
    public synchronized void computeStep(Category bucket, ResponseData responseData) {
        long responseDuration = responseData.getDuration().toMillis();
        minDuration = Math.min(minDuration, responseDuration);
        maxDuration = Math.max(maxDuration, responseDuration);
    }

    @Override
    public void computeEnd(Category bucket) {
        Collection<ResponseData> responses = bucket.responseDatas;
        int numResponses = responses.size();
        if (numResponses > 0) {
            List<Long> durations = responses.stream().map(ResponseData::getDuration).map(Duration::toMillis)
                    .collect(Collectors.toList());
            mean = durations.stream().reduce(0l, (d1, d2) -> d1 + d2) / numResponses;
            variance = durations.stream().map(m -> m - mean).map(m -> m * m).reduce(0l, (d1, d2) -> d1 + d2);
            stdev = Math.sqrt(variance / numResponses * 1.0);
            confidence = 0.99 * (stdev / Math.sqrt(numResponses));
        }
    }

}
