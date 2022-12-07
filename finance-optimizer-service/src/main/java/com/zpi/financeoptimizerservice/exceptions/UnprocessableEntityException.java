package com.zpi.financeoptimizerservice.exceptions;

public class UnprocessableEntityException extends RuntimeException{
    public UnprocessableEntityException(String message){
        super(message);
    }

}
