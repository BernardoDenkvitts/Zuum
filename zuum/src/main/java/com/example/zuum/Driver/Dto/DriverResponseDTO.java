package com.example.zuum.Driver.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DriverResponseDTO(
    Integer id,
    String plate,

    @JsonProperty("car_model")
    String carModel,

    @JsonProperty("driver_license")
    String driverLicense,

    @JsonProperty("user_id")
    Integer userId
) {

} 
