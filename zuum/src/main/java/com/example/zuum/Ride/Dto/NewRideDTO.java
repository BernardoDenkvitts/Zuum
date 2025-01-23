package com.example.zuum.Ride.Dto;

import java.math.BigDecimal;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import com.example.zuum.Ride.RideModel;
import com.example.zuum.User.UserModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public record NewRideDTO(
        @JsonProperty("user_id") Integer userId,
        BigDecimal price,
        Point origin,
        Point destiny) {

    public RideModel toRideModel(UserModel user) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point originPoint = geometryFactory.createPoint(new Coordinate(origin.getX(), origin.getY()));
        Point destinyPoint = geometryFactory.createPoint(new Coordinate(destiny.getX(), destiny.getY()));

        return new RideModel(user, price, originPoint, destinyPoint);
    }
}
