package com.zpi.accommodationservice.accommodationservice.exceptions;

public class SiteNotFoundException extends RuntimeException {
    public SiteNotFoundException(String message) {
        super(message);
    }
}
