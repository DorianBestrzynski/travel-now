package com.zpi.availabilityservice.exceptions;

import feign.Response;
import feign.codec.ErrorDecoder;
import io.micrometer.core.instrument.util.IOUtils;
import org.json.JSONObject;

import javax.persistence.EntityNotFoundException;

import static com.zpi.availabilityservice.exceptions.ExceptionInfo.*;

public class CustomFeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        String message;
        try {
            String complexMessage = IOUtils.toString(response.body().asInputStream());
            JSONObject resultObject = new JSONObject(complexMessage);
            message  = resultObject.getString("message");

        } catch (Exception ex) {
            throw new RuntimeException(EXCEPTION_FEIGN);
        }

        return switch (response.status()) {
            case 400 -> new IllegalArgumentException(message != null ? message : ILLEGAL_ARGUMENT_FEIGN);
            case 403 -> new IllegalArgumentException(message != null ? message : FORBIDDEN_FEIGN);
            case 404 -> new EntityNotFoundException(message != null ? message : RESOURCE_NOT_FOUND_FEIGN);
            case 422 -> new EntityNotFoundException(message != null ? message : UNPROCESSABLE_ENTITY_FEIGN);
            case 503 -> new EntityNotFoundException(message != null ? message : SERVICE_UNAVAILABLE_FEIGN);

            default -> new Exception(EXCEPTION_FEIGN);
        };
    }
}
