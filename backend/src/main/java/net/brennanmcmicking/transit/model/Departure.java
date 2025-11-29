package net.brennanmcmicking.transit.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.brennanmcmicking.transit.data.StopData;

import java.time.Instant;

@Getter
@Builder
@ToString
public class Departure {
    private final Instant departureTime;
    private final String routeId;
    private final String stopId;

    public static Departure fromStopUpdate(Trip.StopUpdate stopUpdate, Trip trip) {
        return builder()
                .stopId(stopUpdate.getStopId())
                .routeId(trip.getRouteId() + ":" + trip.getDirection())
                .departureTime(stopUpdate.getArrival().plusSeconds(stopUpdate.getArrivalDelay()))
                .build();
    }
}
