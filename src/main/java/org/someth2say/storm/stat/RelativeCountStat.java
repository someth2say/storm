package org.someth2say.storm.stat;

import java.util.Map;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public class RelativeCountStat implements Stat {

    public double relativeCount = 1d;

    @Override
    public Map<Object, Object> getStatResults() {
       return Map.of("relativeCounte", this.relativeCount);
    }
    
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

    public Stat newInstance() {
        return new RelativeCountStat();
    }

}
