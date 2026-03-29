package net.brennanmcmicking.transit.data;

import net.brennanmcmicking.transit.model.Route;
import net.brennanmcmicking.transit.model.Stop;
import net.brennanmcmicking.transit.model.TripMetadata;

import java.util.List;
import java.util.Optional;

public interface StaticData {
    Optional<Route> getRoute(String routeId);

    Optional<Stop> getStop(String stopId);
    List<Stop> getAllStops();

    Optional<TripMetadata> getTripMetadata(String tripId);
}
