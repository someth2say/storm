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

    public Stat build() {
        return supplier.get();
    }

    public static List<Stat> buildAll(final List<String> buildParams) {
        if (buildParams==null)
            return Collections.emptyList();
        return buildParams.stream()
            .map(str -> StatBuilder.valueOf(str.toUpperCase()))
            .map(st -> st.build())
            .collect(Collectors.toList());
    }

}
