package com.example.zuum.Ride.exception;

public class UserNotRelatedToRide extends RuntimeException {
    public UserNotRelatedToRide(String message) {
        super(message);
    }
}
