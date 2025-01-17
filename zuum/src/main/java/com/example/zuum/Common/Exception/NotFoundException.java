package com.example.zuum.Common.Exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message + " was not found");
    }
}
