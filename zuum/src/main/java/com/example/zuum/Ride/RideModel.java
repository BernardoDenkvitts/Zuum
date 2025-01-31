package com.example.zuum.Ride;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import org.locationtech.jts.geom.Point;

import com.example.zuum.Config.Jackson.PointDeserializer;
import com.example.zuum.Config.Jackson.PointSerializer;
import com.example.zuum.Driver.DriverModel;
import com.example.zuum.User.UserModel;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "ride")
public class RideModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "passanger_id", referencedColumnName = "id")
    private UserModel passanger;

    @ManyToOne
    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    private DriverModel driver;

    @Enumerated(EnumType.STRING)
    private RideStatus status;

    private BigDecimal price;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // This field helps to manage @timestamp in Drools' memory
    @Transient
    private Date date;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @JsonDeserialize(using = PointDeserializer.class)
    @JsonSerialize(using = PointSerializer.class)
    @Column(columnDefinition = "geography(Point, 4326)")
    private Point origin;

    @JsonDeserialize(using = PointDeserializer.class)
    @JsonSerialize(using = PointSerializer.class)
    @Column(columnDefinition = "geography(Point, 4326)")
    private Point destiny;

    public RideModel() {}

    public RideModel(Integer id, UserModel user, DriverModel driver, RideStatus status, BigDecimal price,
            LocalDateTime createdAt, LocalTime startTime, LocalTime endTime, Point origin, Point destiny) {
        this.id = id;
        this.passanger = user;
        this.driver = driver;
        this.status = status;
        this.price = price;
        this.createdAt = createdAt;
        this.date = Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
        this.startTime = startTime;
        this.endTime = endTime;
        this.origin = origin;
        this.destiny = destiny;
    }

    public RideModel(UserModel user, BigDecimal price, Point origin, Point destiny) {
        this.passanger = user;
        this.status = RideStatus.PENDING;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.date = Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
        this.startTime = null;
        this.endTime = null;
        this.origin = origin;
        this.destiny = destiny;
    }

    public Duration getRideDuration() {
        if (this.startTime == null || this.endTime == null) {
            return Duration.ofMinutes(0);
        }

        return Duration.between(this.startTime, this.endTime);
    }

    public Integer getId() {
        return id;
    }

    public UserModel getPassanger() {
        return passanger;
    }

    public DriverModel getDriver() {
        return driver;
    }

    public RideStatus getStatus() {
        return status;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public Date getDate() {
        return this.date;
    }

    public LocalTime getStarTime() {
        return startTime;
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

    public void setDriver(DriverModel driver) {
        this.driver = driver;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @PostLoad
    private void setDate() {
        this.date = Date.from(createdAt.atZone(ZoneId.systemDefault()).toInstant());
    }

}
