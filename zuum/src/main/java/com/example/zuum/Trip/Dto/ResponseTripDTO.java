package com.example.zuum.Trip.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.zuum.Trip.TripModel;
import com.example.zuum.Trip.TripStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.locationtech.jts.geom.Point;

public record ResponseTripDTO(
    Integer id,

    @JsonProperty("user_id") Integer userId,

    TripStatus status,
    BigDecimal price,
    Point origin,
    Point destiny,

    @JsonProperty("created_at") @JsonFormat(shape = JsonFormat.Shape.STRING) LocalDateTime createdAt
) {
    
    public static ResponseTripDTO createResponseTripDTO(TripModel model) {
        return new ResponseTripDTO(
            model.getId(), model.getPassanger().getId(), model.getStatus(), model.getPrice(),
            model.getOrigin(), model.getDestiny(), model.getCreatedAt()
        );
    }
}
