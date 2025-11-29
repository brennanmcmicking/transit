package net.brennanmcmicking.transit.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.VehicleDescriptor;
import com.google.transit.realtime.GtfsRealtime.Position;
import net.brennanmcmicking.transit.model.Bus;
import net.brennanmcmicking.transit.model.Direction;
import net.brennanmcmicking.transit.model.Stop;
import net.brennanmcmicking.transit.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

    public DefaultRealtimeData() {
    }

    @Override
    public List<Bus> getBusses() {
        if (isCacheExpired(lastBusFeedRefresh)) {
            refreshBusses();
        }

        return lastBusFeedMessage.getEntityList()
                .stream()
                .filter(GtfsRealtime.FeedEntity::hasVehicle)
                .map(GtfsRealtime.FeedEntity::getVehicle)
                .filter(it -> it.hasPosition() && it.hasVehicle())
                .map(it -> {
                    VehicleDescriptor vehicle = it.getVehicle();
                    Position position = it.getPosition();
                    return Bus.builder()
                            .id(vehicle.getId())
                            .label(vehicle.getLabel())
                            .latitude(position.getLatitude())
                            .longitude(position.getLongitude())
                            .speed(position.getSpeed())
                            .build();
                })
                .toList();
    }

    @Override
    public List<Trip> getTripUpdates() {
        if (isCacheExpired(lastTripUpdateRefresh)) {
            refreshTripUpdates();
        }

        LOG.info("{}", lastTripUpdateMessage.getEntityList().stream().findFirst().orElseThrow());
        return lastTripUpdateMessage.getEntityList().stream()
                .filter(GtfsRealtime.FeedEntity::hasTripUpdate)
                .map(
                it -> {
                    GtfsRealtime.TripUpdate tripUpdate = it.getTripUpdate();
                    GtfsRealtime.TripDescriptor trip = tripUpdate.getTrip();
                    Instant startTime = LocalDateTime
                            .parse(trip.getStartDate() + " " + trip.getStartTime(), DATE_TIME_FORMATTER)
                            .atZone(PACIFIC_TIME)
                            .toInstant();
                    List<Trip.StopUpdate> stopUpdates = tripUpdate
                            .getStopTimeUpdateList()
                            .stream()
                            .map(stopTimeUpdate -> Trip.StopUpdate.builder()
                                    .stopSequence(stopTimeUpdate.getStopSequence())
                                    .stopId(stopTimeUpdate.getStopId())
                                    .arrival(Instant.ofEpochSecond(stopTimeUpdate.getArrival().getTime()))
                                    .arrivalDelay(stopTimeUpdate.getArrival().getDelay())
                                    .arrivalUncertainty(stopTimeUpdate.getArrival().getUncertainty())
                                    .departure(Instant.ofEpochSecond(stopTimeUpdate.getDeparture().getTime()))
                                    .departureDelay(stopTimeUpdate.getDeparture().getDelay())
                                    .departureUncertainty(stopTimeUpdate.getDeparture().getUncertainty())
                                    .build()
                            )
                            .toList();

                    return Trip.builder()
                            .tripId(trip.getTripId())
                            .routeId(trip.getRouteId())
                            .startTime(startTime)
                            .direction(trip.getDirectionId() == 0 ? Direction.UP : Direction.DOWN)
                            .stopUpdates(stopUpdates)
                            .build();
                    }
                )
                .toList();
    }

    @Override
    public List<Stop> getStops() {
        try {
            URL url = new URL("https://bct.tmix.se/Tmix.Cap.TdExport.WebApi/gtfs/?operatorIds=48");
            GtfsRealtime.FeedMessage fm = GtfsRealtime.FeedMessage.parseFrom(url.openStream());
            LOG.info(new ObjectMapper().writeValueAsString(fm));
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Malforumed URL used in DefaultRealtimeData, realtime stop data will not be loadable", ex);
        } catch (IOException ex) {
            LOG.error("Temporarily failed to refresh real-time stop data", ex);
        }

        return List.of();
    }

    void refreshBusses() {
        synchronized (busFeedMutex) {
            if (isCacheExpired(lastBusFeedRefresh)) {
                try {
                    URL url = new URL("https://bct.tmix.se/gtfs-realtime/vehicleupdates.pb?operatorIds=48");
                    lastBusFeedMessage = GtfsRealtime.FeedMessage.parseFrom(url.openStream());
                    lastBusFeedRefresh = Instant.now();
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
            if (isCacheExpired(lastTripUpdateRefresh)) {
                try {
                    URL url = new URL("https://bct.tmix.se/gtfs-realtime/tripupdates.pb?operatorIds=48");
                    lastTripUpdateMessage = GtfsRealtime.FeedMessage.parseFrom(url.openStream());
                    lastTripUpdateRefresh = Instant.now();
                } catch (MalformedURLException ex) {
                    throw new RuntimeException("Malformed URL used in DefaultRealtimeData, realtime bus data will not be loadable", ex);
                } catch (IOException ex) {
                    LOG.error("Temporarily failed to refresh real-time trip updates", ex);
                }
            }
        }
    }

    private static boolean isCacheExpired(Instant instant) {
        return instant.isBefore(Instant.now().minusSeconds(60));
    }
}
