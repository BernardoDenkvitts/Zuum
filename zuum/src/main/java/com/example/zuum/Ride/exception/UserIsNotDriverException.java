package com.example.zuum.Ride.exception;

public class UserIsNotDriverException extends RuntimeException {
    public UserIsNotDriverException() {
        super("Passenger is not allowed to access this resource.");
    }
}
