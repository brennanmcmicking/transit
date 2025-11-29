package net.brennanmcmicking.transit.server;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import net.brennanmcmicking.transit.TransitReader;
import net.brennanmcmicking.transit.model.Bus;
import net.brennanmcmicking.transit.model.Departure;

import java.util.List;

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

    // TODO:
    // implement an endpoint which takes the current user's location and returns
    // a list of nearby stops with the next departing bus
    @GET
    @Path("/nearby-departures")
    public List<Departure> getNearbyDepartures(
            @QueryParam("latitude") Float latidude,
            @QueryParam("longitude") Float longitude,
            @QueryParam("maxDistanceKm") Double maxDistanceKm
    ) {
        return reader.getNearbyDepartures(latidude, longitude, maxDistanceKm);
    }

    // implement an endpoint for when a user clicks on a stop (input is just the stop id) and returns
    // all departures from that stop
    @GET
    @Path("/departures-for-stop")
    public List<Departure> getAllDeparturesByStopId(@QueryParam("stopId") String stopId) {
        return reader.getAllDeparturesByStopId(stopId);
    }

    @GET
    @Path("/departures-for-stop-and-route")
    public List<Departure> getDeparturesForStopAndRoute(
            @QueryParam("stopId") String stopId,
            @QueryParam("routeId") String routeId
    ) {
        return reader.getDeparturesForStopAndRoute(stopId, routeId);
    }
}
