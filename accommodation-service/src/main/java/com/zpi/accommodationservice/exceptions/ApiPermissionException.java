package com.zpi.accommodationservice.exceptions;

public class ApiPermissionException extends RuntimeException{

    public ApiPermissionException(String message) {
        super(message);
    }
}
