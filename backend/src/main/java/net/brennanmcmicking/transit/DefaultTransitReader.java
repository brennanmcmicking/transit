package net.brennanmcmicking.transit;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import javafx.util.Pair;
import lombok.Builder;
import lombok.Getter;
import net.brennanmcmicking.transit.data.RealtimeData;
import net.brennanmcmicking.transit.data.StopData;
import net.brennanmcmicking.transit.model.Bus;
import net.brennanmcmicking.transit.model.Departure;
import net.brennanmcmicking.transit.model.Stop;
import net.brennanmcmicking.transit.model.Trip;
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
    private final StopData stopData;

    public DefaultTransitReader(RealtimeData realtimeData, StopData stopData) {
        this.realtimeData = realtimeData;
        this.stopData = stopData;
    }

    @Override
    public List<Bus> getBusses() {
        realtimeData.getStops();
        return realtimeData.getBusses();
    }

    // algorithm:
    // comb through all of the trips that are happening
    // for each trip, find all future departures
    // filter down to trips which have a future departure within maxDistanceKm
    // optionally filter down to soonest upcoming trip sorted by route
    @Override
    public List<Departure> getNearbyDepartures(Float latitude, Float longitude, Double maxDistanceKm) {
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
                            .map(stopUpdate -> Departure.fromStopUpdate(stopUpdate, trip))
                            .map(departure -> {
                                Optional<Stop> stopOptional = stopData.getStop(departure.getStopId());
                                if (stopOptional.isEmpty()) {
                                    LOG.warn("Could not get stop from stopId={} for departure={}", departure.getStopId(), departure);
                                    return DepartureAndDistance.builder().departure(departure).distance(Double.MAX_VALUE).build();
                                }
                                Stop stop = stopOptional.get();
                                LatLng stopPosition = new LatLng(stop.getLatitude(), stop.getLongitude());
                                LatLng userPosition = new LatLng(latitude, longitude);
                                double distanceKm = LatLngTool.distance(stopPosition, userPosition, LengthUnit.KILOMETER);
                                return DepartureAndDistance.builder().departure(departure).distance(distanceKm).build();
                            })
                            .min(Comparator.comparing(DepartureAndDistance::getDistance))
                            .filter(departureAndDistance -> departureAndDistance.getDistance() < maxDistance);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(DepartureAndDistance::getDeparture)
                .sorted(Comparator.comparing(Departure::getDepartureTime))
                .filter(distinctByKey(departure -> departure.getRouteId()))
                .toList();
    }

    @Override
    public List<Departure> getDeparturesForStopAndRoute(String stopId, String routeId) {
        return realtimeData
                .getTripUpdates()
                .stream()
                .filter(trip -> Objects.equals(trip.getRouteId(), routeId))
                .flatMap(trip -> trip.getStopUpdates()
                        .stream()
                        .map(stopUpdate -> Departure.fromStopUpdate(stopUpdate, trip)))
                .filter(it -> Objects.equals(it.getStopId(), stopId))
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
                        .filter(su -> Objects.equals(su.getStopId(), stopId))
                        .findFirst()
                        .map(su -> Departure.fromStopUpdate(su, trip)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
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

    @Getter
    @Builder
    private static class DepartureAndDistance {
        private final Departure departure;
        private final Double distance;
    }
}
