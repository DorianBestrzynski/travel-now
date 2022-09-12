package com.zpi.tripgroupservice.tripgroupservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    private final static ZoneId ZONE_ID = ZoneId.of("Europe/Warsaw");

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> handleApiRequestException(IllegalArgumentException ex){
        return handleExceptions(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handleApiRequestException(EntityNotFoundException ex){
        return handleExceptions(ex, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<Object> handleExceptions(RuntimeException ex, HttpStatus request){
        ApiException apiException = new ApiException(ex.getMessage(), request, ZonedDateTime.now(ZONE_ID));
        return new ResponseEntity<>(apiException, request);
    }
}
