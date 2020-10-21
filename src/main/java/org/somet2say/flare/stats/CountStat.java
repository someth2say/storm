package org.somet2say.flare.stats;

import java.util.concurrent.atomic.AtomicInteger;

import org.somet2say.flare.Category;
import org.somet2say.flare.ResponseData;

public class CountStat implements Stat {

    public AtomicInteger count = new AtomicInteger();
    @Override
    public void computeStep(Category bucket, ResponseData<String> responseData) {
        count.incrementAndGet();
    }

    @Override
    public void computeEnd(Category bucket) {}

    public Stat newInstance() {
        return new CountStat();
    }

}
