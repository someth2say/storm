package org.someth2say.storm.stat;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum Stats {
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

    public Stat getInstance() {
        return supplier.get();
    }

    private Stats(Supplier<Stat> supplier) {
        this.supplier = supplier;
    }

    public static List<Stat> buildStats(List<String> stats) {
        if (stats==null)
            return Collections.emptyList();
        return stats.stream()
            .map(str -> Stats.valueOf(str.toUpperCase()))
            .map(st -> st.getInstance())
            .collect(Collectors.toList());
    }

}
