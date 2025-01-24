package com.example.zuum.Driver.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateDriverDataDTO(
    @NotNull(message = "Plate cannot be null")
    @NotBlank(message = "Plate cannot be blank")
    String plate,

    @NotNull(message = "Car model cannot be null")
    @NotBlank(message = "Car model cannot be blank")
    @JsonProperty("car_model")
    String carModel,

    @NotNull(message = "Driver license cannot be null")
    @NotBlank(message = "Driver license cannot be blank")
    @JsonProperty("driver_license")
    String driverLicense
) {
}