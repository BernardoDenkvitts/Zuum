package com.example.zuum.Trip;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.locationtech.jts.geom.Point;

import com.example.zuum.Config.Jackson.PointDeserializer;
import com.example.zuum.Config.Jackson.PointSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trip")
public class TripModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "driver_id")
    private Integer driverId;

    @Enumerated(EnumType.STRING)
    private TripStatus status;

    private BigDecimal price;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "end_time")
    private LocalTime endTime;

    @JsonDeserialize(using = PointDeserializer.class)
    @JsonSerialize(using = PointSerializer.class)
    @Column(columnDefinition = "geography")
    private Point origin;

    @JsonDeserialize(using = PointDeserializer.class)
    @JsonSerialize(using = PointSerializer.class)
    @Column(columnDefinition = "geography")
    private Point destiny;

    public TripModel(Integer id, Integer userId, Integer driverId, TripStatus status, BigDecimal price,
            LocalDateTime createdAt, LocalTime endTime, Point origin, Point destiny) {
        this.id = id;
        this.userId = userId;
        this.driverId = driverId;
        this.status = status;
        this.price = price;
        this.createdAt = createdAt;
        this.endTime = endTime;
        this.origin = origin;
        this.destiny = destiny;
    }

    public TripModel(Integer userId, BigDecimal price, Point origin, Point destiny) {
        this.userId = userId;
        this.status = TripStatus.PENDING;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.endTime = null;
        this.origin = origin;
        this.destiny = destiny;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public TripStatus getStatus() {
        return status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Point getOrigin() {
        return origin;
    }

    public Point getDestiny() {
        return destiny;
    }

}
