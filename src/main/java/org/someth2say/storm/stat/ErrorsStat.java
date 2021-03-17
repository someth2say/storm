package org.someth2say.storm.stat;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonValue;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public class ErrorsStat extends Stat {

    @JsonValue
    public Set<String> exceptions = new HashSet<>();
   
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
