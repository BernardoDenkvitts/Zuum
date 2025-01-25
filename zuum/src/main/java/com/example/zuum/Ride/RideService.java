package com.example.zuum.Ride;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.zuum.Common.utils;
import com.example.zuum.Common.Exception.NotFoundException;
import com.example.zuum.Driver.DriverModel;
import com.example.zuum.Driver.DriverRepository;
import com.example.zuum.Notification.WsNotifier;
import com.example.zuum.Ride.Dto.NewRideDTO;
import com.example.zuum.Ride.Dto.RideAcceptedDTO;
import com.example.zuum.Ride.Dto.RideRequestNotificationDTO;

import com.example.zuum.Ride.exception.DriverAlreadyHasRideInProgressException;
import com.example.zuum.Ride.exception.DriverRequestRideException;
import com.example.zuum.Ride.exception.RideRequestExistsException;
import com.example.zuum.Ride.exception.RideStatusIsNotPendingException;
import com.example.zuum.Ride.exception.UserIsNotDriverException;
import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserRepository;
import com.example.zuum.User.UserType;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final WsNotifier notifier;

    static Logger LOGGER = utils.getLogger(RideService.class);

    public RideService(RideRepository rideRepository, UserRepository userRepository, DriverRepository driverRepository,
            WsNotifier driverNotifier) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.notifier = driverNotifier;
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

        CompletableFuture.runAsync(() -> notifier.newRideRequest(
            RideRequestNotificationDTO.create(newRideRequest),
            driverRepository.findDriversNearby(dto.origin().getX(), dto.origin().getY(), 6000f)
        ));

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

    public Page<RideModel> getRecentPendingRidesByLocation(Integer driverId, double lat, double longt,
            Pageable pageable) {
        DriverModel driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new NotFoundException("Driver with id " + driverId));

        if (driver.getUser().getUserType() == UserType.PASSANGER) {
            throw new UserIsNotDriverException();
        }

        return rideRepository.findPendingNearbyRides(lat, longt, 6000f, pageable);
    }

    @Transactional
    public RideModel acceptRide(Integer rideId, Integer driverId) {
        RideModel ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride with id " + rideId));

        if (ride.getStatus() != RideStatus.PENDING) {
            throw new RideStatusIsNotPendingException();
        }

        DriverModel driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new NotFoundException("Driver with id " + rideId));

        if (driver.getUser().getUserType() != UserType.DRIVER) {
            throw new UserIsNotDriverException();
        }

        if (rideRepository.driverHasInProgressRide(driverId)) {
            throw new DriverAlreadyHasRideInProgressException();
        }

        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setDriver(driver);
        rideRepository.save(ride);

        notifier.notifyUser(String.valueOf(ride.getPassanger().getId()), "/queue/ride-accepted", RideAcceptedDTO.create(ride));

        return ride;
    }

}
