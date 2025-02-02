package com.example.zuum.Financial;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.zuum.Common.Exception.NotFoundException;
import com.example.zuum.Driver.DriverModel;
import com.example.zuum.Driver.DriverRepository;
import com.example.zuum.Ride.RideRepository;
import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserRepository;

@Service
public class FinancialService {
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;


    public FinancialService(RideRepository rideRepository, UserRepository userRepository, DriverRepository driverRepository) {
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
    }

    public BigDecimal getSumPriceBetweenDates(Integer passengerId, Integer driverId, LocalDate startDate, LocalDate endDate) {
        if (passengerId != null) {
            userRepository.findById(passengerId).orElseThrow(() -> new NotFoundException("Passenger with id " + passengerId));
        } else if (driverId != null) {
            driverRepository.findById(driverId).orElseThrow(() -> new NotFoundException("Driver with id " + driverId));
        }

        LocalDateTime starDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        BigDecimal total = rideRepository.sumRidesAmountsBetweenDates(passengerId, driverId, starDateTime, endDateTime);

        return total != null ? total : BigDecimal.valueOf(0);
    }

}
