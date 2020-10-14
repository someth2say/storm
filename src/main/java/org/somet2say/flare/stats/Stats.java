package org.somet2say.flare.stats;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public enum Stats {
    COUNT(CountStat::new), DURATION(DurationStat::new), TIME(TimeStat::new);

    private Supplier<Stat> supplier;

    public Stat getInstance() {
        return supplier.get();
    }

    private Stats(Supplier<Stat> supplier) {
        this.supplier = supplier;
    }

    public static List<Stat> buildStats(List<String> stats) {
        return stats.stream().map(str -> Stats.valueOf(str.toUpperCase())).map(st -> st.getInstance())
                .collect(Collectors.toList());
    }

}
