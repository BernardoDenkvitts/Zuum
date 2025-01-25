package com.example.zuum.Driver.exception;

public class DriverLicenseLinkedToAnotherDriverException extends RuntimeException {
    public DriverLicenseLinkedToAnotherDriverException(String driverLicense) {
        super("The driver's license " + driverLicense + " is linked to a driver");
    }
}
