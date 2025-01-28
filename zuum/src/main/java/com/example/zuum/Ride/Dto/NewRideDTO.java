package com.example.zuum.Ride.Dto;

import java.math.BigDecimal;

import org.locationtech.jts.geom.Point;

import com.example.zuum.Ride.RideModel;
import com.example.zuum.User.UserModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

public record NewRideDTO(
    @JsonProperty("user_id")
    @NotNull(message = "User id cannot be null")
    Integer userId,
    
    @NotNull(message = "Price cannot be null")
    BigDecimal price,
    
    @NotNull(message = "Origin cannot be null")
    Point origin,
    
    @NotNull(message = "Destiny cannot be null")
    Point destiny
) {

    public RideModel toRideModel(UserModel user) {
        return new RideModel(user, price, origin, destiny);
    }
}
