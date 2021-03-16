package org.someth2say.storm.stat;

import org.someth2say.storm.ResponseData;
import org.someth2say.storm.category.Category;
import org.someth2say.storm.utils.Pair;

import java.util.*;

public class IdStat extends Stat {

    List<Integer> ids = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void computeStep(Category bucket, ResponseData responseData) {
        ids.add(responseData.requestNum);
    }

    @Override
    public void computeEnd(Category bucket) {
    }

    @Override
    public Map<Object, Object> getStatResults() {
        if (ids.isEmpty())
            return Collections.emptyMap();

        if (ids.size() == 1) {
            return Map.of("ids", ids.toString());
        }

        List<Group> result = new ArrayList<>();

        Collections.sort(this.ids);
        Integer head = ids.get(0);
        Group group = new Group(head, head);
        for (int i = 1; i < ids.size(); i++) {
            Integer currentId = ids.get(i);
            if (currentId == group.rhs + 1) {
                group.rhs = currentId;
            } else {
                result.add(group);
                group = new Group(currentId, currentId);
            }
        }
        result.add(group);
        return Map.of("ids", result.toString());
    }

    private class Group extends Pair<Integer,Integer>{

        public Group(Integer lhs, Integer rhs) {
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
