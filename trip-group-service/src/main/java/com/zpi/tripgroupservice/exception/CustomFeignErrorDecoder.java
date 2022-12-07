package com.zpi.tripgroupservice.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import io.micrometer.core.instrument.util.IOUtils;
import org.json.JSONObject;

import java.io.IOException;

import static com.zpi.tripgroupservice.exception.ExceptionInfo.EXCEPTION_FEIGN;

public class CustomFeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        String message = null;
        try {
            String complexMessage = IOUtils.toString(response.body().asInputStream());
            JSONObject resultObject = new JSONObject(complexMessage);
            message  = resultObject.getString("message");

        } catch (Exception ignored) {
            throw new RuntimeException(EXCEPTION_FEIGN);
        }

        return switch (response.status()) {
            case 400 -> new IllegalArgumentException(message != null ? message : ExceptionInfo.ILLEGAL_ARGUMENT_FEIGN);
            default -> new Exception(EXCEPTION_FEIGN);
        };
    }
}