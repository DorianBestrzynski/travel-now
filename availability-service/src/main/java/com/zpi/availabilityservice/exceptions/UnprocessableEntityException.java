package com.zpi.availabilityservice.exceptions;

public class UnprocessableEntityException extends RuntimeException{
    public UnprocessableEntityException(String message){
        super(message);
    }

}
