package com.zpi.accommodationservice.exceptions;

public class UnprocessableEntityException extends RuntimeException{
    public UnprocessableEntityException(String message){
        super(message);
    }

}
