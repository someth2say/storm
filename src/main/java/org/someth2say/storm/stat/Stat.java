package org.someth2say.storm.stat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;
import org.someth2say.storm.stat.StatIndex.StatIndexEntryBuilderParams;

public abstract class Stat {

    @JsonIgnore
    public StatIndexEntryBuilderParams statBuilderParams;

    public abstract void computeStep(Category bucket, ResponseData responseData);

    public abstract void computeEnd(Category bucket);
    
    @Override
    public String toString() {
        return statBuilderParams.indexEntry.toString();
    }
}
