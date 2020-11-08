package org.someth2say.storm;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.jboss.logging.Logger;
import org.someth2say.storm.category.Categorizer;
import org.someth2say.storm.category.Categorizers;
import org.someth2say.storm.configuration.Configuration;
import org.someth2say.storm.stats.Stat;
import org.someth2say.storm.stats.Stats;
import org.someth2say.storm.utils.SerializationUtils;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Category {

    private static final Logger LOG = Logger.getLogger(Task.class);

    @JsonIgnore
    public final Category parent;

    @JsonIgnore
    public final Deque<ResponseData<String>> responseDatas = new ConcurrentLinkedDeque<>();

    @JsonIgnore
    public final Collection<Stat> statObjs;
    public Map<Object, Object> stats = new LinkedHashMap<>();
    public Map<Object, Category> categories = new LinkedHashMap<>();

    public Category(final Configuration configuration, final Category parent) {
        this.parent = parent;
        this.statObjs = Stats.buildStats(configuration.stats);
    }

    public void addResponse(final ResponseData<String> responseData) {
        LOG.debugf("Adding response from %s to category", responseData.request.uri());
        // 1.- Add response
        responseDatas.add(responseData);
        // 2.- Update stats
        updateStats(responseData);
    }

    private void updateStats(final ResponseData<String> response) {
        statObjs.forEach(stat -> {
            LOG.debugf("Computing stat %s", stat.getClass().getSimpleName());
            stat.computeStep(this, response);
        });
    }

    @Override
    public String toString() {
        try {
            return SerializationUtils.toYAML(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize category.",e);
        }
    }

    public void categorize(final Configuration configuration) {
        categorize(0, configuration);
    }

    private void categorize(final int catIdx, final Configuration configuration) {
        List<String> categorizers = configuration.categorizers;
        // 1.- Get categories
        if (categorizers!=null && catIdx < categorizers.size()) {
            final Categorizer categorizer = Categorizers.buildCategorizer(categorizers.get(catIdx));

            // 2.- Create buckets per category
            //This is important, as some categorizers use it as initialization
            categorizer.getCategoryKeys(this).forEach(
                    key -> this.categories.put(buildCategoryKey(categorizer, key), new Category(configuration, this)));

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
            final Optional<String> optKey = categorizer.getCategoryKeyFor(responseData);

            String key = buildCategoryKey(categorizer, optKey.orElse(null));
            Category subcategory = categories.computeIfAbsent(key, k -> {
                LOG.infof("Inconsistency. Categorizer %s provided a category that was not foreseen: %s",
                        categorizer.getClass().getSimpleName(), k);
                return new Category(configuration, this);
            });
            subcategory.addResponse(responseData);

        });

        // Once all responses are added, update stats
        categories.values().forEach(category -> category.finalizeStats());
    }

    public void finalizeStats() {
        statObjs.forEach(stat -> {
            stat.computeEnd(this);
            this.stats.putAll(stat.getStatResults());
           }
        );
    }

    private String buildCategoryKey(final Categorizer categorizer, final String key) {
        return key != null ? key : "~";
    }
}
