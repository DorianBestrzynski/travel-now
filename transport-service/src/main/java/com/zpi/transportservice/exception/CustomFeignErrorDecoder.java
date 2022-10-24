package com.zpi.transportservice.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import io.micrometer.core.instrument.util.IOUtils;
import org.json.JSONObject;

import java.io.IOException;

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
            case 400 -> new IllegalArgumentException(message != null ? message : ExceptionsInfo.ILLEGAL_ARGUMENT_FEIGN);
            default -> new Exception(ExceptionsInfo.EXCEPTION_FEIGN);
        };
    }
}