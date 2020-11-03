package org.someth2say.storm.stats;

import java.util.Map;

import org.someth2say.storm.Category;
import org.someth2say.storm.ResponseData;

public class RelativeCountStat implements Stat {

    public double relativeCount = 1d;

    @Override
    public Map<Object, Object> getStatResults() {
       return Map.of("relativeCounte", this.relativeCount);
    }
    
    @Override
    public void computeStep(Category bucket, ResponseData<String> responseData) {
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
