package net.brennanmcmicking.transit;

import net.brennanmcmicking.transit.model.Bus;
import net.brennanmcmicking.transit.model.Departure;

import java.util.List;

public interface TransitReader {

    // TODO:
    // implement an endpoint which takes the current user's location and returns
    // a list of nearby busses
    // a list of nearby stops with the next departing bus
    List<Bus> getBusses();

    List<Departure> getNearbyDepartures(Float latitude, Float longitude, Double maxDistance);

    // implement an endpoint for when a user clicks on a specific nearby route (input to the api is route id and stop id) and returns
    // a list of the next departure times for that route at the stop nearest to them
    List<Departure> getDeparturesForStopAndRoute(String stopId, String routeId);

    // implement an endpoint for when a user clicks on a stop (input is just the stop id) and returns
    // all departures from that stop
    List<Departure> getAllDeparturesByStopId(String stopId);
}
