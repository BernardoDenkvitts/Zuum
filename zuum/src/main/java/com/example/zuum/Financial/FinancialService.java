package com.example.zuum.Financial;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.zuum.Common.Exception.NotFoundException;
import com.example.zuum.Driver.DriverModel;
import com.example.zuum.Driver.DriverRepository;
import com.example.zuum.Ride.RideRepository;
import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserRepository;
import com.example.zuum.User.Exception.MissingDriverProfileException;

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

    public BigDecimal getSumPriceBetweenDates(Integer userId, LocalDate startDate, LocalDate endDate, boolean isDriver) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : LocalDateTime.now();
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : LocalDateTime.now();
        
        if (isDriver) {
            DriverModel driver = driverRepository.findByUserId(userId)
            .orElseThrow(() -> new MissingDriverProfileException());
            
            return getTotalRideAmount(null, driver.getId(), startDateTime, endDateTime);
        } 

        UserModel user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id " + userId));

        return getTotalRideAmount(user.getId(), null, startDateTime, endDateTime);
    }

    private BigDecimal getTotalRideAmount(Integer passengerId, Integer driverId, LocalDateTime start, LocalDateTime end) {
        return Optional.ofNullable(rideRepository.sumRidesAmountsBetweenDates(passengerId, driverId, start, end))
                .orElse(BigDecimal.ZERO);
    }

}
