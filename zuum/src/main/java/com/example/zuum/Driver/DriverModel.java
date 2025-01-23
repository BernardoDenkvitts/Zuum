package com.example.zuum.Driver;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Point;

import com.example.zuum.Config.Jackson.PointDeserializer;
import com.example.zuum.Config.Jackson.PointSerializer;
import com.example.zuum.Ride.RideModel;
import com.example.zuum.User.UserModel;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "driver")
public class DriverModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String plate;
    
    @Column(name = "car_model")
    private String carModel;
    
    @Column(name = "driver_license")
    private String driverLicense;

    @JsonDeserialize(using = PointDeserializer.class)
    @JsonSerialize(using = PointSerializer.class)
    @Column(name = "curr_location", columnDefinition = "geography(Point, 4326)")
    private Point currLocation;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private UserModel user;

    @OneToMany(mappedBy = "driver")
    private List<RideModel> rides = new ArrayList<>();

    public DriverModel() {}

    public DriverModel(Integer id, String plate, String carModel, String driverLicense, Point currLocation, UserModel user) {
        this.id = id;
        this.plate = plate;
        this.carModel = carModel;
        this.driverLicense = driverLicense;
        this.currLocation = currLocation;
        this.user = user;
    }

    public void updateLocation(Point newLocation) {
        this.currLocation = newLocation;
    }

    public List<RideModel> getRides() {
        return rides;
    }

}
