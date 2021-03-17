package org.someth2say.storm.stat;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;
import org.someth2say.storm.utils.FoldingGroup;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

public class IdStat extends Stat {

    @JsonIgnore
    private List<Integer> ids = Collections.synchronizedList(new ArrayList<>());

    @JsonValue
    public List<FoldingGroup> groups;

    @Override
    public void computeStep(Category bucket, ResponseData responseData) {
        ids.add(responseData.requestNum);
    }

    @Override
    public void computeEnd(Category bucket) {
        Collections.sort(ids);
        groups = FoldingGroup.fold(ids);
    }

}
