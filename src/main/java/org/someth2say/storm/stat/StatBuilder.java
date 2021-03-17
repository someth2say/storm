package org.someth2say.storm.stat;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum StatBuilder {
    COUNT(CountStat::new), 
    DURATION(DurationStat::new), 
    DURATIONSUM(DurationSumStat::new), 
    ERRORS(ErrorsStat::new),
    HEADERS(HeadersStat::new), 
    HTTPCODE(HttpCodeStat::new),
    ID(IdStat::new), 
    RELATIVECOUNT(RelativeCountStat::new),
    RELATIVEDURATION(RelativeDurationStat::new), 
    THREAD(ThreadStat::new), 
    TIME(TimeStat::new), 
    URLS(URLSStat::new),
    ;

    private Supplier<Stat> supplier;

    private StatBuilder(final Supplier<Stat> supplier) {
        this.supplier = supplier;
    }

    public static Stat build(final StatBuilderParams statBuilderParam) {
        Stat stat = statBuilderParam.statBuilder.supplier.get();
        stat.statBuilderParams=statBuilderParam;
        return stat;
    }

    public static List<Stat> buildAll(final List<StatBuilderParams> statBuilderParams) {
        if (statBuilderParams == null)
            return Collections.emptyList();
        return statBuilderParams.stream()
                .map(StatBuilder::build)
                .collect(Collectors.toList());
    }

}
