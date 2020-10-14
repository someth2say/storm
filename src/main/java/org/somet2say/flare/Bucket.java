package org.somet2say.flare;

import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.somet2say.flare.category.Categorizer;
import org.somet2say.flare.category.Categorizers;
import org.somet2say.flare.configuration.Configuration;
import org.somet2say.flare.serialization.SerializationUtils;
import org.somet2say.flare.stats.Stat;
import org.somet2say.flare.stats.Stats;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Bucket {
    @JsonIgnore
    public final Deque<ResponseData<String>> responseDatas = new ConcurrentLinkedDeque<>();

    public final Collection<Stat> stats;

    public Map<Object, Bucket> buckets = new HashMap<>();

    public Bucket(final Configuration configuration) {
        this.stats = Stats.buildStats(configuration.getStats());
    }


    public void addResponse(final ResponseData<String> responseData) {
        // 1.- Update stats
        updateStats(responseData);
        // 2.- Add response
        responseDatas.add(responseData);

    }

    private void updateStats(final ResponseData<String> response) {
        // stats.addStats(response.stats);

        // newStats
        stats.forEach(s -> s.computeStep(this, response));

    }

    @Override
    public String toString() {
        return SerializationUtils.toYAML(this);
    }

    public void categorize(final Configuration configuration) {
        categorize(0, configuration);
    }

    private void categorize(final int catIdx, final Configuration configuration) {
        List<String> categorizers2 = configuration.getCategorizers();
        // 1.- Get categories
        if (catIdx < categorizers2.size()) {
            final Categorizer categorizer = Categorizers.buildCategorizer(categorizers2.get(catIdx));

            final List<String> categories = categorizer.getCategories(this);

            // 2.- Create buckets per category
            categories.forEach(cat -> this.buckets.put(cat, new Bucket(configuration)));

            // 3.- Add each response to the new bucket.
            addResponsesToBuckets(categorizer, configuration);

            // 4.- Recursivelly categorize each new bucket;
            if (catIdx + 1 < categorizers2.size()) {
                buckets.values().forEach(bucket -> bucket.categorize(catIdx + 1, configuration));
            }
        }
    }

    private void addResponsesToBuckets(final Categorizer categorizer, final Configuration configuration) {
        while (!responseDatas.isEmpty()) {
            final ResponseData<String> responseData = responseDatas.pop();
            final Optional<String> optKey = categorizer.getCategoryFor(responseData);

            if (optKey.isPresent()) {
                final String key = optKey.get();

                Bucket bucket;
                if (buckets.containsKey(key)) {
                    bucket = buckets.get(key);
                } else {
                    System.out.println("WARNING: Inconsistency. Categorizer " + categorizer.getClass().getSimpleName()
                            + " provided a category for a response that was not foreseen: " + key);
                    bucket = buckets.put(key, new Bucket(configuration));
                }

                bucket.addResponse(responseData);
            }
        }
        // Once all responses are added, updat stats
        buckets.values().forEach(b -> b.stats.forEach(s -> s.computeEnd(b)));
    }
}
