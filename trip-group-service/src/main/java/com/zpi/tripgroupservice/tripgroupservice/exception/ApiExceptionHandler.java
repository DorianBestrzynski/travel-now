package com.zpi.tripgroupservice.tripgroupservice.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    private ResponseEntity<Object> handleExceptions(String message, HttpStatus request){
        ApiException apiException = new ApiException(message, request, ZonedDateTime.now(ZONE_ID));
        return new ResponseEntity<>(apiException, request);
    }

}
