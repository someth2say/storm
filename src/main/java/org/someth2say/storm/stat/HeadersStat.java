package org.someth2say.storm.stat;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.someth2say.storm.Category;
import org.someth2say.storm.ResponseData;

public class HeadersStat implements Stat {

    public Set<String> headers = new HashSet<>();
    
    @Override
    public Map<Object, Object> getStatResults() {
       return Map.of("headers", this.headers);
    }

    @Override
    public synchronized void computeStep(Category bucket, ResponseData<String> responseData) {
        if (responseData.response!=null)
            headers.addAll(responseData.response.headers().map().keySet());
    }

    @Override
    public void computeEnd(Category bucket) {
    }

    public Stat newInstance(){
        return new CountStat();
    }

}