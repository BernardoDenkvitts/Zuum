package com.example.zuum.http;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.example.zuum.Common.utils;
import com.example.zuum.Common.Exception.NotFoundException;
import com.example.zuum.Driver.exception.DriverAlreadyExistsException;
import com.example.zuum.Driver.exception.DriverLicenseLinkedToAnotherDriverException;
import com.example.zuum.Driver.exception.PlateLinkedToAnotherCarException;
import com.example.zuum.Notification.Exception.UserIsNotConnectedException;
import com.example.zuum.Ride.RideStatus;
import com.example.zuum.Ride.exception.DriverAlreadyHasAnAcceptedRideException;
import com.example.zuum.Ride.exception.DriverRequestRideException;
import com.example.zuum.Ride.exception.RideRequestExistsException;
import com.example.zuum.Ride.exception.RideStatusNotAllowed;
import com.example.zuum.Ride.exception.UserIsNotDriverException;
import com.example.zuum.User.Exception.EmailAlreadyInUseException;
import com.example.zuum.User.Exception.MissingDriverProfileException;

import jakarta.validation.ValidationException;

@RestControllerAdvice
public class ExceptionsHandler {

    Logger LOGGER = utils.getLogger(ExceptionsHandler.class);

    private ProblemDetail getProblemDetail(HttpStatusCode statusCode) {
        return ProblemDetail.forStatus(statusCode.value());
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleInternalServerError(RuntimeException ex) {
        LOGGER.info("INTERNAL SERVER ERROR -> {} \n {} \n {} \n {}", ex.getMessage(), ex.getCause(), ex.getLocalizedMessage(), ex.getClass());
        ProblemDetail pb = getProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR);
        pb.setTitle("Internal Server Error");
        pb.setType(URI.create("Zuum/internal-server-error"));
        pb.setDetail("Internal Server Error");

        return pb;
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handlerNotFound(NotFoundException ex) {
        ProblemDetail pb = getProblemDetail(HttpStatus.NOT_FOUND);
        pb.setTitle("Not Found");
        pb.setType(URI.create("Zuum/not-found"));
        pb.setDetail(ex.getMessage());

        return pb;
    }

    @ExceptionHandler({ 
        MethodArgumentNotValidException.class, ValidationException.class,
        HttpMessageNotReadableException.class, MissingDriverProfileException.class,
        UserIsNotConnectedException.class, RideStatusNotAllowed.class
     })
    public ProblemDetail handleUnprocessableEntity(Exception ex) {
        ProblemDetail pb = getProblemDetail(HttpStatus.UNPROCESSABLE_ENTITY);
        pb.setTitle("Unprocessable Entity");
        pb.setType(URI.create("Zuum/unprocessable-entity"));

        if (ex instanceof MethodArgumentNotValidException == false) {
            pb.setDetail(ex.getMessage());
        } else {
            MethodArgumentNotValidException validationException = (MethodArgumentNotValidException) ex;
            List<FieldError> errors = validationException.getFieldErrors();
            List<String> errorMessage = errors.stream()
                    .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.toList());
            pb.setDetail(errorMessage.toString());
        }

        return pb;
    }

    @ExceptionHandler({ 
        RideRequestExistsException.class, EmailAlreadyInUseException.class, DriverAlreadyExistsException.class,
        PlateLinkedToAnotherCarException.class, DriverLicenseLinkedToAnotherDriverException.class, DriverAlreadyHasAnAcceptedRideException.class
    })
    public ProblemDetail handleConflict(RuntimeException ex) {
        ProblemDetail pb = getProblemDetail(HttpStatus.CONFLICT);
        pb.setTitle("Conflict");
        pb.setType(URI.create("Zuum/conflict"));
        pb.setDetail(ex.getMessage());

        return pb;
    }

    @ExceptionHandler({UserIsNotDriverException.class, DriverRequestRideException.class})
    public ProblemDetail handleUnauthorized(RuntimeException ex) {
        ProblemDetail pb = getProblemDetail(HttpStatus.FORBIDDEN);
        pb.setTitle("Forbidden");
        pb.setType(URI.create("Zuum/forbidden"));
        pb.setDetail(ex.getMessage());

        return pb;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentNotValid(RuntimeException ex) {
        ProblemDetail pb = getProblemDetail(HttpStatus.BAD_REQUEST);
        pb.setTitle("Bad Request");
        pb.setType(URI.create("Zuum/bad-request"));
        if (ex.getMessage().contains("RideStatus")) {
            pb.setDetail("Invalid Ride Status. Valid status: " + RideStatus.allowedValues());
        } else {
            pb.setDetail(ex.getMessage());
        }

        return pb;
    }
}
