package com.example.zuum.http;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.zuum.Common.Exception.NotFoundException;
import com.example.zuum.Trip.exception.DriverRequestTripException;
import com.example.zuum.Trip.exception.TripRequestExistsException;

@RestControllerAdvice
public class ExceptionsHandler {

    private ProblemDetail getProblemDetail(HttpStatusCode statusCode) {
        return ProblemDetail.forStatus(statusCode.value());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleInternalServerError(RuntimeException ex) {
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

    @ExceptionHandler(DriverRequestTripException.class)
    public ProblemDetail handleUnprocessableEntity(RuntimeException ex) {
        ProblemDetail pb = getProblemDetail(HttpStatus.UNPROCESSABLE_ENTITY);
        pb.setTitle("Unprocessable Entity");
        pb.setType(URI.create("Zuum/unprocessable-entity"));
        pb.setDetail(ex.getMessage());

        return pb;
    }

    @ExceptionHandler(TripRequestExistsException.class)
    public ProblemDetail handleConflict(RuntimeException ex) {
        ProblemDetail pb = getProblemDetail(HttpStatus.CONFLICT);
        pb.setTitle("Conflict");
        pb.setType(URI.create("Zuum/conflict"));
        pb.setDetail(ex.getMessage());

        return pb;
    }

}
