package com.example.zuum.Ride.exception;

public class RideRequestExistsException extends RuntimeException {
    public RideRequestExistsException() {
        super("Trip request already exists");
    }
}
