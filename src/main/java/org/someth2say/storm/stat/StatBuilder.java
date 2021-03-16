package org.someth2say.storm.stat;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum StatBuilder {
    COUNT(CountStat::new), DURATION(DurationStat::new), DURATIONSUM(DurationSumStat::new), ERRORS(ErrorsStat::new),
    HEADERS(HeadersStat::new), ID(IdStat::new), RELATIVECOUNT(RelativeCountStat::new),
    RELATIVEDURATION(RelativeDurationStat::new), THREAD(ThreadStat::new), TIME(TimeStat::new), URLS(URLSStat::new),;

    public static class StatBuilderParams {

        public StatBuilder statBuilder;

        public StatBuilderParams(final StatBuilder statBuilder) {
            Objects.requireNonNull(statBuilder);
            this.statBuilder = statBuilder;
        }

        public StatBuilderParams(final String buildParams) {
            this.statBuilder = StatBuilder.valueOf(buildParams.toUpperCase());
        }
    }

    private Supplier<Stat> supplier;

    private StatBuilder(final Supplier<Stat> supplier) {
        this.supplier = supplier;
    }

    public static Stat build(final StatBuilderParams statBuilderParam) {
        return statBuilderParam.statBuilder.supplier.get();
    }

    public static List<Stat> buildAll(final List<StatBuilderParams> statBuilderParams) {
        if (statBuilderParams == null)
            return Collections.emptyList();
        return statBuilderParams.stream()
                // .map(str -> StatBuilder.valueOf(str.toUpperCase()))
                .map(StatBuilder::build).collect(Collectors.toList());
    }

}
