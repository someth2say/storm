package org.someth2say.storm.stats;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.someth2say.storm.Category;
import org.someth2say.storm.ResponseData;

public class ErrorsStat implements Stat {

    public Set<String> exceptions = new HashSet<>();

    @Override
    public Map<Object, Object> getStatResults() {
       return Map.of("errors", this.exceptions);
    }
    
    @Override
    public synchronized void computeStep(Category bucket, ResponseData<String> responseData) {
        if (responseData.exception!=null){
            exceptions.add(responseData.exception.getClass().getName()+"("+responseData.exception.getMessage()+")");
        }
    }

    @Override
    public void computeEnd(Category bucket) {
    }

    public Stat newInstance(){
        return new ErrorsStat();
    }

}
