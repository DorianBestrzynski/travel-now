package com.zpi.availabilityservice.exceptions;

public class ApiPermissionException extends RuntimeException{
    public ApiPermissionException(String message){
        super(message);
    }

}
