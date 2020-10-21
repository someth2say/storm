package org.somet2say.flare.stats;

import java.util.Collection;
import java.util.HashSet;

import org.somet2say.flare.Category;
import org.somet2say.flare.ResponseData;

public class URLStat implements Stat {

    public Collection<String> urls = new HashSet<>();

    @Override
    public synchronized void computeStep(Category bucket, ResponseData<String> responseData) {
        urls.add(responseData.response.uri().toString());
    }

    @Override
    public void computeEnd(Category bucket) {
    }

    @Override
    public Stat newInstance() {
        return new DurationSumStat();
    }

}
