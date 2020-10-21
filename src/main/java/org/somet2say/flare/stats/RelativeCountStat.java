package org.somet2say.flare.stats;

import org.somet2say.flare.Category;
import org.somet2say.flare.ResponseData;

public class RelativeCountStat implements Stat {

    public Double relativeCount = 1d;

    @Override
    public void computeStep(Category bucket, ResponseData<String> responseData) {
    }

    @Override
    public void computeEnd(Category bucket) {
        if (bucket.parent!=null){
            double count = bucket.responseDatas.size();
            relativeCount = bucket.parent.stats.stream().filter(st -> st.getClass().isAssignableFrom(CountStat.class))
                            .mapToDouble(st -> (count / (((CountStat) st).count.get() * 1.0))).findFirst()
                            .orElse(0d);
        }
    }

    public Stat newInstance() {
        return new RelativeCountStat();
    }

}
