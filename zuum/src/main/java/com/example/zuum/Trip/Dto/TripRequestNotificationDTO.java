package com.example.zuum.Trip.Dto;

import java.math.BigDecimal;

import org.locationtech.jts.geom.Point;

import com.example.zuum.Trip.TripStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TripRequestNotificationDTO(
    @JsonProperty("trip_id") Integer tripId,

    @JsonProperty("user_id") Integer userId,

    TripStatus status,
    BigDecimal price,
    Point origin,
    Point destiny
) {

}
