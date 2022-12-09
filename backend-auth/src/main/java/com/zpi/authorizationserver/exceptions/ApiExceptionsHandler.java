package com.zpi.authorizationserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.net.ConnectException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice
public class ApiExceptionsHandler {

    public final static ZoneId ZONE_ID = ZoneId.of("Europe/Warsaw");

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException e){
        return handleExceptions(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(EntityNotFoundException e){
        return handleExceptions(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ConnectException.class})
    public ResponseEntity<Object> handleConnectException(ConnectException e){
        return handleExceptions(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(value = {NoSuchElementException.class})
    public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException e){
        return handleExceptions(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ApiPermissionException.class})
    public ResponseEntity<Object> handleApiPermissionException(ApiPermissionException e){
        return handleExceptions(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<Object> handleExceptions(String message, HttpStatus request){
        ApiException apiException = new ApiException(message, request, ZonedDateTime.now(ZONE_ID));
        return new ResponseEntity<>(apiException, request);
    }
}
