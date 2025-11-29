package net.brennanmcmicking.transit.data;

import com.google.transit.realtime.GtfsRealtime;
import net.brennanmcmicking.transit.model.Bus;
import net.brennanmcmicking.transit.model.Stop;
import net.brennanmcmicking.transit.model.Trip;

import java.util.List;

public interface RealtimeData {
    List<Bus> getBusses();

    List<Trip> getTripUpdates();

    List<Stop> getStops();
}
