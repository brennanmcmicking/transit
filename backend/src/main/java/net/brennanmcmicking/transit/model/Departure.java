package net.brennanmcmicking.transit.model;

import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

@Getter
@ToString
public class Departure {
    private static final Logger LOG = LoggerFactory.getLogger(Departure.class);

    private final Instant departureTime;
    private final Route route;
    private final Integer direction;
    private final String tripId;
    private final String busHeader;
    private final Stop stop;

    private Departure(Instant departureTime, Route route, Integer direction, String tripId, String busHeading, Stop stop) {
        this.departureTime = departureTime;
        this.route = route;
        this.direction = direction;
        this.tripId = tripId;
        this.busHeader = busHeading;
        this.stop = stop;
    }

    public static Departure fromStopUpdate(Trip.StopUpdate stopUpdate, Route route, Trip trip) {
        return new Departure(
                stopUpdate.getArrival().plusSeconds(stopUpdate.getArrivalDelay()),
                route,
                trip.getDirection(),
                trip.getTripId(),
                trip.getBusHeader(),
                stopUpdate.getStop()
        );
    }
}
