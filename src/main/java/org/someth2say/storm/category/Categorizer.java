package org.someth2say.storm.category;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.someth2say.storm.Category;
import org.someth2say.storm.ResponseData;

public interface Categorizer {
    default List<String> getCategories(final Category bucket) {
        return bucket.responseDatas.stream()
        .map(this::getCategoryFor)
        .flatMap(Optional::stream)
        .distinct().collect(Collectors.toList());
    }

    Optional<String> getCategoryFor(ResponseData<String> responseData);
}
