package com.example.zuum.Driver.exception;

public class PlateLinkedToAnotherCarException extends RuntimeException {
    public PlateLinkedToAnotherCarException(String plate) {
        super("The plate " + plate + " is linked to another car");
    }
}
