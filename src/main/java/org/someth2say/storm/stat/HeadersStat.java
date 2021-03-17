package org.someth2say.storm.stat;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonValue;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public class HeadersStat extends Stat {

    @JsonValue
    public Set<String> headers = new HashSet<>();
    
    @Override
    public synchronized void computeStep(Category bucket, ResponseData responseData) {
        if (responseData.response!=null)
            headers.addAll(responseData.response.headers().map().keySet());
    }

    @Override
    public void computeEnd(Category bucket) {
    }

}
