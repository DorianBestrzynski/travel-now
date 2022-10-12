package com.zpi.transportservice.transportservice.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import io.micrometer.core.instrument.util.IOUtils;
import org.json.JSONObject;

import java.io.IOException;

import static com.zpi.transportservice.transportservice.exception.ExceptionsInfo.EXCEPTION_FEIGN;
import static com.zpi.transportservice.transportservice.exception.ExceptionsInfo.ILLEGAL_ARGUMENT_FEIGN;

public class CustomFeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        String message = null;
        try {
            String complexMessage = IOUtils.toString(response.body().asInputStream());
            JSONObject resultObject = new JSONObject(complexMessage);
            message  = resultObject.getString("message");

        } catch (IOException ignored) {
        }

        return switch (response.status()) {
            case 400 -> new IllegalArgumentException(message != null ? message : ILLEGAL_ARGUMENT_FEIGN);
            default -> new Exception(EXCEPTION_FEIGN);
        };
    }
}