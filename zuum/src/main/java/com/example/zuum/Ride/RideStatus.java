package com.example.zuum.Ride;

import java.util.stream.Stream;

public enum RideStatus {
    // Waiting for a driver to accept the ride
    PENDING,
    ACCEPTED,
    IN_PROGRESS,
    COMPLETED;


    public static String allowedValues() {
        String[] status = Stream.of(values()).map(Enum::name).toArray(String[]::new);
 
        return String.join(", ", status);
    }

}
