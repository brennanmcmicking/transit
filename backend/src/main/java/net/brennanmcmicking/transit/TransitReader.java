package net.brennanmcmicking.transit;

import net.brennanmcmicking.transit.model.Bus;
import net.brennanmcmicking.transit.model.Departure;
import net.brennanmcmicking.transit.model.DepartureAndDistance;
import net.brennanmcmicking.transit.model.Stop;

import java.util.List;

public interface TransitReader {

    List<Bus> getBusses();

    List<DepartureAndDistance> getNearbyDepartures(Float latitude, Float longitude, Double maxDistance);

    List<Departure> getDeparturesForStopRouteDirection(String stopId, String routeId, Integer direction);

    List<Departure> getAllDeparturesByStopId(String stopId);

    List<Stop> getAllStops();
}
