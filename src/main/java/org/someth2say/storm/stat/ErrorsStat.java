package org.someth2say.storm.stat;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public class ErrorsStat implements Stat {

    public Set<String> exceptions = new HashSet<>();

    @Override
    public Map<Object, Object> getStatResults() {
       return Map.of("errors", this.exceptions);
    }
    
    @Override
    public synchronized void computeStep(Category bucket, ResponseData responseData) {
        if (responseData.exception!=null){
            exceptions.add(responseData.exception.getClass().getName()+"("+responseData.exception.getMessage()+")");
        }
    }

    @Override
    public void computeEnd(Category bucket) {
    }

}
