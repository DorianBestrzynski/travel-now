package com.zpi.tripgroupservice.tripgroupservice.exception;
import org.springframework.http.HttpStatus;
import java.time.ZonedDateTime;

public record ApiException(String message, HttpStatus status, ZonedDateTime timestamp) {
}
