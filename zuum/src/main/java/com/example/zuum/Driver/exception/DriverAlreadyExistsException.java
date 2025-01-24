package com.example.zuum.Driver.exception;

public class DriverAlreadyExistsException extends RuntimeException {
    public DriverAlreadyExistsException(Integer userId) {
        super("Driver with user id " + userId + " already exists");
    }
}
