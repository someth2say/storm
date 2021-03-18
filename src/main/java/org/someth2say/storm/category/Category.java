package org.someth2say.storm.category;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.jboss.logging.Logger;
import org.someth2say.storm.ResponseData;
import org.someth2say.storm.StormCallable;
import org.someth2say.storm.category.CategorizerIndex.CategorizerBuilderParams;
import org.someth2say.storm.configuration.StormConfiguration;
import org.someth2say.storm.stat.Stat;
import org.someth2say.storm.stat.StatIndex;
import org.someth2say.storm.utils.SerializationUtils;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Category {

    private static final Logger LOG = Logger.getLogger(StormCallable.class);

    @JsonIgnore
    public final Category parent;

    @JsonIgnore
    public final Deque<ResponseData> responseDatas = new ConcurrentLinkedDeque<>();
    
    public final Map<StatIndex.StatBuilderParams, Stat> stats;
    public final Map<Object, Category> categories = new LinkedHashMap<>();

    public Category(final StormConfiguration configuration, final Category parent) {
        this.parent = parent;
        this.stats = configuration.stats.stream().collect(
            Collectors.toUnmodifiableMap(Function.identity(), StatIndex::build)
        );
    }

    public void addResponse(final ResponseData responseData) {
        LOG.debugf("Adding response %010d from %s to category", responseData.requestNum, responseData.request.uri());
        // 1.- Add response
        responseDatas.stream().filter(rd->rd.requestNum==responseData.requestNum).findAny().ifPresent(rd->
                LOG.errorf("Another response with the same number added: %d", rd.requestNum));

        responseDatas.add(responseData);
        // 2.- Update stats
        updateStats(responseData);
    }

    private void updateStats(final ResponseData response) {
        stats.values().forEach(stat -> {
            LOG.debugf("Computing stat %s for response %010d", stat.getClass().getSimpleName(), response.requestNum);
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

    public void categorize(final StormConfiguration configuration) {
        categorize(0, configuration);
    }

    private void categorize(final int catIdx, final StormConfiguration configuration) {
        List<CategorizerBuilderParams> categorizers = new ArrayList<>(configuration.categorizers);
        // 1.- Get categories
        if (categorizers!=null && catIdx < categorizers.size()) {
            final Categorizer categorizer = CategorizerIndex.build(categorizers.get(catIdx));

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

    private void addResponsesToChildCategories(final Categorizer categorizer, final StormConfiguration configuration) {

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
        stats.values().forEach(stat -> stat.computeEnd(this));
    }

    private String buildCategoryKey(final Categorizer categorizer, final String key) {
        return key != null ? key : "~";
    }
}
