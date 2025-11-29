package net.brennanmcmicking.transit.model;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Bus {
    private final String id;
    private final String label;
    private final Float latitude;
    private final Float longitude;
    private final Float speed;
}
