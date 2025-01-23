package com.example.zuum.Ride.Dto;

import java.math.BigDecimal;

import org.locationtech.jts.geom.Point;

import com.example.zuum.Ride.RideStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RideRequestNotificationDTO(
    @JsonProperty("trip_id") Integer tripId,

    @JsonProperty("user_id") Integer userId,

    RideStatus status,
    BigDecimal price,
    Point origin,
    Point destiny
) {

}
