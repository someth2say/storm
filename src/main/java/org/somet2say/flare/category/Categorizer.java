package org.somet2say.flare.category;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.somet2say.flare.Bucket;
import org.somet2say.flare.ResponseData;

public interface Categorizer {
    default List<String> getCategories(final Bucket bucket) {
        return bucket.responseDatas.stream()
        .map(this::getCategoryFor)
        .flatMap(Optional::stream)
        .distinct().collect(Collectors.toList());
    }

    Optional<String> getCategoryFor(ResponseData<String> responseData);
}
