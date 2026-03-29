package net.brennanmcmicking.transit.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Route {
    private final String id;
    private final String shortName;
    private final String longName;
    private final Integer type;
    private final String colorHex;
    private final String textColorHex;
}
