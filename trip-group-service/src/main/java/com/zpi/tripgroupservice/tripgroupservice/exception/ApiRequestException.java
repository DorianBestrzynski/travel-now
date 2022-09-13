package com.zpi.tripgroupservice.tripgroupservice.exception;

public class ApiRequestException extends RuntimeException{
    public ApiRequestException(String message){
        super(message);
    }

}
