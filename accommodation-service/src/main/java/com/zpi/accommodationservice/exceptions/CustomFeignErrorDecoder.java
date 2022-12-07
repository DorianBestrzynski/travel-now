package com.zpi.accommodationservice.exceptions;

import feign.Response;
import feign.codec.ErrorDecoder;
import io.micrometer.core.instrument.util.IOUtils;
import org.json.JSONObject;

import javax.management.ServiceNotFoundException;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;

import static com.zpi.accommodationservice.exceptions.ExceptionsInfo.*;

public class CustomFeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        String message = null;
        try {
            String complexMessage = IOUtils.toString(response.body().asInputStream());
            JSONObject resultObject = new JSONObject(complexMessage);
            if(resultObject.has("message"))
                message = resultObject.getString("message");

        } catch (Exception ex) {
            throw new RuntimeException(EXCEPTION_FEIGN);
        }

        return switch (response.status()) {
            case 400 -> new IllegalArgumentException(message != null ? message : ILLEGAL_ARGUMENT_FEIGN);
            case 401 -> new ApiPermissionException(message != null? message : UNAUTHORIZED_REQUEST_FEIGN);
            case 403 -> new ApiPermissionException(message != null ? message : FORBIDDEN_FEIGN);
            case 404 -> new EntityNotFoundException(message != null ? message : RESOURCE_NOT_FOUND_FEIGN);
            case 422 -> new UnprocessableEntityException(message != null ? message : UNPROCESSABLE_ENTITY_FEIGN);
            case 503 -> new ServiceNotFoundException(message != null ? message : SERVICE_UNAVAILABLE_FEIGN);
            default -> new Exception(EXCEPTION_FEIGN);
        };
    }
}
