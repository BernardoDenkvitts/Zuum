package com.example.zuum.Trip.Dto;

import java.math.BigDecimal;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import com.example.zuum.Trip.TripModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public record NewTripDTO(
    @JsonProperty("user_id")
    Integer userId,
    BigDecimal price,
    Point origin,
    Point destiny
) {

    public TripModel toTripModel() {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point originPoint = geometryFactory.createPoint(new Coordinate(origin.getX(), origin.getY()));
        Point destinyPoint = geometryFactory.createPoint(new Coordinate(destiny.getX(), destiny.getY()));

        return new TripModel(userId, price, originPoint, destinyPoint);
    }
}
