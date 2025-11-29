package net.brennanmcmicking.transit.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Stop {
    private String id;
    private String name;
    private String site;
    private Float latitude;
    private Float longitude;
    private String sysCode;
    private String system;
    private String municipality;
}
