package org.someth2say.storm.stat;

import com.fasterxml.jackson.annotation.JsonValue;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public class RelativeCountStat extends Stat {

    @JsonValue
    public double relativeCount = 1d;

    @Override
    public void computeStep(Category bucket, ResponseData responseData) {
    }

    @Override
    public void computeEnd(Category bucket) {
        if (bucket.parent!=null){
            double count = bucket.responseDatas.size();
            double parentCount = bucket.parent.responseDatas.size();
            relativeCount = count/parentCount;
        }
    }

}
