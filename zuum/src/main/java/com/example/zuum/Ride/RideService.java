package com.example.zuum.Ride;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.zuum.Common.utils;
import com.example.zuum.Common.Exception.NotFoundException;
import com.example.zuum.Driver.DriverModel;
import com.example.zuum.Driver.DriverNotifier;
import com.example.zuum.Driver.DriverRepository;
import com.example.zuum.Ride.Dto.NewRideDTO;
import com.example.zuum.Ride.Dto.RideRequestNotificationDTO;
import com.example.zuum.Ride.exception.DriverRequestRideException;
import com.example.zuum.Ride.exception.RideRequestExistsException;
import com.example.zuum.Ride.exception.UserIsNotDriverException;
import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserRepository;
import com.example.zuum.User.UserType;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final DriverNotifier driverNotifier;

    static Logger LOGGER = utils.getLogger(RideService.class);

    public RideService(RideRepository rideRepository, UserRepository userRepository, DriverRepository driverRepository, DriverNotifier driverNotifier) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.driverNotifier = driverNotifier;
    }

    @Transactional
    public RideModel requestRide(NewRideDTO dto) {
        UserModel user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new NotFoundException("Passanger with id " + dto.userId()));

        if (user.getUserType() == UserType.DRIVER) {
            throw new DriverRequestRideException();
        }

        if (rideRepository.existsPendingRideRequest(user.getId())) {
            throw new RideRequestExistsException();
        }

        RideModel newRideRequest = rideRepository.save(dto.toRideModel(user));

        CompletableFuture.runAsync(() -> driverNotifier.newRideRequest(
                new RideRequestNotificationDTO(
                        newRideRequest.getId(), newRideRequest.getPassanger().getId(), newRideRequest.getStatus(),
                        newRideRequest.getPrice(), newRideRequest.getOrigin(), newRideRequest.getDestiny())));

        return newRideRequest;
    }

    @Scheduled(fixedRate = 350000)
    public void deletePendingRidesLongerThanFiveMinutes() {
        LOGGER.info("Deleteing trips with PENDING longer than 5 minutes");
        LocalDateTime limit = LocalDateTime.now().minusMinutes(5);
        List<RideModel> expiredRides = rideRepository.findByStatusAndCreatedAtBefore(RideStatus.PENDING, limit);
        for (RideModel ride : expiredRides) {
            rideRepository.delete(ride);
        }
    }

    public Page<RideModel> getRecentPendingRidesByLocation(Integer driverId, double lat, double longt, Pageable pageable) {
        DriverModel driver = driverRepository.findById(driverId).orElseThrow(() -> new NotFoundException("Driver with id " + driverId));

        if (driver.getUser().getUserType() == UserType.PASSANGER) {
            throw new UserIsNotDriverException();
        }

        return rideRepository.findPendingNearbyRides(lat, longt, 6000f, pageable);
    }

}
