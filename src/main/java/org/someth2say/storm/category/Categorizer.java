package org.someth2say.storm.category;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.someth2say.storm.Category;
import org.someth2say.storm.ResponseData;

public interface Categorizer {
    default List<String> getCategoryKeys(final Category bucket) {
        return bucket.responseDatas.stream()
        .map(this::getCategoryKeyFor)
        .flatMap(Optional::stream)
        .distinct().collect(Collectors.toList());
    }

    Optional<String> getCategoryKeyFor(ResponseData<String> responseData);
}
