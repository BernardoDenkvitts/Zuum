package com.example.zuum.Ride;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.example.zuum.Ride.Dto.RideResponseDTO;
import com.example.zuum.Ride.Dto.RideRequestNotificationDTO;
import com.example.zuum.Ride.exception.DriverAlreadyHasAnAcceptedRideException;
import com.example.zuum.Ride.exception.DriverRequestRideException;
import com.example.zuum.Ride.exception.RideRequestExistsException;
import com.example.zuum.Ride.exception.RideStatusNotAllowed;
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

    private final Map<RideStatus, RideStatus> nextStatus = Map.of(
        RideStatus.PENDING, RideStatus.ACCEPTED,
        RideStatus.ACCEPTED, RideStatus.IN_PROGRESS,
        RideStatus.IN_PROGRESS, RideStatus.COMPLETED
    );

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
                driverRepository.findDriversNearby(dto.origin().getX(), dto.origin().getY(), 6000f)));

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
    public RideModel updateRideStatus(Integer rideId, Integer driverId, RideStatus newStatus) {
        DriverModel driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new NotFoundException("Driver with id " + rideId));
        
        validateDriver(driver.getUser().getUserType(), newStatus, driverId);
        
        RideModel ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride with id " + rideId));

        validateRide(ride.getStatus(), newStatus);

        ride.setStatus(newStatus);
        ride.setDriver(driver);
        rideRepository.save(ride);

        notifier.notifyUser(String.valueOf(ride.getPassanger().getId()), "/queue/ride-accepted", RideResponseDTO.create(ride));

        return ride;
    }

    private void validateDriver(UserType type, RideStatus newStatus, Integer id) {
        if (type != UserType.DRIVER) {
            throw new UserIsNotDriverException();
        }
        
        if (newStatus == RideStatus.ACCEPTED && rideRepository.driverHasAcceptedRide(id)) {
            throw new DriverAlreadyHasAnAcceptedRideException();
        }
    }

    private void validateRide(RideStatus currentStatus, RideStatus newStatus) {
        if (currentStatus == RideStatus.COMPLETED) {
            throw new RideStatusNotAllowed("The ride is completed, it cannot change its status");
        }

        if (nextStatus.get(currentStatus) != newStatus) {
            throw new RideStatusNotAllowed("Ride Status " + newStatus.name() + " not allowed, the next status must be "
                    + nextStatus.get(currentStatus).name());
        }
    }

}
