package com.example.zuum.Driver.Dto;

import org.locationtech.jts.geom.Point;

import com.example.zuum.Driver.DriverModel;
import com.example.zuum.User.Dto.UserResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DriverResponseDTO(
    Integer id,
    String plate,

    @JsonProperty("car_model")
    String carModel,

    @JsonProperty("driver_license")
    String driverLicense,

    @JsonProperty("user")
    UserResponseDTO user,

    @JsonProperty("curr_location")
    Point currLocation
) {
    public static DriverResponseDTO create(DriverModel model) {
        return new DriverResponseDTO(model.getId(), model.getPlate(),
                 model.getCarModel(), model.getDriverLicense(), UserResponseDTO.create(model.getUser()), model.getCurrLocation());
    }
} 
