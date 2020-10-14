package org.somet2say.flare.stats;

import java.util.concurrent.atomic.AtomicInteger;

import org.somet2say.flare.Bucket;
import org.somet2say.flare.ResponseData;

public class CountStat implements Stat {

    public AtomicInteger count = new AtomicInteger();

    @Override
    public void computeStep(Bucket bucket, ResponseData<String> responseData) {
        count.incrementAndGet();
    }

    @Override
    public void computeEnd(Bucket bucket) {
    }

    public Stat newInstance(){
        return new CountStat();
    }

}
