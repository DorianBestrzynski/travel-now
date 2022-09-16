package com.zpi.dayplanservice.exception;

public class ApiPermissionException extends RuntimeException{
    public ApiPermissionException(String message){
        super(message);
    }

}
