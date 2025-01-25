package com.example.zuum.Ride.exception;

public class RideStatusIsNotPendingException extends RuntimeException {
    public RideStatusIsNotPendingException() {
        super("Current ride status is not 'Pending'");
    }
}
