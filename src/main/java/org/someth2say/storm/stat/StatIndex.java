package org.someth2say.storm.stat;

import org.someth2say.storm.index.IndexEntryBuilder;
import org.someth2say.storm.index.IndexEntryBuilderParams;

public enum StatIndex {
    COUNT(CountStat.class), 
    DURATION(DurationStat.class), 
    DURATIONSUM(DurationSumStat.class), 
    ERRORS(ErrorsStat.class),
    HEADERS(HeadersStat.class), 
    HTTPCODE(HttpCodeStat.class),
    ID(IdStat.class), 
    RELATIVECOUNT(RelativeCountStat.class),
    RELATIVEDURATION(RelativeDurationStat.class), 
    THREAD(ThreadStat.class), 
    TIME(TimeStat.class), 
    URLS(URLSStat.class),
    ;

    private final Class<? extends Stat> targetClass;

    private StatIndex(final Class<? extends Stat> targetClass) {
        this.targetClass = targetClass;
    }

    public static Stat build(final StatBuilderParams statBuilderParam) {
        if (statBuilderParam.paramsPresent)
            return IndexEntryBuilder.build(statBuilderParam.indexEntry.targetClass, statBuilderParam.params);
        return IndexEntryBuilder.build(statBuilderParam.indexEntry.targetClass);
    }


    public static class StatBuilderParams extends IndexEntryBuilderParams<StatIndex>{

        public StatBuilderParams(final StatIndex index) {
            super(index);
        }

        public StatBuilderParams(final StatIndex index, final String param) {
            super(index,param);
        }   

        public StatBuilderParams(final String param) {
            super(StatIndex.class, param);
        }   

    }
}
