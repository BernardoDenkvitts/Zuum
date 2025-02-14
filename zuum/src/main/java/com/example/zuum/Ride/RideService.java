package com.example.zuum.Ride;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.zuum.Common.utils;
import com.example.zuum.Common.Exception.NotFoundException;
import com.example.zuum.Distance.IDistanceCalculator;
import com.example.zuum.Driver.DriverModel;
import com.example.zuum.Driver.DriverRepository;
import com.example.zuum.Drools.DroolsService;
import com.example.zuum.Notification.WsNotifier;
import com.example.zuum.Notification.Dto.WsMessageDTO;
import com.example.zuum.Notification.Dto.WsMessageType;
import com.example.zuum.Ride.Dto.NewRideDTO;
import com.example.zuum.Ride.Dto.PriceRequestDTO;
import com.example.zuum.Ride.Dto.RideResponseDTO;
import com.example.zuum.Ride.Dto.RideRequestNotificationDTO;
import com.example.zuum.Ride.exception.DriverRequestRideException;
import com.example.zuum.Ride.exception.MissingNecessaryParameters;
import com.example.zuum.Ride.exception.RideStatusNotAllowed;
import com.example.zuum.Ride.exception.UserIsNotDriverException;
import com.example.zuum.Ride.exception.UserNotRelatedToRide;
import com.example.zuum.Ride.exception.UserInARideException;
import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserRepository;
import com.example.zuum.User.UserType;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final WsNotifier notifier;
    private final IDistanceCalculator distanceCalculator;
    private final DroolsService droolsService;

    private final Map<RideStatus, RideStatus> nextStatus = Map.of(
            RideStatus.PENDING, RideStatus.ACCEPTED,
            RideStatus.ACCEPTED, RideStatus.IN_PROGRESS,
            RideStatus.IN_PROGRESS, RideStatus.COMPLETED);

    static Logger LOGGER = utils.getLogger(RideService.class);

    public RideService(RideRepository rideRepository, UserRepository userRepository, DriverRepository driverRepository,
            WsNotifier driverNotifier, IDistanceCalculator distanceCalculator, DroolsService kieSession) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.notifier = driverNotifier;
        this.distanceCalculator = distanceCalculator;
        this.droolsService = kieSession;
    }

    @Transactional
    public RideModel requestRide(NewRideDTO dto) {
        UserModel user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new NotFoundException("Passenger with id " + dto.userId()));

        if (user.getUserType() == UserType.DRIVER) {
            throw new DriverRequestRideException();
        }

        if (rideRepository.findActiveRideByUser(user.getId(), false).isPresent()) {
            throw new UserInARideException("Passenger with id " + user.getId() + " is in a ride");
        }

        RideModel newRideRequest = rideRepository.save(dto.toRideModel(user));

        CompletableFuture.runAsync(() -> notifier.newRideRequest(
                RideRequestNotificationDTO.create(newRideRequest),
                driverRepository.findDriversNearby(dto.origin().getX(), dto.origin().getY(), 6000f)));

        return newRideRequest;
    }

    @Scheduled(fixedRate = 300000)
    public void deletePendingRidesLongerThanFiveMinutes() {
        LOGGER.info("Deleteing rides with PENDING longer than 5 minutes");
        LocalDateTime limit = LocalDateTime.now().minusMinutes(5);
        List<RideModel> expiredRides = rideRepository.findByStatusAndCreatedAtBefore(RideStatus.PENDING, limit);
        for (RideModel ride : expiredRides) {
            rideRepository.delete(ride);
        }
    }

    public Page<RideModel> getRecentPendingRidesByLocation(Integer driverId, double longt, double lat,
            Pageable pageable) {
        DriverModel driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new NotFoundException("Driver with id " + driverId));

        if (driver.getUser().getUserType() == UserType.PASSENGER) throw new UserIsNotDriverException();

        return rideRepository.findPendingNearbyRides(longt, lat, 6000f, pageable);
    }

    @Transactional
    public RideModel updateRideStatus(Integer rideId, Integer driverId, RideStatus newStatus) {
        DriverModel driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new NotFoundException("Driver with id " + driverId));

        validateDriver(driver.getUser().getUserType(), newStatus, driverId);

        RideModel ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride with id " + rideId));

        validateRide(ride.getStatus(), newStatus);

        if (newStatus == RideStatus.IN_PROGRESS) ride.setStartTime(LocalTime.now());
        else if (newStatus == RideStatus.COMPLETED) ride.setEndTime(LocalTime.now());

        ride.setStatus(newStatus);
        ride.setDriver(driver);
        rideRepository.save(ride);

        notifier.notifyUser(ride.getPassenger().getEmail(), "/queue/ride",
                            new WsMessageDTO(WsMessageType.RIDE_UPDATE, RideResponseDTO.create(ride)));

        if (ride.getStatus() != RideStatus.ACCEPTED) droolsService.updateRideModel(ride);
        else droolsService.insert(ride);

        return ride;
    }

    private void validateDriver(UserType type, RideStatus newStatus, Integer id) {
        if (type != UserType.DRIVER) throw new UserIsNotDriverException();

        if (newStatus == RideStatus.ACCEPTED && rideRepository.findActiveRideByUser(id, true).isPresent()) {
            throw new UserInARideException("Driver with id " + id + " is in a ride");
        }
    }

    private void validateRide(RideStatus currentStatus, RideStatus newStatus) {
        if (currentStatus == RideStatus.COMPLETED) throw new RideStatusNotAllowed("The ride is completed, it cannot change its status");

        if (nextStatus.get(currentStatus) != newStatus) {
            throw new RideStatusNotAllowed("Ride Status " + newStatus.name() + " not allowed, the next status must be "
                    + nextStatus.get(currentStatus).name());
        }
    }

    public RideModel getData(Integer rideId, Integer driverId, Integer passengerId) {
        RideModel ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new NotFoundException("Ride with id " + rideId));

        if (driverId == null && passengerId == null) throw new MissingNecessaryParameters();

        if (driverId != null) {
            driverRepository.findById(driverId).orElseThrow(() -> new NotFoundException("Driver with id " + driverId));
            if (ride.getDriver().getId() != driverId) {
                throw new UserNotRelatedToRide("Driver with id " + driverId + " is not related to ride");
            }
        } else {
            userRepository.findById(passengerId).orElseThrow(() -> new NotFoundException("Passenger with id " + passengerId));

            if (ride.getPassenger().getId() != passengerId) {
                throw new UserNotRelatedToRide("Passenger with id " + passengerId + " is not related to ride");
            }
        }

        return ride;
    }

    public RidePrice calculateRidePrice(PriceRequestDTO dto) {
        double distance = distanceCalculator.calculate(dto.getOrigin().getX(), dto.getOrigin().getY(), dto.getDestiny().getX(), dto.getDestiny().getY());
        LOGGER.info("Ride distance -> {}", distance);

        RidePrice ridePrice = new RidePrice(distance);

        droolsService.insert(dto);
        droolsService.insert(ridePrice);

        droolsService.fireAllRules();

        droolsService.deleteFacts();

        return ridePrice;
    }

}
