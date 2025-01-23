package com.example.zuum.http;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.example.zuum.Common.utils;
import com.example.zuum.Common.Exception.NotFoundException;
import com.example.zuum.Trip.exception.DriverRequestTripException;
import com.example.zuum.Trip.exception.TripRequestExistsException;
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
        LOGGER.info("INTERNAL SERVER ERROR -> {} \n {}", ex.getMessage(), ex.getStackTrace());
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

    @ExceptionHandler({ DriverRequestTripException.class, MethodArgumentNotValidException.class,
            ValidationException.class, HttpMessageNotReadableException.class, MissingDriverProfileException.class })
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

    @ExceptionHandler({ TripRequestExistsException.class, EmailAlreadyInUseException.class })
    public ProblemDetail handleConflict(RuntimeException ex) {
        ProblemDetail pb = getProblemDetail(HttpStatus.CONFLICT);
        pb.setTitle("Conflict");
        pb.setType(URI.create("Zuum/conflict"));
        pb.setDetail(ex.getMessage());

        return pb;
    }

}
