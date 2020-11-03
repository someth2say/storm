package org.someth2say.storm.stats;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.someth2say.storm.Category;
import org.someth2say.storm.ResponseData;

public class CountStat implements Stat {

    public AtomicInteger count = new AtomicInteger();

    @Override
    public void computeStep(Category bucket, ResponseData<String> responseData) {
        count.incrementAndGet();
    }

    @Override
    public void computeEnd(Category bucket) {
    }

    public Stat newInstance() {
        return new CountStat();
    }

    @Override
    public Map<Object, Object> getStatResults() {
       return Map.of("count", this.count);
    }

}
