package org.somet2say.flare.category;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.somet2say.flare.Category;
import org.somet2say.flare.ResponseData;
import org.somet2say.flare.utils.InstantComparator;
import org.somet2say.flare.utils.Pair;

public class TimeHistogramCategorizer implements Categorizer {

    private int sliceDuration;
    private List<Pair<Instant, Instant>> slices;

    public TimeHistogramCategorizer(final String config) {
        try {
            sliceDuration = config == null ? 1000 : Integer.parseInt(config);
        } catch (NumberFormatException e) {
            System.err.println("stats.timehistogram: Can't parse duration histogram slices (" + config + ")");
            sliceDuration = 1000;
        }
        if (sliceDuration < 2) {
            System.err.println("stats.timehistogram: Number of slices must be at least 2 (is " + config + ")");
            sliceDuration = 1000;
        }
    }

    @Override
    public String toString() {
        return "timehistogram";
    }

    public List<String> getCategories(final Category bucket) {
        calculateSlices(bucket);
        return slices.stream().map(Pair::toString).collect(Collectors.toList());
    }

    private void calculateSlices(final Category bucket) {
        if (bucket.responseDatas.isEmpty()) {
            slices = Collections.emptyList();
            return;
        }

        Instant startTime = bucket.responseDatas.stream().map(rd->rd.endTime).min(InstantComparator.INSTANCE).orElse(Instant.EPOCH);
        Instant endTime =  bucket.responseDatas.stream().map(rd->rd.endTime).max(InstantComparator.INSTANCE).orElse(Instant.EPOCH);
        int duration = (int) (endTime.toEpochMilli() - startTime.toEpochMilli());
        slices = new ArrayList<Pair<Instant,Instant>>(duration/sliceDuration);

        Instant currentStart = startTime;
        Instant currentEnd;
        do {
            currentEnd=currentStart.plus(sliceDuration, ChronoUnit.MILLIS);
            slices.add(new Pair<>(currentStart, currentEnd));
            currentStart=currentEnd;
        } while (currentStart.isBefore(endTime));
    }

    @Override
    public Optional<String> getCategoryFor(ResponseData<String> responseData) {
        if (responseData.response != null) {
            return slices.stream()
                    .filter(slice -> sliceContains(slice, responseData.endTime))
                    .map(Pair::toString)
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }

    private boolean sliceContains(Pair<Instant, Instant> slice, Instant duration) {
        return (slice.lhs == null || slice.lhs.compareTo(duration) <=0)
                && (slice.rhs == null || slice.rhs.compareTo(duration) >= 0);
    }

}
