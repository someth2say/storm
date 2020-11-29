package org.someth2say.storm.stat;

import java.time.Duration;
import java.util.Map;

import org.someth2say.storm.Category;
import org.someth2say.storm.ResponseData;

public class RelativeDurationStat implements Stat {

    public double relativeDuration = 1d;
    private Duration durationsum = Duration.ZERO;

    @Override
    public Map<Object, Object> getStatResults() {
       return Map.of("relativeDuration", this.relativeDuration);
    }

    @Override
    public synchronized void computeStep(Category bucket, ResponseData<String> responseData) {
        durationsum = durationsum.plus(responseData.getDuration());
    }
    @Override
    public void computeEnd(Category bucket) {
        if (bucket.parent!=null){
            Duration parentDuration=bucket.parent.statObjs.stream()
                .filter(stat->stat.getClass().isAssignableFrom(RelativeDurationStat.class)).map(s->((RelativeDurationStat)s))
                .map(rds->rds.durationsum).findFirst().orElse(Duration.ZERO);
            relativeDuration= this.durationsum.toMillis()*1.0/parentDuration.toMillis();
        }
    }

    public Stat newInstance() {
        return new RelativeCountStat();
    }

}