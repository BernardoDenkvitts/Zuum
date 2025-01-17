package com.example.zuum.Driver.Dto;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LocationDTO(
    @JsonProperty("driver_id")
    Integer driverId,
    Point origin,
    Point destiny
) {
}
