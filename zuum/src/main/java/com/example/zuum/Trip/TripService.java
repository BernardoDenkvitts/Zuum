package com.example.zuum.Trip;

import org.springframework.stereotype.Service;

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
public record TripService(
    TripRepository tripRepository,
    UserRepository userRepository,
    DriverNotifier driverNotifier
) {
    public TripModel requestTrip(NewTripDTO dto) {
        UserModel user = userRepository.findById(dto.userId()).orElseThrow(() -> new NotFoundException("Passanger with id " + dto.userId()));

        if (user.getUserType() == UserType.DRIVER) {
            throw new DriverRequestTripException();
        }

        if (tripRepository.existsPendingTripRequest(user.getId())) {
            throw new TripRequestExistsException();
        }

        TripModel newTripRequest = tripRepository.save(dto.toTripModel());

        driverNotifier.newTripRequest(new TripRequestNotificationDTO(newTripRequest.getId(), newTripRequest.getUserId(), newTripRequest.getStatus(), newTripRequest.getPrice(), newTripRequest.getOrigin(), newTripRequest.getDestiny()));

        return newTripRequest;
    }
} 
