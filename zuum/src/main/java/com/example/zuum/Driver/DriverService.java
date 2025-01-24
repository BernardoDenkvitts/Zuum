package com.example.zuum.Driver;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.zuum.Common.Exception.NotFoundException;
import com.example.zuum.Driver.Dto.LocationDTO;
import com.example.zuum.Driver.Dto.NewDriverDTO;
import com.example.zuum.Driver.Dto.UpdateDriverDataDTO;
import com.example.zuum.Driver.exception.DriverAlreadyExistsException;
import com.example.zuum.Driver.exception.DriverLicenseLinkedToAnotherDriverException;
import com.example.zuum.Driver.exception.PlateLinkedToAnotherCarException;
import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserRepository;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    public DriverService(DriverRepository driverRepository, UserRepository userRepository) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public DriverModel updateLocation(LocationDTO dto) {
        DriverModel driver = driverRepository.findById(dto.driverId()).orElseThrow(() -> new NotFoundException("Driver with id " + dto.driverId()));
        driver.updateLocation(dto.currLocation());
        driverRepository.save(driver);

        return driver;
    }

    public List<DriverModel> findDriversNearby(double lat, double longt, float maxDistance) {
        return driverRepository.findDriversNearby(lat, longt, maxDistance);
    }

    @Transactional
    public DriverModel createDriver(NewDriverDTO dto) {
        UserModel user = userRepository.findById(dto.userId()).orElseThrow(() -> new NotFoundException("User with id " + dto.userId()));

        if (driverRepository.findByUserId(dto.userId()).isPresent()) {
            throw new DriverAlreadyExistsException(dto.userId());
        }

        DriverModel newDriver = driverRepository.save(new DriverModel(dto.plate(), dto.carModel(), dto.driverLicense(), user));

        return newDriver;
    }

    @Transactional
    public DriverModel updateInformations(Integer id, UpdateDriverDataDTO dto) {
        DriverModel driver = driverRepository.findById(id).orElseThrow(() -> new NotFoundException("Driver with id " + id));

        driverRepository.findByPlate(dto.plate())
            .ifPresent(d -> {
                if (d.getId() != driver.getId()) {
                    throw new PlateLinkedToAnotherCarException(dto.plate()); 
                }
            });
        
        driverRepository.findByDriverLicense(dto.driverLicense())
            .ifPresent(d -> {
                if (d.getId() != driver.getId()) {
                    throw new DriverLicenseLinkedToAnotherDriverException(dto.driverLicense()); 
                }
            });
        
        driver.setPlate(dto.plate());
        driver.setCarModel(dto.carModel());
        driver.setDriverLicense(dto.driverLicense());
        
        driverRepository.save(driver);

        return driver;
    }

}
