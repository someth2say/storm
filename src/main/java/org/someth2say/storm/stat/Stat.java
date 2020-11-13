package org.someth2say.storm.stat;

import java.util.Map;

import org.someth2say.storm.Category;
import org.someth2say.storm.ResponseData;

public interface Stat {
    public void computeStep(Category bucket, ResponseData<String> responseData);

    public void computeEnd(Category bucket);

    public Stat newInstance();

    public Map<Object, Object> getStatResults();
}
