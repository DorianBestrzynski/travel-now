package com.zpi.dayplanservice.exception;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javax.management.ServiceNotFoundException;
import java.io.IOException;
import static com.zpi.dayplanservice.exception.ExceptionInfo.*;

@Slf4j
@RequiredArgsConstructor
public class CustomFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        String message = null;
        try {
            String complexMessage = IOUtils.toString(response.body().asInputStream());
            JSONObject resultObject = new JSONObject(complexMessage);
            message  = resultObject.getString("message");

        } catch (Exception exception) {
            throw new RuntimeException(EXCEPTION_FEIGN);
        }

        return switch (response.status()) {
            case 400 -> new IllegalArgumentException(message != null ? message : ILLEGAL_ARGUMENT_FEIGN);
            case 401 -> new ApiPermissionException(message != null? message : UNAUTHORIZED_REQUEST_FEIGN);
            case 404 -> new ApiRequestException(message != null ? message : REQUESTED_ENTITY_NOT_FOUND_FEIGN);
            case 409 -> new IllegalDateException(message != null ? message : TAKEN_DATE);
            case 422 -> new UnprocessableEntityException(message != null ? message : UNPROCESSABLE_ENTITY_FEIGN);
            case 503 -> new ServiceNotFoundException(message != null ? message : SERVICE_UNAVAILABLE_FEIGN);
            default -> new Exception(EXCEPTION_FEIGN);
        };
    }
}
