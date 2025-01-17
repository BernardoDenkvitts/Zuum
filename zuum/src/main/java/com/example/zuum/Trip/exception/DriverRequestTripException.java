package com.example.zuum.Trip.exception;

public class DriverRequestTripException extends RuntimeException {
    public DriverRequestTripException() {
        super("Driver is not allowed to request a trip");
    }

}
