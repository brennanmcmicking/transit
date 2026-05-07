package net.brennanmcmicking.transit;

import net.brennanmcmicking.transit.model.*;

import java.util.List;

public interface TransitReader {

    List<Bus> getBusses();

    List<DepartureAndDistance> getNearbyDepartures(Float latitude, Float longitude, Double maxDistance, SortBy sortBy);

    List<DepartureAndDistance> getDeparturesForStopRouteDirection(String routeId, Integer direction, String stopId, Float latitude, Float longitude);

    List<Departure> getAllDeparturesByStopId(String stopId);
    List<Departure> getAllDeparturesByStopPosition(Float latitude, Float longitude);

    List<Stop> getAllStops();
}
