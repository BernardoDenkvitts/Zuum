package com.example.zuum.Ride.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.zuum.Config.Jackson.PointSerializer;
import com.example.zuum.Ride.RideModel;
import com.example.zuum.Ride.RideStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.locationtech.jts.geom.Point;

public record RideResponseDTO(
    Integer id,

    @JsonProperty("passanger_id")
    Integer passangerId,

    @JsonProperty("driver_id") 
    Integer driverId,

    RideStatus status,
    BigDecimal price,

    @JsonSerialize(using = PointSerializer.class)
    Point origin,

    @JsonSerialize(using = PointSerializer.class)
    Point destiny,

    @JsonProperty("created_at") @JsonFormat(shape = JsonFormat.Shape.STRING) LocalDateTime createdAt
) {
    public static RideResponseDTO create(RideModel model) {
        return new RideResponseDTO(
                model.getId(), model.getPassanger().getId(), null, model.getStatus(), model.getPrice(),
                model.getOrigin(), model.getDestiny(), model.getCreatedAt());
    }
}
