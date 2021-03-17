package org.someth2say.storm.stat;

import java.util.Objects;

public class StatBuilderParams {

    public StatBuilder statBuilder;

    public StatBuilderParams(){}

    public StatBuilderParams(final StatBuilder statBuilder) {
        Objects.requireNonNull(statBuilder);
        this.statBuilder = statBuilder;
    }

    public StatBuilderParams(final String buildParams) {
        Objects.requireNonNull(buildParams);
        this.statBuilder = StatBuilder.valueOf(buildParams.toUpperCase());
    }

    @Override
    public String toString() {
        return statBuilder.toString();
    }
}