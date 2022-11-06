package com.zpi.apigateway.exceptions;

import java.util.Date;

public record ExceptionResponseModel(String errCode, String err, String errDetails, Object o, Date date) {
}
