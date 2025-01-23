package com.example.zuum.Ride.Dto;

import java.math.BigDecimal;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import com.example.zuum.Ride.RideModel;
import com.example.zuum.User.UserModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewRideDTO(
    @JsonProperty("user_id")
    @NotBlank(message = "User id cannot be blank")
    @NotNull(message = "User id cannot be null")
    Integer userId,
    
    @NotBlank(message = "Price cannot be blank")
    @NotNull(message = "Price cannot be null")
    BigDecimal price,
    
    @NotBlank(message = "Origin cannot be blank")
    @NotNull(message = "Origin cannot be null")
    Point origin,
    
    @NotBlank(message = "Destiny cannot be blank")
    @NotNull(message = "Destiny cannot be null")
    Point destiny
) {

    public RideModel toRideModel(UserModel user) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point originPoint = geometryFactory.createPoint(new Coordinate(origin.getX(), origin.getY()));
        Point destinyPoint = geometryFactory.createPoint(new Coordinate(destiny.getX(), destiny.getY()));

        return new RideModel(user, price, originPoint, destinyPoint);
    }
}
