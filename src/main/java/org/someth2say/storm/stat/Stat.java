package org.someth2say.storm.stat;

import java.util.Map;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;

public interface Stat {
    public void computeStep(Category bucket, ResponseData responseData);

    public void computeEnd(Category bucket);

    public Stat newInstance();

    public Map<Object, Object> getStatResults();
}
