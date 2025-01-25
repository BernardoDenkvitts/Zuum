package com.example.zuum.Ride.exception;

public class DriverAlreadyHasRideInProgressException extends RuntimeException{
    public DriverAlreadyHasRideInProgressException() {
        super("Driver already has a ride in progress");
    }
}
