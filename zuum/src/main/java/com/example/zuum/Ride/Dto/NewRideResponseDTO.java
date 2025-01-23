package com.example.zuum.Ride.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.zuum.Ride.RideModel;
import com.example.zuum.Ride.RideStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.locationtech.jts.geom.Point;

public record NewRideResponseDTO(
    Integer id,

    @JsonProperty("user_id") Integer userId,

    RideStatus status,
    BigDecimal price,
    Point origin,
    Point destiny,

    @JsonProperty("created_at") @JsonFormat(shape = JsonFormat.Shape.STRING) LocalDateTime createdAt
) {
    public static NewRideResponseDTO create(RideModel model) {
        return new NewRideResponseDTO(
                model.getId(), model.getPassanger().getId(), model.getStatus(), model.getPrice(),
                model.getOrigin(), model.getDestiny(), model.getCreatedAt());
    }
}
