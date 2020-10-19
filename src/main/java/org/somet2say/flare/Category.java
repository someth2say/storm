package org.somet2say.flare;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.somet2say.flare.category.Categorizer;
import org.somet2say.flare.category.Categorizers;
import org.somet2say.flare.configuration.Configuration;
import org.somet2say.flare.serialization.SerializationUtils;
import org.somet2say.flare.stats.Stat;
import org.somet2say.flare.stats.Stats;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Category {
    @JsonIgnore
    public final Category parent;

    @JsonIgnore
    public final Deque<ResponseData<String>> responseDatas = new ConcurrentLinkedDeque<>();

    public final Collection<Stat> stats;

    public Map<Object, Category> categories = new LinkedHashMap<>();

    public Category(final Configuration configuration, final Category parent) {
        this.parent = parent;
        this.stats = Stats.buildStats(configuration.stats);
    }

    public void addResponse(final ResponseData<String> responseData) {
        // 1.- Update stats
        updateStats(responseData);
        // 2.- Add response
        responseDatas.add(responseData);

    }

    private void updateStats(final ResponseData<String> response) {
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
        List<String> categorizers = configuration.categorizers;
        // 1.- Get categories
        if (catIdx < categorizers.size()) {
            final Categorizer categorizer = Categorizers.buildCategorizer(categorizers.get(catIdx));

            final List<String> subcategories = categorizer.getCategories(this);

            // 2.- Create buckets per category
            subcategories.forEach(
                    cat -> this.categories.put(buildCategoryKey(categorizer, cat), new Category(configuration, this)));

            // 3.- Add each response to the new bucket.
            addResponsesToChildCategories(categorizer, configuration);

            // 4.- Recursivelly categorize each new bucket;
            if (catIdx + 1 < categorizers.size()) {
                categories.values().forEach(bucket -> bucket.categorize(catIdx + 1, configuration));
            }
        }
    }

    private void addResponsesToChildCategories(final Categorizer categorizer, final Configuration configuration) {

        responseDatas.forEach((responseData) -> {
            final Optional<String> optKey = categorizer.getCategoryFor(responseData);

            if (optKey.isPresent()) {
                String key = buildCategoryKey(categorizer, optKey.get());
                Category subcategory = categories.computeIfAbsent(key, k -> {
                    System.out.println("WARNING: Inconsistency. Categorizer " + categorizer.getClass().getSimpleName()
                            + " provided a category for a response that was not foreseen: " + k);
                    return new Category(configuration, this);
                });
                subcategory.addResponse(responseData);
            }
        });

        // Once all responses are added, update stats
        categories.values().forEach(category -> category.finalizeStats());
    }

    public void finalizeStats() {
        stats.forEach(stat -> stat.computeEnd(this));
    }

    private String buildCategoryKey(final Categorizer categorizer, final String key) {
        return categorizer.toString() + "=" + (key != null ? key : "~");
    }
}
