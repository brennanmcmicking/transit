package net.brennanmcmicking.transit.data;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.VehicleDescriptor;
import com.google.transit.realtime.GtfsRealtime.Position;
import net.brennanmcmicking.transit.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DefaultRealtimeData implements RealtimeData {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultRealtimeData.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
    private static final ZoneId PACIFIC_TIME = ZoneId.of("America/Los_Angeles");

    private final Object busFeedMutex = new Object();
    private final Object tripUpdateMutex = new Object();

    GtfsRealtime.FeedMessage lastBusFeedMessage;
    Instant lastBusFeedRefresh = Instant.EPOCH;

    GtfsRealtime.FeedMessage lastTripUpdateMessage;
    Instant lastTripUpdateRefresh = Instant.EPOCH;

    private final StaticData staticData;

    public DefaultRealtimeData(StaticData staticData) {
        refreshBusses();
        refreshTripUpdates();
        this.staticData = staticData;
    }

    @Override
    public List<Bus> getBusses() {
        if (isCacheExpired(lastBusFeedRefresh, 60L)) {
            refreshBusses();
        }

        return lastBusFeedMessage.getEntityList()
                .stream()
                .filter(GtfsRealtime.FeedEntity::hasVehicle)
                .map(GtfsRealtime.FeedEntity::getVehicle)
                .filter(it -> it.hasPosition() && it.hasVehicle() && it.hasTrip())
                .map(it -> {
                    VehicleDescriptor vehicle = it.getVehicle();
                    Position position = it.getPosition();
                    GtfsRealtime.TripDescriptor trip = it.getTrip();
                    return Bus.builder()
                            .id(vehicle.getId())
                            .label(trip.getRouteId().split("-")[0])
                            .latitude(position.getLatitude())
                            .longitude(position.getLongitude())
                            .speed(position.getSpeed())
                            .build();
                })
                .toList();
    }

    @Override
    public List<Trip> getTripUpdates() {
        if (isCacheExpired(lastTripUpdateRefresh, 60L)) {
            refreshTripUpdates();
        }

        return lastTripUpdateMessage.getEntityList().stream()
                .filter(GtfsRealtime.FeedEntity::hasTripUpdate)
                .map(
                it -> {
                    GtfsRealtime.TripUpdate tripUpdate = it.getTripUpdate();
                    GtfsRealtime.TripDescriptor trip = tripUpdate.getTrip();
                    Optional<Instant> startTime = Optional.empty();
                    try {
                        startTime = Optional.of(LocalDateTime
                                .parse(trip.getStartDate() + " " + trip.getStartTime(), DATE_TIME_FORMATTER)
                                .atZone(PACIFIC_TIME)
                                .toInstant());
                    } catch (DateTimeParseException ignored) {
                    }

                    List<Trip.StopUpdate> stopUpdates = tripUpdate
                            .getStopTimeUpdateList()
                            .stream()
                            .map(stopTimeUpdate -> Trip.StopUpdate.builder()
                                    .stopSequence(stopTimeUpdate.getStopSequence())
                                    .stop(staticData.getStop(stopTimeUpdate.getStopId()).orElseThrow())
                                    .arrival(Instant.ofEpochSecond(stopTimeUpdate.getArrival().getTime()))
                                    .arrivalDelay(stopTimeUpdate.getArrival().getDelay())
                                    .arrivalUncertainty(stopTimeUpdate.getArrival().getUncertainty())
                                    .departure(Instant.ofEpochSecond(stopTimeUpdate.getDeparture().getTime()))
                                    .departureDelay(stopTimeUpdate.getDeparture().getDelay())
                                    .departureUncertainty(stopTimeUpdate.getDeparture().getUncertainty())
                                    .build()
                            )
                            .toList();

                    TripMetadata tripMetadata = staticData.getTripMetadata(trip.getTripId()).orElseThrow();

                    return Trip.builder()
                            .tripId(trip.getTripId())
                            .routeId(trip.getRouteId())
                            .startTime(startTime.orElse(Instant.MAX))
                            .direction(trip.getDirectionId())
                            .stopUpdates(stopUpdates)
                            .busHeader(tripMetadata.getTripHeadsign())
                            .build();
                    }
                )
                .toList();
    }

    void refreshBusses() {
        synchronized (busFeedMutex) {
            if (isCacheExpired(lastBusFeedRefresh, 60L)) {
                try {
                    URL url = new URL("https://bct.tmix.se/gtfs-realtime/vehicleupdates.pb?operatorIds=48");
                    try (InputStream stream = url.openStream()) {
                        lastBusFeedMessage = GtfsRealtime.FeedMessage.parseFrom(stream);
                        lastBusFeedRefresh = Instant.now();
                    }
                } catch (MalformedURLException ex) {
                    throw new RuntimeException("Malformed URL used in DefaultRealtimeData, realtime bus data will not be loadable", ex);
                } catch (IOException ex) {
                    LOG.error("Temporarily failed to refresh real-time bus data", ex);
                }
            }
        }
    }

    void refreshTripUpdates() {
        synchronized (tripUpdateMutex) {
            if (isCacheExpired(lastTripUpdateRefresh, 60L)) {
                try {
                    URL url = new URL("https://bct.tmix.se/gtfs-realtime/tripupdates.pb?operatorIds=48");
                    try (InputStream stream = url.openStream()) {
                        lastTripUpdateMessage = GtfsRealtime.FeedMessage.parseFrom(stream);
                        lastTripUpdateRefresh = Instant.now();
                    }
                } catch (MalformedURLException ex) {
                    throw new RuntimeException("Malformed URL used in DefaultRealtimeData, realtime bus data will not be loadable", ex);
                } catch (IOException ex) {
                    LOG.error("Temporarily failed to refresh real-time trip updates", ex);
                }
            }
        }
    }

    private static boolean isCacheExpired(Instant lastRefresh, Long expirationSeconds) {
        return lastRefresh.isBefore(Instant.now().minusSeconds(expirationSeconds));
    }
}
