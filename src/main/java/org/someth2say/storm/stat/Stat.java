package org.someth2say.storm.stat;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public abstract class Stat {

    @JsonIgnore
    public StatBuilderParams statBuilderParams;

    public abstract void computeStep(Category bucket, ResponseData responseData);

    public abstract void computeEnd(Category bucket);

//    @JsonIgnore
//    public abstract Map<Object, Object> getStatResults();

    @Override
    public String toString() {
        return statBuilderParams.statBuilder.toString();
    }
}
