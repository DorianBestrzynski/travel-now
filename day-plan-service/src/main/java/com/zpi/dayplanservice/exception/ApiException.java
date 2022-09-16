package com.zpi.dayplanservice.exception;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import java.time.ZonedDateTime;

public record ApiException(
        @JsonProperty("message")
        String message,
        @JsonProperty("status")
        HttpStatus status,
        @JsonProperty("timestamp")
        ZonedDateTime timestamp) {
}
