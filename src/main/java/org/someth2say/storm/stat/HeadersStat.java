package org.someth2say.storm.stat;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public class HeadersStat extends Stat {

    public Set<String> headers = new HashSet<>();
    
    @Override
    public Map<Object, Object> getStatResults() {
       return Map.of("headers", this.headers);
    }

    @Override
    public synchronized void computeStep(Category bucket, ResponseData responseData) {
        if (responseData.response!=null)
            headers.addAll(responseData.response.headers().map().keySet());
    }

    @Override
    public void computeEnd(Category bucket) {
    }

}
