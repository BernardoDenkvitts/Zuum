package com.example.zuum.Ride.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.locationtech.jts.geom.Point;

import com.example.zuum.Config.Jackson.PointSerializer;
import com.example.zuum.Driver.Dto.DriverResponseDTO;
import com.example.zuum.Ride.RideModel;
import com.example.zuum.Ride.RideStatus;
import com.example.zuum.User.Dto.UserResponseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


public record RideAcceptedDTO(
    Integer id,

    DriverResponseDTO driver,

    UserResponseDTO passanger,

    RideStatus status,

    BigDecimal price,
 
    @JsonSerialize(using = PointSerializer.class)
    Point origin,

    @JsonSerialize(using = PointSerializer.class)
    Point destiny,

    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    LocalDateTime createdAt
) {
    public static RideAcceptedDTO create(RideModel model) {
        return new RideAcceptedDTO(
            model.getId(), DriverResponseDTO.create(model.getDriver()), UserResponseDTO.create(model.getPassanger()),
            model.getStatus(), model.getPrice(), model.getOrigin(), model.getDestiny(), model.getCreatedAt()
        );
    }
}
