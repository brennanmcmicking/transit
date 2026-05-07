package net.brennanmcmicking.transit.model;

import java.util.Comparator;
import java.util.function.Function;

public enum SortBy {
    TIME(DepartureAndDistance::getDepartureTime),
    DISTANCE(DepartureAndDistance::getDistance);

    // kind of a hack here that Comparable isn't given a generic parameter but there's nothing we can give it which overlaps Instant and Double
    private final Function<DepartureAndDistance, ? extends Comparable> keyExtractor;

    SortBy(Function<DepartureAndDistance, ? extends Comparable> keyExtractor) {
        this.keyExtractor = keyExtractor;
    }

    public Comparator<DepartureAndDistance> getDepartureAndDistanceComparator() {
        return Comparator.comparing(keyExtractor);
    }
}