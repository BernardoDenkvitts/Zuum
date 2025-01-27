package com.example.zuum.Ride.exception;

public class DriverAlreadyHasAnAcceptedRideException extends RuntimeException{
    public DriverAlreadyHasAnAcceptedRideException() {
        super("Driver already has a ride accepted");
    }
}
