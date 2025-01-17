package com.example.zuum.Trip.exception;

public class TripRequestExistsException extends RuntimeException {
    public TripRequestExistsException() {
        super("Trip request already exists");
    }
}
