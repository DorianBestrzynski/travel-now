package com.zpi.financeoptimizerservice.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiRestException extends RuntimeException {
    private final HttpStatus status;

    public ApiRestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ApiRestException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }
}
