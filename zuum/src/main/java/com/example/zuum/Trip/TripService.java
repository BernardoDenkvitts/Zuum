package com.example.zuum.Trip;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.zuum.Common.utils;
import com.example.zuum.Common.Exception.NotFoundException;
import com.example.zuum.Driver.DriverNotifier;
import com.example.zuum.Trip.Dto.NewTripDTO;
import com.example.zuum.Trip.Dto.TripRequestNotificationDTO;
import com.example.zuum.Trip.exception.DriverRequestTripException;
import com.example.zuum.Trip.exception.TripRequestExistsException;
import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserRepository;
import com.example.zuum.User.UserType;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final  UserRepository userRepository;
    private final  DriverNotifier driverNotifier;

    static Logger LOGGER = utils.getLogger(TripService.class);

    public TripService(TripRepository tripRepository, UserRepository userRepository, DriverNotifier driverNotifier) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.driverNotifier = driverNotifier;
    }

    @Transactional
    public TripModel requestTrip(NewTripDTO dto) {
        UserModel user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new NotFoundException("Passanger with id " + dto.userId()));

        if (user.getUserType() == UserType.DRIVER) {
            throw new DriverRequestTripException();
        }

        if (tripRepository.existsPendingTripRequest(user.getId())) {
            throw new TripRequestExistsException();
        }

        TripModel newTripRequest = tripRepository.save(dto.toTripModel(user));

        driverNotifier.newTripRequest(
            new TripRequestNotificationDTO(newTripRequest.getId(),
            newTripRequest.getPassanger().getId(), newTripRequest.getStatus(), newTripRequest.getPrice(),
            newTripRequest.getOrigin(), newTripRequest.getDestiny())
        );

        return newTripRequest;
    }

    @Scheduled(fixedRate = 350000)
    public void deletePendingTripsLongerThanFiveMinutes() {
        LOGGER.info("Deleteing trips with PENDING longer than 5 minutes");
        LocalDateTime limit = LocalDateTime.now().minusMinutes(5);
        List<TripModel> expiredTrips = tripRepository.findByStatusAndCreatedAtBefore(TripStatus.PENDING, limit);
        for (TripModel trip : expiredTrips) {
            tripRepository.delete(trip);
        }
    }

}
