package net.brennanmcmicking.transit;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import net.brennanmcmicking.transit.data.RealtimeData;
import net.brennanmcmicking.transit.data.StaticData;
import net.brennanmcmicking.transit.model.*;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class DefaultTransitReader implements TransitReader {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultTransitReader.class);

    private static final Double DEFAULT_MAX_DISTANCE_KM = 3.0;

    private final RealtimeData realtimeData;
    private final StaticData staticData;

    public DefaultTransitReader(RealtimeData realtimeData, StaticData staticData) {
        this.realtimeData = realtimeData;
        this.staticData = staticData;
    }

    @Override
    public List<Bus> getBusses() {
        return realtimeData.getBusses();
    }

    // algorithm:
    // comb through all of the trips that are happening
    // for each trip, find all future departures
    // filter down to trips which have a future departure within maxDistanceKm
    // optionally filter down to soonest upcoming trip sorted by route
    @Override
    public List<DepartureAndDistance> getNearbyDepartures(Float latitude, Float longitude, Double maxDistanceKm) {
        LOG.info("getNearby called with location={},{}; maxDistance={}", latitude, longitude, maxDistanceKm);
        Objects.requireNonNull(latitude, "latitude cannot be null");
        Objects.requireNonNull(longitude, "longitude cannot be null");
        final double maxDistance = Objects.isNull(maxDistanceKm) ? DEFAULT_MAX_DISTANCE_KM : maxDistanceKm;
        return realtimeData.getTripUpdates()
                .stream()
                .map(trip -> {
                    // find future departure stops
                    Instant now = Instant.now();
                    return trip
                            .getStopUpdates()
                            .stream()
                            .filter(stopUpdate -> now.isBefore(stopUpdate.getArrival()))
                            .map(stopUpdate -> Departure.fromStopUpdate(stopUpdate, staticData.getRoute(trip.getRouteId()).orElseThrow(), trip))
                            .map(departure -> {
//                                Optional<Stop> stopOptional = stopData.getStop(departure.getStop());
//                                if (stopOptional.isEmpty()) {
//                                    LOG.warn("Could not get stop from stopId={} for departure={}", departure.getStopId(), departure);
//                                    return DepartureAndDistance.builder().departure(departure).distance(Double.MAX_VALUE).build();
//                                }
//                                Stop stop = stopOptional.get();
                                Stop stop = departure.getStop();
                                LatLng stopPosition = new LatLng(stop.getLatitude(), stop.getLongitude());
                                LatLng userPosition = new LatLng(latitude, longitude);
                                double distanceKm = Precision.round(LatLngTool.distance(stopPosition, userPosition, LengthUnit.KILOMETER), 2);
                                return DepartureAndDistance.builder().departure(departure).distance(distanceKm).build();
                            })
                            .min(Comparator.comparing(DepartureAndDistance::getDistance))
                            .filter(departureAndDistance -> departureAndDistance.getDistance() < maxDistance);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
//                .map(DepartureAndDistance::getDeparture)
                .sorted(Comparator.comparing(DepartureAndDistance::getDepartureTime))
                .filter(distinctByKey(DepartureAndDistance::getKey))
                .toList();
    }

    @Override
    public List<Departure> getDeparturesForStopRouteDirection(String stopId, String routeId, Integer direction) {
        return realtimeData
                .getTripUpdates()
                .stream()
                .filter(trip -> Objects.equals(trip.getRouteId(), routeId) && Objects.equals(trip.getDirection(), direction))
                .flatMap(trip -> trip.getStopUpdates()
                        .stream()
                        .map(stopUpdate -> Departure.fromStopUpdate(
                                stopUpdate,
                                staticData.getRoute(trip.getRouteId()).orElseThrow(),
                                trip)))
                .filter(it -> Objects.equals(it.getStop().getId(), stopId))
                .toList();
    }

    @Override
    public List<Departure> getAllDeparturesByStopId(String stopId) {
        return realtimeData
                .getTripUpdates()
                .stream()
                .map(trip -> trip
                        .getStopUpdates()
                        .stream()
                        .filter(su -> Objects.equals(su.getStop().getId(), stopId))
                        .findFirst()
                        .map(su -> Departure.fromStopUpdate(su, staticData.getRoute(trip.getRouteId()).orElseThrow(), trip)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public List<Stop> getAllStops() {
        return staticData.getAllStops();
    }

    private Optional<Bus> getBusById(String busId) {
        return realtimeData.getBusses().stream()
                .filter(bus -> Objects.equals(bus.getId(), busId))
                .findFirst();
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = new HashSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
