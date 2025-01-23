package com.example.zuum.Ride.exception;

public class DriverRequestRideException extends RuntimeException {
    public DriverRequestRideException() {
        super("Driver is not allowed to request a ride");
    }

}
