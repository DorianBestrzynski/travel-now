package com.zpi.transportservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    public final static ZoneId ZONE_ID = ZoneId.of("Europe/Warsaw");

    @ExceptionHandler(value = {LufthansaApiException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(LufthansaApiException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ApiPermissionException.class})
    public ResponseEntity<Object> handleApiPermissionException(ApiPermissionException ex){
        return handleExceptions(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<Object> handleExceptions(String message, HttpStatus request){
        ApiException apiException = new ApiException(message, request, ZonedDateTime.now(ZONE_ID));
        return new ResponseEntity<>(apiException, request);
    }
}
