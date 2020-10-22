package org.someth2say.storm.utils;

public class Pair<L, R> {
    public Pair(final L lhs, final R rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public L lhs;
    public R rhs;

    @Override
    public String toString() {
        return (lhs != null ? lhs.toString() : "") + "," + (rhs != null ? rhs.toString() : "");
    }
}
