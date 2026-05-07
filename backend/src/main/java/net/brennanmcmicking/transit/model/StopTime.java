package net.brennanmcmicking.transit.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class StopTime {
    private final String tripId;
    private final Instant arrivalTime;
    private final Instant departureTime;
    private final Integer stopId;
    private final Integer stopSequence;
    private final Integer shapeDistanceTraveled;
    private final String stopHeadsignUnused;
    private final Integer pickupType;
    private final Integer dropOffType;
    private final Integer timePoint;
}
