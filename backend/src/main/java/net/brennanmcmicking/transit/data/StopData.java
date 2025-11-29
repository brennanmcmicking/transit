package net.brennanmcmicking.transit.data;

import net.brennanmcmicking.transit.model.Stop;

import java.util.Optional;

public interface StopData {

    Optional<Stop> getStop(String stopId);
}
