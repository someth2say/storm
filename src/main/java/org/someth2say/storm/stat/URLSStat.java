package org.someth2say.storm.stat;

import java.util.Collection;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonValue;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public class URLSStat extends Stat {

    @JsonValue
    public Collection<String> urls = new HashSet<>();
    
    @Override
    public synchronized void computeStep(Category bucket, ResponseData responseData) {
        urls.add(responseData.request.uri().toString());
    }

    @Override
    public void computeEnd(Category bucket) {
    }

}
