package com.example.zuum.Ride.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.zuum.Config.Jackson.PointSerializer;
import com.example.zuum.Driver.Dto.DriverResponseDTO;
import com.example.zuum.Ride.RideModel;
import com.example.zuum.Ride.RideStatus;
import com.example.zuum.User.Dto.UserResponseDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.locationtech.jts.geom.Point;

public record RideResponseDTO(
    Integer id,

    DriverResponseDTO driver,
    UserResponseDTO passenger,

    RideStatus status,
    BigDecimal price,

    @JsonSerialize(using = PointSerializer.class) Point origin,

    @JsonSerialize(using = PointSerializer.class) Point destiny,

    @JsonProperty("created_at") @JsonFormat(shape = JsonFormat.Shape.STRING) LocalDateTime createdAt,

    @JsonProperty("start_time") @JsonFormat(shape = JsonFormat.Shape.STRING) LocalTime startTime,
    @JsonProperty("end_time") @JsonFormat(shape = JsonFormat.Shape.STRING) LocalTime endTime
) {
    public static RideResponseDTO create(RideModel model) {
        DriverResponseDTO driverResponseDTO = model.getDriver() == null ? null : DriverResponseDTO.create(model.getDriver());

        return new RideResponseDTO(
            model.getId(), driverResponseDTO, UserResponseDTO.create(model.getPassenger()),
            model.getStatus(), model.getPrice(), model.getOrigin(), model.getDestiny(),
            model.getCreatedAt(), model.getStarTime(), model.getEndTime()
        );
    }
}
