package net.brennanmcmicking.transit.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class Trip {
    private final String tripId;
    private final Instant startTime;
    private final String routeId;
    private final Direction direction;
    private final List<StopUpdate> stopUpdates;

    @Getter
    @Builder
    public static class StopUpdate {
        private final Integer stopSequence;
        private final String stopId;

        private final Instant arrival;
        private final Integer arrivalDelay;
        private final Integer arrivalUncertainty;

        private final Instant departure;
        private final Integer departureDelay;
        private final Integer departureUncertainty;
    }
}
