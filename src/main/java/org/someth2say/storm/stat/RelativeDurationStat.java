package org.someth2say.storm.stat;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonValue;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public class RelativeDurationStat extends Stat {

    @JsonValue
    public double relativeDuration = 1d;
    
    private Duration durationsum = Duration.ZERO;

    @Override
    public synchronized void computeStep(Category bucket, ResponseData responseData) {
        durationsum = durationsum.plus(responseData.getDuration());
    }
    @Override
    public void computeEnd(Category bucket) {
        if (bucket.parent!=null){
            Duration parentDuration=bucket.parent.stats.values().stream()
                .filter(stat->stat.getClass().isAssignableFrom(RelativeDurationStat.class))
                .map(s->((RelativeDurationStat)s))
                .map(rds->rds.durationsum).findFirst().orElse(Duration.ZERO);
            relativeDuration = this.durationsum.toMillis()*1.0/parentDuration.toMillis();
        }
    }

}
