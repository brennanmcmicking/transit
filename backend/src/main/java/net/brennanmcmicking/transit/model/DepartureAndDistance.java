package net.brennanmcmicking.transit.model;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class DepartureAndDistance {
    private final Departure departure;
    private final Double distance;

    public Instant getDepartureTime() {
        return departure.getDepartureTime();
    }

    public String getKey() {
        return String.format("%s:%S", departure.getRoute().getId(), departure.getDirection());
    }
}
