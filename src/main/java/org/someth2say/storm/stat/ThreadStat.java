package org.someth2say.storm.stat;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;
import org.someth2say.storm.utils.FoldingGroup;
import org.someth2say.storm.utils.Pair;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonValue;

public class ThreadStat extends Stat {

    private SortedSet<Long> _threads = Collections.synchronizedSortedSet(new TreeSet<>());
    
    @JsonValue
    public List<FoldingGroup> threads;

    @Override
    public void computeStep(Category bucket, ResponseData responseData) {
        _threads.add(Thread.currentThread().getId());
    }

    @Override
    public void computeEnd(Category bucket) {
        threads = FoldingGroup.fold(_threads);
    }

    // @Override
    // public Map<Object, Object> getStatResults() {
    //     if (threads.isEmpty())
    //         return Collections.emptyMap();

    //     if (threads.size() == 1) {
    //         return Map.of("threads", threads.toString());
    //     }

    //     List<Group> result = new ArrayList<>();
    //     Group group=null;
    //     for (Long currentId : threads) {
    //         if (group==null){
    //             group = new Group(currentId,currentId);
    //         } else if (currentId == group.rhs + 1) {
    //             group.rhs = currentId;
    //         } else {
    //             result.add(group);
    //             group = new Group(currentId, currentId);
    //         }
    //     }
    //     result.add(group);
    //     return Map.of("threads", result.toString());
    // }

    private class Group extends Pair<Long,Long>{

        public Group(Long lhs, Long rhs) {
            super(lhs, rhs);
        }

        @Override
        public String toString() {
            if (lhs!=null && lhs.equals(rhs)){
                return lhs.toString();
            } else if (lhs==null && rhs==null){
                return "";
            } else {
                return (lhs != null ? lhs.toString() : "") + "-" + (rhs != null ? rhs.toString() : "");
            }
        }
    }

}
