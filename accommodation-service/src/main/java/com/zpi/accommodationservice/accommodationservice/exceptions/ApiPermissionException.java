package com.zpi.accommodationservice.accommodationservice.exceptions;

public class ApiPermissionException extends RuntimeException{
    public ApiPermissionException(String message) {
        super(message);
    }

}
