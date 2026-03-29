package net.brennanmcmicking.transit.data;

import net.brennanmcmicking.transit.model.Route;
import net.brennanmcmicking.transit.model.Stop;
import net.brennanmcmicking.transit.model.TripMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DefaultStaticData implements StaticData {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultStaticData.class);

    private final Object staticDataMutex = new Object();

    private Instant lastRefresh = Instant.EPOCH;

    private Map<String, Stop> stops;
    private List<Stop> stopList;
    private Map<String, Route> routes;
    private Map<String, TripMetadata> trips;

    public DefaultStaticData() {
        refreshStaticData();
    }

    @Override
    public Optional<Route> getRoute(String routeId) {
        if (isCacheExpired(lastRefresh)) {
            refreshStaticData();
        }
        return Optional.ofNullable(routes.get(routeId));
    }

    @Override
    public Optional<Stop> getStop(String stopId) {
        if (isCacheExpired(lastRefresh)) {
            refreshStaticData();
        }
        return Optional.ofNullable(stops.get(stopId));
    }

    @Override
    public List<Stop> getAllStops() {
        return stopList;
    }

    @Override
    public Optional<TripMetadata> getTripMetadata(String tripId) {
        if (isCacheExpired(lastRefresh)) {
            refreshStaticData();
        }
        return Optional.ofNullable(trips.get(tripId));
    }

    private void refreshStaticData() {
        synchronized (staticDataMutex) {
            if (isCacheExpired(lastRefresh)) {
                try {
                    URL url = new URL("https://bct.tmix.se/Tmix.Cap.TdExport.WebApi/gtfs/?operatorIds=48");
                    try (InputStream is = url.openStream(); ZipInputStream zip = new ZipInputStream(is)) {
                        ZipEntry entry = zip.getNextEntry();
                        while (Objects.nonNull(entry)) {
                            String name = entry.getName();
                            LOG.info("processing name={}", name);
                            if ("routes.txt".equals(name)) {
                                String routesFile = new String(zip.readAllBytes());
                                routes = parseRoutesFile(routesFile);
                            } else if ("stops.txt".equals(name)) {
                                String stopsFile = new String(zip.readAllBytes());
                                stops = parseStopsFile(stopsFile);
                                stopList = stops.values().stream().toList(); // toList returns an unmodifiable list
                            } else if ("trips.txt".equals(name)) {
                                String tripsFile = new String(zip.readAllBytes());
                                trips = parseTripsFile(tripsFile);
                            }
                            entry = zip.getNextEntry();
                        }
                        lastRefresh = Instant.now();
                    }
                } catch (MalformedURLException ex) {
                    throw new RuntimeException("Malformed URL used in DefaultRealtimeData");
                } catch (IOException ex) {
                    LOG.error("Temporarily failed to refresh real-time bus data", ex);
                }
            }
        }
    }

    private Map<String, Route> parseRoutesFile(String csv) {
        Map<String, Route> localMap = new ConcurrentHashMap<>();
        List<String> rows = List.of(csv.split("\n"));
        rows
                .stream()
                .skip(1)
                .forEach(row -> {
                    List<String> columns = List.of(row.split(","));
                    Route route = Route.builder()
                            .id(columns.get(0))
                            .shortName(columns.get(1))
                            .longName(columns.get(2))
                            .type(Integer.parseInt(columns.get(3)))
                            .colorHex(columns.get(4))
                            .textColorHex(columns.get(5))
                            .build();

                    localMap.put(route.getId(), route);
                });
        return localMap;
    }

    private Map<String, Stop> parseStopsFile(String csv) {
        Map<String, Stop> localMap = new ConcurrentHashMap<>();
        List<String> rows = List.of(csv.split("\n"));
        rows
                .stream()
                .skip(1)
                .forEach(row -> {
                    List<String> columns = List.of(row.split(","));
                    String stopId = columns.get(0);
                    String stopName = columns.get(1);
                    Float latitude = Float.parseFloat(columns.get(2));
                    Float longitude = Float.parseFloat(columns.get(3));
                    Boolean wheelchair = Boolean.parseBoolean(columns.get(4));
                    String stopCode = columns.get(5);
                    Stop stop = Stop.builder()
                            .id(stopId)
                            .name(stopName)
                            .latitude(latitude)
                            .longitude(longitude)
                            .wheelchair(wheelchair)
                            .code(stopCode)
                            .build();
                    localMap.put(stop.getId(), stop);
                });

        return localMap;
    }

    private Map<String, TripMetadata> parseTripsFile(String csv) {
        Map<String, TripMetadata> localMap = new ConcurrentHashMap<>();
        LOG.debug(csv.substring(0, 500));
        List<String> rows = List.of(csv.replace("\r", "").split("\n"));
        rows
                .stream()
                .skip(1)
                .forEach(row -> {
                    LOG.debug("row={}", row);
                    List<String> columns = List.of(row.split(","));
                    String routeId = columns.get(0);
                    Integer serviceId = Integer.parseInt(columns.get(1));
                    String tripId = columns.get(2);
                    String tripHeadsign = columns.get(3);
                    Integer shapeId = Integer.parseInt(columns.get(4));
                    Integer blockId = Integer.parseInt(columns.get(5));
                    String direction = columns.get(6);
                    LOG.debug("directionId={}, len={}", direction, direction.length());
                    Integer directionId = Integer.parseInt(direction);
                    TripMetadata tripMetadata = TripMetadata.builder()
                            .routeId(routeId)
                            .serviceId(serviceId)
                            .tripId(tripId)
                            .tripHeadsign(tripHeadsign)
                            .shapeId(shapeId)
                            .blockId(blockId)
                            .directionId(directionId)
                            .build();
                    localMap.put(tripId, tripMetadata);
                });

        return localMap;
    }


    private static boolean isCacheExpired(Instant lastRefresh) {
        return lastRefresh.isBefore(Instant.now().minus(12, ChronoUnit.HOURS));
    }
}
