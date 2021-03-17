package org.someth2say.storm.stat;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonValue;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public class HttpCodeStat extends Stat {

    @JsonValue
    public Set<Integer> statusCodes = new HashSet<>();
    
    @Override
    public synchronized void computeStep(Category bucket, ResponseData responseData) {
        if (responseData.response!=null)
            statusCodes.add(responseData.response.statusCode());
    }

    @Override
    public void computeEnd(Category bucket) {
    }

}
