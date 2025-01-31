package com.example.zuum.Ride.Dto;

import java.time.LocalDateTime;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

public class PriceRequestDTO {
    @JsonProperty("user_id")
    @NotNull(message = "User id cannot be null")
    Integer userId;

    @JsonIgnore
    LocalDateTime createdAt;

    @NotNull(message = "Origin cannot be null")
    Point origin;

    @NotNull(message = "Destiny cannot be null")
    Point destiny;

    public PriceRequestDTO() {
    }

    public PriceRequestDTO(Integer userId, Point origin, Point destiny) {
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.origin = origin;
        this.destiny = destiny;
    }

    public Integer getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Point getOrigin() {
        return origin;
    }

    public Point getDestiny() {
        return destiny;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}