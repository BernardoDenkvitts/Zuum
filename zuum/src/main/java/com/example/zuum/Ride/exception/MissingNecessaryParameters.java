package com.example.zuum.Ride.exception;

public class MissingNecessaryParameters extends RuntimeException {
    public MissingNecessaryParameters() {
        super("Driver ID or Passenger ID is missed");
    }

}
