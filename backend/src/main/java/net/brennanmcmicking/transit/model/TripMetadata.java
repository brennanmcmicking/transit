package net.brennanmcmicking.transit.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TripMetadata {
    private final String routeId;
    private final Integer serviceId;
    private final String tripId;
    private final String tripHeadsign;
    private final Integer shapeId;
    private final Integer blockId;
    private final Integer directionId;
}
