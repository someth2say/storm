package org.someth2say.storm.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonValue;

public class FoldingGroup extends Pair<Number, Number> {

    public FoldingGroup(Number lhs, Number rhs) {
        super(lhs, rhs);
    }

    @JsonValue
    @Override
    public String toString() {
        if (lhs != null && lhs.equals(rhs)) {
            return lhs.toString();
        } else if (lhs == null && rhs == null) {
            return "";
        } else {
            return (lhs != null ? lhs.toString() : "") + "-" + (rhs != null ? rhs.toString() : "");
        }
    }

    public static List<FoldingGroup> fold(final Collection<? extends Number> numbers) {
        if (numbers.isEmpty())
            return Collections.emptyList();

        Iterator<? extends Number> iterator = numbers.iterator();

        // if (numbers.size() == 1) {
        //     Number head = iterator.next();
        //     return List.of(new FoldingGroup(head, head));
        // }

        List<FoldingGroup> result = new ArrayList<>();

        Number head = iterator.next();
        FoldingGroup group = new FoldingGroup(head, head);
        while (iterator.hasNext()) {
            Number currentId = iterator.next();
            if (currentId.longValue() == group.rhs.longValue() + 1) {
                group.rhs = currentId;
            } else {
                result.add(group);
                group = new FoldingGroup(currentId, currentId);
            }
        }
        result.add(group);
        return result;
    }
}