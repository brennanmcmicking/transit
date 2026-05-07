package net.brennanmcmicking.transit.server;

import jakarta.ws.rs.*;
import net.brennanmcmicking.transit.TransitReader;
import net.brennanmcmicking.transit.model.*;

import java.util.List;
import java.util.Objects;

@Path("/v1")
@Produces("application/json")
public class TransitResource {
    private final TransitReader reader;

    public TransitResource(TransitReader reader) {
        this.reader = reader;
    }

    @GET
    @Path("/busses")
    public List<Bus> getBusses() {
        return reader.getBusses();
    }

    @GET
    @Path("/nearby-departures")
    public List<DepartureAndDistance> getNearbyDepartures(
            @QueryParam("latitude") Float latitude,
            @QueryParam("longitude") Float longitude,
            @QueryParam("maxDistanceKm") Double maxDistanceKm,
            @QueryParam("sortBy") SortBy sortBy
    ) {
        Objects.requireNonNull(latitude, "Latitude cannot be null");
        Objects.requireNonNull(longitude, "Longitude cannot be null");
        return reader.getNearbyDepartures(latitude, longitude, maxDistanceKm, sortBy);
    }

    // implement an endpoint for when a user clicks on a stop (input is just the stop id) and returns
    // all departures from that stop
    @GET
    @Path("/departures-for-stop")
    public List<Departure> getAllDeparturesByStopId(@QueryParam("stopId") String stopId) {
        Objects.requireNonNull(stopId, "stoId cannot be null");
        return reader.getAllDeparturesByStopId(stopId);
    }

    @GET
    @Path("/departures/stop")
    public List<Departure> getAllDeparturesByStopPosition(
            @QueryParam("latitude") Float latitude,
            @QueryParam("longitude") Float longitude
    ) {
        Objects.requireNonNull(latitude, "latitude cannot be null");
        Objects.requireNonNull(longitude, "longitude cannot be null");
        return reader.getAllDeparturesByStopPosition(latitude, longitude);
    }

    @GET
    @Path("/departures-for-route/{routeId}/{direction}/{stopId}")
    public List<DepartureAndDistance> getDeparturesForStopAndRoute(
            @PathParam("routeId") String routeId,
            @PathParam("direction") Integer direction,
            @PathParam("stopId") String stopId,
            @QueryParam("latitude") Float latitude,
            @QueryParam("longitude") Float longitude
    ) {
        Objects.requireNonNull(stopId, "stopId cannot be null");
        Objects.requireNonNull(routeId, "routeId cannot be null");
        Objects.requireNonNull(direction, "direction cannot be null");
        return reader.getDeparturesForStopRouteDirection(routeId, direction, stopId, latitude, longitude);
    }

    @GET
    @Path("/stops")
    public List<Stop> getStops() {
        return reader.getAllStops();
    }
}
