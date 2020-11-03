package org.someth2say.storm.stats;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.someth2say.storm.Category;
import org.someth2say.storm.ResponseData;

public class URLSStat implements Stat {

    public Collection<String> urls = new HashSet<>();
    @Override
    public Map<Object, Object> getStatResults() {
       return Map.of("urls", this.urls);
    }
    
    @Override
    public synchronized void computeStep(Category bucket, ResponseData<String> responseData) {
        urls.add(responseData.request.uri().toString());
    }

    @Override
    public void computeEnd(Category bucket) {
    }

    @Override
    public Stat newInstance() {
        return new DurationSumStat();
    }

}
