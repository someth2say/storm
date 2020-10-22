package org.someth2say.storm.stats;

import java.util.HashSet;
import java.util.Set;

import org.someth2say.storm.Category;
import org.someth2say.storm.ResponseData;

public class HeadersStat implements Stat {

    public Set<String> headers = new HashSet<>();
    @Override
    public synchronized void computeStep(Category bucket, ResponseData<String> responseData) {
        if (responseData.response!=null)
            headers.addAll(responseData.response.headers().map().keySet());
    }

    @Override
    public void computeEnd(Category bucket) {
    }

    public Stat newInstance(){
        return new CountStat();
    }

}
