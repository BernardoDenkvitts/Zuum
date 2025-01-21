package com.example.zuum.Driver;

import org.springframework.stereotype.Service;

import com.example.zuum.Common.Exception.NotFoundException;
import com.example.zuum.Driver.Dto.LocationDTO;

@Service
public record DriverService(
    DriverRepository repository
) {
    public DriverModel updateLocation(LocationDTO dto) {
        DriverModel driver = repository.findById(dto.driverId()).orElseThrow(() -> new NotFoundException("Driver with id " + dto.driverId()));
        driver.updateLocation(dto.currLocation());
        repository.save(driver);

        return driver;
    }
} 
