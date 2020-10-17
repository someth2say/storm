package org.somet2say.flare.stats;

import java.util.HashSet;
import java.util.Set;

import org.somet2say.flare.Bucket;
import org.somet2say.flare.ResponseData;

public class HeadersStat implements Stat {

    public Set<String> headers = new HashSet<>();
    @Override
    public synchronized void computeStep(Bucket bucket, ResponseData<String> responseData) {
        if (responseData.response!=null)
            headers.addAll(responseData.response.headers().map().keySet());
    }

    @Override
    public void computeEnd(Bucket bucket) {
    }

    public Stat newInstance(){
        return new CountStat();
    }

}
