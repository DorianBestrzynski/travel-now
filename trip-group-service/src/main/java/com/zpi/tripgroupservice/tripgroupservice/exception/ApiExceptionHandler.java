package com.zpi.tripgroupservice.tripgroupservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.net.ConnectException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;

import static com.zpi.tripgroupservice.tripgroupservice.exception.ExceptionInfo.GROUP_CREATION_VALIDATION_ERROR;

@ControllerAdvice
public class ApiExceptionHandler {

    public final static ZoneId ZONE_ID = ZoneId.of("Europe/Warsaw");

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex){
        return handleExceptions(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handleApiRequestException(ApiRequestException ex){
        return handleExceptions(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex){
        return handleExceptions(GROUP_CREATION_VALIDATION_ERROR, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = {ApiPermissionException.class})
    public ResponseEntity<Object> handleApiPermissionException(ApiPermissionException ex){
        return handleExceptions(ex.getMessage(), HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(EntityNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ConnectException.class})
    public ResponseEntity<Object> handleConnectException(ConnectException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(value = {NoSuchElementException.class})
    public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<Object> handleExceptions(String message, HttpStatus request){
        ApiException apiException = new ApiException(message, request, ZonedDateTime.now(ZONE_ID));
        return new ResponseEntity<>(apiException, request);
    }

}
