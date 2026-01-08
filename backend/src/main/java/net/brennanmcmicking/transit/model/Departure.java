package net.brennanmcmicking.transit.model;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class Departure {
    private final Instant departureTime;
    private final String routeId;
    private final String busHeader;
    private final String stopId;

    private Departure(Instant departureTime, String routeId, String busHeading, String stopId) {
        this.departureTime = departureTime;
        this.routeId = routeId;
        this.busHeader = busHeading;
        this.stopId = stopId;
    }

    public static Departure fromStopUpdate(Trip.StopUpdate stopUpdate, Trip trip) {
        return new Departure(
                stopUpdate.getArrival().plusSeconds(stopUpdate.getArrivalDelay()),
                trip.getRouteId() + ":" + trip.getDirection(),
                trip.getBusHeader(),
                stopUpdate.getStopId()
        );
    }
}
