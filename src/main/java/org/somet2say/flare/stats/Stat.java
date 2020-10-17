package org.somet2say.flare.stats;

import org.somet2say.flare.Category;
import org.somet2say.flare.ResponseData;

public interface Stat {
    public void computeStep(Category bucket, ResponseData<String> responseData);

    public void computeEnd(Category bucket);

    public Stat newInstance();

}
