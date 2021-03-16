package org.someth2say.storm.stat;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public class CountStat extends Stat {

    public AtomicInteger count = new AtomicInteger();

    @Override
    public void computeStep(Category bucket, ResponseData responseData) {
        count.incrementAndGet();
    }

    @Override
    public void computeEnd(Category bucket) {
    }

    @Override
    public Map<Object, Object> getStatResults() {
       return Map.of("count", this.count);
    }

}
