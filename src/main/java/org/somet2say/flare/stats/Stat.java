package org.somet2say.flare.stats;

import org.somet2say.flare.Bucket;
import org.somet2say.flare.ResponseData;

public interface Stat {
    public void computeStep(Bucket bucket, ResponseData<String> responseData);

    public void computeEnd(Bucket bucket);

    public Stat newInstance();

}
