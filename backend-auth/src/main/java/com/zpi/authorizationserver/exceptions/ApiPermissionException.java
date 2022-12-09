package com.zpi.authorizationserver.exceptions;

public class ApiPermissionException extends RuntimeException{

    public ApiPermissionException(String message){
        super(message);
    }
}

