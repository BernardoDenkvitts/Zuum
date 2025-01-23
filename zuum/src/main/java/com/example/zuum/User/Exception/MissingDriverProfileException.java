package com.example.zuum.User.Exception;

public class MissingDriverProfileException extends RuntimeException {
    public MissingDriverProfileException() {
        super("The user needs to register as a Driver first to be able to change the Status");
    }
}
