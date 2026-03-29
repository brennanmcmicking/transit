package net.brennanmcmicking.transit.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Stop {
    private String id;
    private String name;
    private Float latitude;
    private Float longitude;
    private Boolean wheelchair;
    private String code;
}
