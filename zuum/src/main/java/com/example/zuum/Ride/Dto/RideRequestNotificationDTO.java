package com.example.zuum.Ride.Dto;

import java.math.BigDecimal;

import org.locationtech.jts.geom.Point;

import com.example.zuum.Ride.RideModel;
import com.example.zuum.Ride.RideStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public record RideRequestNotificationDTO(
        @JsonProperty("ride_id") Integer rideId,

        @JsonProperty("user_id") Integer userId,

        RideStatus status,
        BigDecimal price,
        Point origin,
        Point destiny
) {

    public static RideRequestNotificationDTO toDTO(RideModel model) {
        return new RideRequestNotificationDTO(
            model.getId(), model.getPassanger().getId(), model.getStatus(),
            model.getPrice(), model.getOrigin(), model.getDestiny()
        );
    }
}
