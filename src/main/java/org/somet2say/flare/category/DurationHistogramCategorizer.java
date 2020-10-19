package org.somet2say.flare.category;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.somet2say.flare.Category;
import org.somet2say.flare.ResponseData;
import org.somet2say.flare.utils.Pair;

public class DurationHistogramCategorizer implements Categorizer {

    private int numSlices;
    private final List<Duration> slices;
    private final List<Pair<Duration, Duration>> slices2;

    public DurationHistogramCategorizer(final String config) {
        try {
            numSlices = config == null ? 10 : Integer.parseInt(config);
        } catch (NumberFormatException e) {
            System.err.println("stats.duratiohistogram: Can't parse duration histogram slices (" + config + ")");
            numSlices = 10;
        }
        if (numSlices < 2) {
            System.err.println("stats.duratiohistogram: Number of slices must be at least 2 (is " + config + ")");
            numSlices = 10;
        }
        slices = new ArrayList<>(numSlices - 1);
        slices2 = new ArrayList<>(numSlices);
    }

    @Override
    public String toString() {
        return "durationhistogram";
    }

    public List<String> getCategories(final Category bucket) {
        calculateSlices(bucket);
        return slices2.stream().map(Pair::toString).collect(Collectors.toList());
    }

    private void calculateSlices(final Category bucket) {
        var minDuration = Duration.ofSeconds(Long.MAX_VALUE, 0);
        var maxDuration = Duration.ZERO;
        for (ResponseData<String> responseData : bucket.responseDatas) {
            var duration = responseData.getDuration();
            minDuration = (duration.compareTo(minDuration) < 0) ? duration : minDuration;
            maxDuration = (duration.compareTo(maxDuration) > 0) ? duration : maxDuration;
        }
        var sliceSize = maxDuration.minus(minDuration).dividedBy(numSlices);
        Duration currentDuration = minDuration;
        Duration prevDuration = minDuration;
        for (int slice = 0; slice < numSlices - 1; slice++) {
            prevDuration = currentDuration;
            currentDuration = currentDuration.plus(sliceSize);
            slices.add(currentDuration);
            slices2.add(new Pair<>(prevDuration, currentDuration));
        }
        // Adding this avoids rounding issues with the division.
        slices.add(maxDuration);
        slices2.add(new Pair<>(currentDuration, maxDuration));
    }

    @Override
    public Optional<String> getCategoryFor(ResponseData<String> responseData) {
        if (responseData.response != null) {
            var duration = responseData.getDuration(); 
            return slices2.stream().filter(slice -> sliceContains(slice, duration)).map(Pair::toString).findFirst();
        } else {
            return Optional.empty();
        }
    }

    private boolean sliceContains(Pair<Duration, Duration> slice, Duration duration) {
        return (slice.lhs == null || slice.lhs.compareTo(duration) <= 0)
                && (slice.rhs == null || slice.rhs.compareTo(duration) >= 0);
    }

}
