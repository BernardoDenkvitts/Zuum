package com.example.zuum.Driver;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.zuum.Common.Exception.NotFoundException;
import com.example.zuum.Driver.Dto.LocationDTO;
import com.example.zuum.Driver.Dto.NewDriverDTO;
import com.example.zuum.Driver.Dto.UpdateDriverDataDTO;
import com.example.zuum.Driver.exception.DriverAlreadyExistsException;
import com.example.zuum.Driver.exception.DriverLicenseLinkedToAnotherDriverException;
import com.example.zuum.Driver.exception.PlateLinkedToAnotherCarException;
import com.example.zuum.Notification.WsNotifier;
import com.example.zuum.Notification.Dto.WsMessageDTO;
import com.example.zuum.Notification.Dto.WsMessageType;
import com.example.zuum.Ride.RideModel;
import com.example.zuum.Ride.RideRepository;
import com.example.zuum.Ride.RideStatus;
import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserRepository;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final WsNotifier wsNotifier;

    public DriverService(DriverRepository driverRepository, UserRepository userRepository,
            RideRepository rideRepository, WsNotifier wsNotifier) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.wsNotifier = wsNotifier;
    }

    public DriverModel getInformations(Integer id) {
        return driverRepository.findById(id).orElseThrow(() -> new NotFoundException("Driver with id " + id));
    }

    @Transactional
    public DriverModel updateLocation(LocationDTO dto) {
        DriverModel driver = driverRepository.findById(dto.driverId())
                .orElseThrow(() -> new NotFoundException("Driver with id " + dto.driverId()));

        driver.updateLocation(dto.currLocation());
        driverRepository.save(driver);

        CompletableFuture.runAsync(() -> {
            Optional<RideModel> activeRide = rideRepository.findActiveRideByUser(driver.getId(), true);

            // Send the driver's location to the passenger in real time
            if (activeRide.isPresent() && activeRide.get().getStatus() == RideStatus.ACCEPTED) {
                Integer passengerId = activeRide.get().getPassenger().getId();
                wsNotifier.notifyUser(String.valueOf(passengerId),
                        "/queue/ride", new WsMessageDTO(WsMessageType.DRIVER_LOCATION_UPDATE, driver.getCurrLocation()));
            }
        });

        return driver;
    }

    @Transactional
    public DriverModel createDriver(NewDriverDTO dto) {
        UserModel user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new NotFoundException("User with id " + dto.userId()));

        if (driverRepository.findByUserId(dto.userId()).isPresent()) {
            throw new DriverAlreadyExistsException(dto.userId());
        }

        validateDriversData(dto.plate(), dto.driverLicense(), null);

        DriverModel newDriver = driverRepository
                .save(new DriverModel(dto.plate(), dto.carModel(), dto.driverLicense(), user));

        return newDriver;
    }

    @Transactional
    public DriverModel updateInformations(Integer id, UpdateDriverDataDTO dto) {
        DriverModel driver = driverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Driver with id " + id));

        validateDriversData(dto.plate(), dto.driverLicense(), id);

        driver.setPlate(dto.plate());
        driver.setCarModel(dto.carModel());
        driver.setDriverLicense(dto.driverLicense());

        driverRepository.save(driver);

        return driver;
    }

    private void validateDriversData(String plate, String driverLicense, Integer driverId) {
        CompletableFuture<Optional<DriverModel>> plateValidation = CompletableFuture
                .supplyAsync(() -> driverRepository.findByPlate(plate));

        CompletableFuture<Optional<DriverModel>> driverLicenseValidation = CompletableFuture
                .supplyAsync(() -> driverRepository.findByDriverLicense(driverLicense));

        try {
            CompletableFuture.allOf(plateValidation, driverLicenseValidation).join();

            Optional<DriverModel> plateResult = plateValidation.join();
            plateResult.ifPresent(d -> {
                if (!d.getId().equals(driverId)) {
                    throw new PlateLinkedToAnotherCarException(plate);
                }
            });

            Optional<DriverModel> licenseResult = driverLicenseValidation.join();
            licenseResult.ifPresent(d -> {
                if (!d.getId().equals(driverId)) {
                    throw new DriverLicenseLinkedToAnotherDriverException(driverLicense);
                }
            });

        } catch (CompletionException e) {
            if (e.getCause() instanceof PlateLinkedToAnotherCarException) {
                throw (PlateLinkedToAnotherCarException) e.getCause();
            } else if (e.getCause() instanceof DriverLicenseLinkedToAnotherDriverException) {
                throw (DriverLicenseLinkedToAnotherDriverException) e.getCause();
            } else {
                throw new RuntimeException("Unexpected validation error", e.getCause());
            }
        }
    }

    public Page<RideModel> getRides(Integer driverId, Pageable pageable) {
        driverRepository.findById(driverId).orElseThrow(() -> new NotFoundException("Driver with id " + driverId));
  
        return rideRepository.findRides(null, driverId, pageable);
    }

}
