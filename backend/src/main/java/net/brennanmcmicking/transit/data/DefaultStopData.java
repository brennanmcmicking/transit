package net.brennanmcmicking.transit.data;

import net.brennanmcmicking.transit.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultStopData implements StopData {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultStopData.class);

    private final Map<String, Stop> stops;

    public DefaultStopData() {
        Map<String, Stop> localMap = new ConcurrentHashMap<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            try (InputStream is = classLoader.getResourceAsStream("stops.csv")) {
                Objects.requireNonNull(is, "input stream for stops.csv cannot be null");
                String csv = new String(is.readAllBytes());
                List<String> rows = List.of(csv.split("\n"));
                rows
                        .stream()
                        .skip(1)
                        .forEach(row -> {
                            List<String> columns = List.of(row.split(","));
                            String stopId = columns.get(0);
                            String stopName = columns.get(1);
                            String stopSite = columns.get(2);
                            Float latitude = Float.parseFloat(columns.get(3));
                            Float longitude = Float.parseFloat(columns.get(4));
                            String sysCode = columns.get(5);
                            String system = columns.get(6);
                            String municipality = columns.get(7);
                            Stop stop = Stop.builder()
                                    .id(stopId)
                                    .name(stopName)
                                    .site(stopSite)
                                    .latitude(latitude)
                                    .longitude(longitude)
                                    .sysCode(sysCode)
                                    .system(system)
                                    .municipality(municipality)
                                    .build();
                            LOG.info("Found stopId={}; stopName={}; position={},{}", stopId, stopName, latitude, longitude);
                            localMap.put(stop.getId(), stop);
                        });
            }
        } catch (IOException ex) {
            // we cannot recover in this situation so crash the program
            throw new RuntimeException("Could not load stop data from resources", ex);
        }

        stops = Collections.unmodifiableMap(localMap);
    }

    @Override
    public Optional<Stop> getStop(String stopId) {
        Objects.requireNonNull(stopId, "stopId cannot be null");
        return Optional.ofNullable(stops.get(stopId));
    }
}
